/*
 * Copyright (c) 2024 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.element.android.libraries.matrix.impl.timeline

import io.element.android.libraries.matrix.api.core.EventId
import io.element.android.libraries.matrix.api.room.MatrixRoom
import io.element.android.libraries.matrix.api.timeline.MatrixTimelineItem
import io.element.android.libraries.matrix.api.timeline.ReceiptType
import io.element.android.libraries.matrix.api.timeline.Timeline
import io.element.android.libraries.matrix.api.timeline.TimelineException
import io.element.android.libraries.matrix.impl.timeline.item.event.EventMessageMapper
import io.element.android.libraries.matrix.impl.timeline.item.event.EventTimelineItemMapper
import io.element.android.libraries.matrix.impl.timeline.item.event.TimelineEventContentMapper
import io.element.android.libraries.matrix.impl.timeline.item.virtual.VirtualTimelineItemMapper
import io.element.android.libraries.matrix.impl.timeline.postprocessor.InvisibleIndicatorPostProcessor
import io.element.android.libraries.matrix.impl.timeline.postprocessor.LoadingIndicatorsPostProcessor
import io.element.android.libraries.matrix.impl.timeline.postprocessor.RoomBeginningPostProcessor
import io.element.android.libraries.matrix.impl.timeline.postprocessor.TimelineEncryptedHistoryPostProcessor
import io.element.android.services.toolbox.api.systemclock.SystemClock
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.matrix.rustcomponents.sdk.TimelineDiff
import org.matrix.rustcomponents.sdk.TimelineItem
import timber.log.Timber
import uniffi.matrix_sdk_ui.EventItemOrigin
import java.util.Date
import java.util.concurrent.atomic.AtomicBoolean
import org.matrix.rustcomponents.sdk.Timeline as InnerTimeline

private const val INITIAL_MAX_SIZE = 50
private const val PAGINATION_SIZE = 50

class RustTimeline(
    private val inner: InnerTimeline,
    private val isLive: Boolean,
    private val systemClock: SystemClock,
    private val roomCoroutineScope: CoroutineScope,
    private val isKeyBackupEnabled: Boolean,
    private val matrixRoom: MatrixRoom,
    private val dispatcher: CoroutineDispatcher,
    private val lastLoginTimestamp: Date?,
    private val fetchDetailsForEvent: suspend (EventId) -> Result<Unit>,
    private val onNewSyncedEvent: () -> Unit,
) : Timeline {

    private val initLatch = CompletableDeferred<Unit>()
    private val isInit = AtomicBoolean(false)

    private val _timelineItems: MutableStateFlow<List<MatrixTimelineItem>> =
        MutableStateFlow(emptyList())

    private val encryptedHistoryPostProcessor = TimelineEncryptedHistoryPostProcessor(
        lastLoginTimestamp = lastLoginTimestamp,
        isRoomEncrypted = matrixRoom.isEncrypted,
        isKeyBackupEnabled = isKeyBackupEnabled,
        dispatcher = dispatcher,
    )

    private val roomBeginningPostProcessor = RoomBeginningPostProcessor()
    private val loadingIndicatorsPostProcessor = LoadingIndicatorsPostProcessor(systemClock)
    private val invisibleIndicatorPostProcessor = InvisibleIndicatorPostProcessor(isLive)

    private val timelineItemFactory = MatrixTimelineItemMapper(
        fetchDetailsForEvent = fetchDetailsForEvent,
        roomCoroutineScope = roomCoroutineScope,
        virtualTimelineItemMapper = VirtualTimelineItemMapper(),
        eventTimelineItemMapper = EventTimelineItemMapper(
            contentMapper = TimelineEventContentMapper(
                eventMessageMapper = EventMessageMapper()
            )
        )
    )

    private val timelineDiffProcessor = MatrixTimelineDiffProcessor(
        timelineItems = _timelineItems,
        timelineItemFactory = timelineItemFactory,
    )

    private val backPaginationStatus = MutableStateFlow(
        Timeline.PaginationStatus(isPaginating = false, hasMoreToLoad = true)
    )

    private val forwardPaginationStatus = MutableStateFlow(
        Timeline.PaginationStatus(isPaginating = false, hasMoreToLoad = !isLive)
    )

    init {
        roomCoroutineScope.launch(dispatcher) {
            inner.timelineDiffFlow { initialList ->
                postItems(initialList)
            }.onEach { diffs ->
                if (diffs.any { diff -> diff.eventOrigin() == EventItemOrigin.SYNC }) {
                    onNewSyncedEvent()
                }
                postDiffs(diffs)
            }.launchIn(this)

            launch {
                fetchMembers()
            }
        }
    }

    override val membershipChangeEventReceived: Flow<Unit> = timelineDiffProcessor.membershipChangeEventReceived

    override suspend fun sendReadReceipt(eventId: EventId, receiptType: ReceiptType): Result<Unit> {
        return runCatching {
            inner.sendReadReceipt(receiptType.toRustReceiptType(), eventId.value)
        }
    }

    private fun updatePaginationStatus(direction: Timeline.PaginationDirection, update: (Timeline.PaginationStatus)->Timeline.PaginationStatus){
        when (direction) {
            Timeline.PaginationDirection.BACKWARDS -> backPaginationStatus.getAndUpdate(update)
            Timeline.PaginationDirection.FORWARDS -> forwardPaginationStatus.getAndUpdate(update)
        }
    }

    override suspend fun paginate(direction: Timeline.PaginationDirection): Result<Boolean> {
        initLatch.await()
        return runCatching {
            if (!canPaginate(direction)) throw TimelineException.CannotPaginate
            updatePaginationStatus(direction) { it.copy(isPaginating = true) }
            when (direction) {
                Timeline.PaginationDirection.BACKWARDS -> inner.paginateBackwards(PAGINATION_SIZE.toUShort())
                Timeline.PaginationDirection.FORWARDS -> inner.paginateForwards(PAGINATION_SIZE.toUShort())
            }
        }.onFailure { error ->
            updatePaginationStatus(direction) { it.copy(isPaginating = false) }
            if (error is CancellationException) {
                throw error
            }
            if (error is TimelineException.CannotPaginate) {
                Timber.d("Can't paginate $direction on room ${matrixRoom.roomId} with paginationStatus: ${backPaginationStatus.value}")
            } else {
                Timber.e(error, "Error paginating $direction on room ${matrixRoom.roomId}")
            }
        }.onSuccess { hasReachedEnd ->
            updatePaginationStatus(direction) { it.copy(isPaginating = false, hasMoreToLoad = !hasReachedEnd) }
        }
    }

    private fun canPaginate(direction: Timeline.PaginationDirection): Boolean {
        if (!isInit.get()) return false
        return when (direction) {
            Timeline.PaginationDirection.BACKWARDS -> backPaginationStatus.value.canPaginate
            Timeline.PaginationDirection.FORWARDS -> forwardPaginationStatus.value.canPaginate
        }
    }

    override fun paginationStatus(direction: Timeline.PaginationDirection): StateFlow<Timeline.PaginationStatus> {
        return when (direction) {
            Timeline.PaginationDirection.BACKWARDS -> backPaginationStatus
            Timeline.PaginationDirection.FORWARDS -> forwardPaginationStatus
        }
    }

    override val timelineItems: Flow<List<MatrixTimelineItem>> = combine(
        _timelineItems,
        backPaginationStatus.map { it.hasMoreToLoad }.distinctUntilChanged(),
        forwardPaginationStatus.map { it.hasMoreToLoad }.distinctUntilChanged(),
    ) { timelineItems, hasMoreToLoadBackward, hasMoreToLoadForward ->
        timelineItems
            .let { items -> encryptedHistoryPostProcessor.process(items) }
            .let { items ->
                roomBeginningPostProcessor.process(
                    items = items,
                    isDm = matrixRoom.isDm,
                    hasMoreToLoadBackwards = hasMoreToLoadBackward
                )
            }.let { items -> loadingIndicatorsPostProcessor.process(items, hasMoreToLoadBackward, hasMoreToLoadForward) }
            .let { items -> invisibleIndicatorPostProcessor.process(items) }

    }

    override fun close() {
        inner.close()
    }

    private suspend fun fetchMembers() = withContext(dispatcher) {
        initLatch.await()
        try {
            inner.fetchMembers()
        } catch (exception: Exception) {
            Timber.e(exception, "Error fetching members for room ${matrixRoom.roomId}")
        }
    }

    private suspend fun postItems(items: List<TimelineItem>) = coroutineScope {
        // Split the initial items in multiple list as there is no pagination in the cached data, so we can post timelineItems asap.
        items.chunked(INITIAL_MAX_SIZE).reversed().forEach {
            ensureActive()
            timelineDiffProcessor.postItems(it)
        }
        isInit.set(true)
        initLatch.complete(Unit)
    }

    private suspend fun postDiffs(diffs: List<TimelineDiff>) {
        initLatch.await()
        timelineDiffProcessor.postDiffs(diffs)
    }
}
