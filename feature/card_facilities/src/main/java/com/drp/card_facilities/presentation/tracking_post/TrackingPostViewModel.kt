package com.drp.card_facilities.presentation.tracking_post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drp.card_facilities.R
import com.drp.data.network.RequestState
import com.drp.data.network.toRequestState
import com.drp.data.repository.CardFacilitiesRepository
import com.drp.shared_ui.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackingPostViewModel @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private var cardFacilitiesRepository: CardFacilitiesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TrackingPostScreenState())
    val uiState: StateFlow<TrackingPostScreenState> = _uiState

    private val errorChannel = Channel<UiText>()
    val errors = errorChannel.receiveAsFlow()

    private fun validateFields(): Boolean {
        if (uiState.value.trackingNumber.length < 24) {
            _uiState.value =
                _uiState.value.copy(
                    trackingNumberValidationMessage = UiText.StringResource(
                        R.string.tracking_post_number_error_st
                    )
                )
            return false
        }
        return true
    }

    fun inquiry() {
        if (!validateFields())
            return
        viewModelScope.launch(dispatcher) {
            _uiState.value = _uiState.value.copy(inquiry = RequestState.Loading)
            cardFacilitiesRepository.trackingPostInquiry(
                trackingNumber = uiState.value.trackingNumber
            ).collectLatest {
                it.toRequestState().let { response ->
                    if (response.isError()) {
                        showError(UiText.StringResource(R.string.tracking_post_inquiry_error_st))
                    }
                    if (response.isSuccess()) {
                        if (response.getSuccessData().parameters == null) {
                            showError(UiText.DynamicString(response.getSuccessData().status.description))
                            _uiState.value = _uiState.value.copy(inquiry = RequestState.Idle)
                            return@collectLatest
                        }
                    }
                    _uiState.value = _uiState.value.copy(inquiry = response)
                }
            }
        }
    }

    fun setTrackingNumber(trackingNumber: String) {
        _uiState.value = _uiState.value.copy(trackingNumber = trackingNumber)
    }

    fun dismissTrackingNumberValidationMessage() {
        _uiState.value =
            _uiState.value.copy(trackingNumberValidationMessage = UiText.DynamicString(""))
    }

    fun dismissFailureDialog() {
        if (_uiState.value.inquiry.isFail())
            _uiState.value = _uiState.value.copy(inquiry = RequestState.Idle)
    }

    fun dismissInquiry() {
        _uiState.value = _uiState.value.copy(inquiry = RequestState.Idle)
    }

    fun showError(message: UiText) {
        viewModelScope.launch(dispatcher) {
            errorChannel.send(message)
        }
    }
}