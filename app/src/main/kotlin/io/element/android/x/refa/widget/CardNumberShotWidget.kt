package com.drp.shared_ui.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import com.drp.refah.ui.theme.Red
import io.element.android.x.R
import com.drp.shared_ui.enums.Banks
import com.drp.shared_ui.model.CardShotItemInfo
import com.drp.shared_ui.model.SearchSheetItemModel
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.utils.panFormatter
import com.drp.utils.panMaskFormatter
import kotlinx.coroutines.delay


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CardNumberShotWidget(
    modifier: Modifier = Modifier,
    selectedCard: CardShotItemInfo?,
    cardsList: List<CardShotItemInfo> = emptyList(),
    onSelectedCardChange: (CardShotItemInfo?) -> Unit = {},
    onCardRemoved: (CardShotItemInfo) -> Unit = {},
    onCardSetToDefault: (CardShotItemInfo) -> Unit = {},
    onCardEdited: (CardShotItemInfo) -> Unit = {},
    onCardAdd: (CardShotItemInfo) -> Unit = {},
    isJustRefahSourceCardEnabled: Boolean = false,
    isCardNumberInFabMode: Boolean = false,
    openSelectedCardForEditBottomSheet: Boolean = false,
    openSelectedCardForEditCardShotItemInfo: CardShotItemInfo? = null,
    actionsAfterSelectedCardForEditInFabMode: (isSuccessEditOrInDismissMode: Boolean) -> Unit = {},
    horizontalPadding: Dp = dimensionResource(id = R.dimen.medium_padding),
    topPadding: Dp = dimensionResource(id = R.dimen.medium_padding)
) {
    /*var selectedCard by rememberSaveable {
        mutableStateOf<CardShotItemInfo?>(
            null
        )
    }*/
    var searchedCardsList by remember {
        mutableStateOf(emptyList<CardShotItemInfo>())
    }
    var cardListBottomSheetVisibility by remember {
        mutableStateOf(false)
    }
    var searchValue by remember {
        mutableStateOf(TextFieldValue())
    }
    var leadingIcon by remember {
        mutableStateOf<String?>(null)
    }
    var bankName by remember {
        mutableStateOf<String?>(null)
    }
    var trailingButtonVisibility by remember {
        mutableStateOf(false)
    }
    var selectedCardForOptions by remember {
        mutableStateOf<CardShotItemInfo?>(null)
    }
    var selectedCardForEdit by remember {
        mutableStateOf<CardShotItemInfo?>(null)
    }
    var showInfoPopUp by rememberSaveable {
        mutableStateOf(false)
    }
    var popUpLaunchedEffectExecuted by rememberSaveable {
        mutableStateOf(false)
    }
    var infoButtonClicked by rememberSaveable {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = cardsList) {
        val filteredCardList =
            if (isJustRefahSourceCardEnabled) cardsList.filter { it.isRefahSource } else cardsList
        if (filteredCardList.isNotEmpty()) {
            /** this if calls when the first time screen loads and selected card is null
             * if there was a default card the default one loads else the first card in the cards list*/
            if (selectedCard == null)
                filteredCardList.filter { it.default }.let {
                    onSelectedCardChange(
                        if (it.isNotEmpty()) it[0]
                        else filteredCardList[0]
                    )
                }
            else
            /** when card list changes and item inserted or updated, the selected card must update with correct data from db */
            {
                if (selectedCard.id == -1) {
                    filteredCardList.filter { it.pan == selectedCard.pan }.let {
                        if (it.isNotEmpty()) onSelectedCardChange(it[0])
                    }
                } else
                    filteredCardList.filter { it.id == selectedCard.id }.let {
                        if (it.isNotEmpty()) onSelectedCardChange(it[0])
                    }
            }
        }
        /** any modifications in cards list must like remove must update the card list bottom sheet items */
        searchedCardsList = filteredCardList
    }

    LaunchedEffect(key1 = Unit) {
        if (isJustRefahSourceCardEnabled && !popUpLaunchedEffectExecuted) {
            delay(500)
            showInfoPopUp = true
            delay(3000)
            showInfoPopUp = false
            popUpLaunchedEffectExecuted = true
        }
    }

    LaunchedEffect(key1 = infoButtonClicked) {
        if (infoButtonClicked) {
            delay(100) // Adjust delay as needed
            showInfoPopUp = !showInfoPopUp
            infoButtonClicked = false
        }
    }

    /** this boolean in for opening edit bottom sheet straightly from outside the CardNumberShot */
    LaunchedEffect(key1 = openSelectedCardForEditBottomSheet) {
        if (openSelectedCardForEditBottomSheet) {
            selectedCardForEdit = openSelectedCardForEditCardShotItemInfo
        }
    }

    if (!isCardNumberInFabMode)
        ElevatedCard(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding)
                .padding(top = topPadding),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = dimensionResource(id = R.dimen.small_elevation)
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Row(
                modifier = Modifier.clickable {
                    cardListBottomSheetVisibility = true
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(modifier = Modifier.padding(
                    dimensionResource(id = R.dimen.small_padding)
                ), onClick = { cardListBottomSheetVisibility = true }) {

                    Icon(
                        modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                        painter = painterResource(id = findImageResources(selectedCard?.bankIcon)),
                        contentDescription = "",
                        tint = Color.Unspecified
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        modifier = Modifier,
                        text = selectedCard?.let {
                            panFormatter(
                                it.pan, " "
                            )
                        } ?: "**** **** **** ****",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold, textDirection = TextDirection.Ltr
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = selectedCard?.bankName ?: stringResource(id = R.string.add_card_st),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.W200),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (isJustRefahSourceCardEnabled) {
                    Box(modifier = Modifier.wrapContentWidth()) {
                        IconButton(modifier = Modifier.padding(
                            dimensionResource(id = R.dimen.small_padding)
                        ), onClick = {
                            infoButtonClicked = true
                        }) {
                            Icon(modifier = Modifier.size(20.dp),
                                painter = painterResource(id = selectedCard?.let {
                                    R.drawable.ic_info_card
                                } ?: R.drawable.ic_add_round),
                                contentDescription = "",
                                tint = selectedCard?.let { Red.copy(alpha = 0.9f) }
                                    ?: MaterialTheme.colorScheme.onSurface)
                        }
                        PopupBox(showPopup = showInfoPopUp) {
                            infoButtonClicked = true
                        }
                    }
                } else {
                    IconButton(modifier = Modifier.padding(
                        dimensionResource(id = R.dimen.small_padding)
                    ), onClick = { cardListBottomSheetVisibility = true }) {
                        Icon(modifier = Modifier.size(16.dp),
                            painter = painterResource(id = selectedCard?.let {
                                R.drawable.ic_double_arrow_down
                            } ?: R.drawable.ic_add_round),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    else
        FloatingActionButton(
            modifier = modifier,
            onClick = {
                cardListBottomSheetVisibility = true
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = dimensionResource(
                    id = R.dimen.medium_elevation
                )
            )
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }



    CardListBottomSheet(
        visibility = cardListBottomSheetVisibility,
        searchValue = searchValue,
        onSearchValueChange = { searchValue = it },
        isJustRefahSourceCardEnabled = isJustRefahSourceCardEnabled,
        leadingIconName = leadingIcon,
        onLeadingIconChange = { leadingIcon = it },
        bankName = bankName,
        onBankNameChange = { bankName = it },
        trailingButtonVisibility = trailingButtonVisibility,
        onTrailingButtonVisibilityChange = { trailingButtonVisibility = it },
        searchedCardsList = searchedCardsList,
        onSearchedCardsListChange = { searchedCardsList = it },
        cardsList = if (isJustRefahSourceCardEnabled) cardsList.filter { it.isRefahSource } else cardsList,
        onSelectedCardChange = onSelectedCardChange,
        onSelectedCardForOptionsChange = { selectedCardForOptions = it },
        backToDefault = {
            searchValue = searchValue.copy(text = "")
            leadingIcon = null
            trailingButtonVisibility = false
            bankName = null
            searchedCardsList =
                if (isJustRefahSourceCardEnabled) cardsList.filter { it.isRefahSource } else cardsList
        },
        isCardNumberInFabMode = isCardNumberInFabMode,
        onCardAdd = onCardAdd,
        dismiss = { cardListBottomSheetVisibility = false }
    )

    CardOptionsBottomSheet(
        visibility = selectedCardForOptions != null,
        clickedCardShotItemInfo = selectedCardForOptions,
        selectedCardShotItemInfo = selectedCard,
        backToDefault = {
            leadingIcon = null
            trailingButtonVisibility = false
            bankName = null
            searchValue = searchValue.copy(text = "")
        },
        clearSelectedCard = {
            onSelectedCardChange(null)
        },
        onCardRemoved = onCardRemoved,
        setCardToEdit = {
            selectedCardForEdit = it
        },
        onCardSetToDefault = onCardSetToDefault,
        dismiss = { selectedCardForOptions = null })

    CardEditBottomSheet(
        visibility = selectedCardForEdit != null,
        selectedCardForEdit = selectedCardForEdit,
        onCardEdited = onCardEdited,
        isJustRefahSourceCardEnabled = isJustRefahSourceCardEnabled,
        isCardNumberInFabMode = isCardNumberInFabMode,
        actionsAfterSelectedCardForEditInFabMode = actionsAfterSelectedCardForEditInFabMode,
        dismiss = { selectedCardForEdit = null })
}

@Composable
fun PopupBox(showPopup: Boolean, onClickOutside: () -> Unit) {
    AnimatedVisibility(visible = showPopup) {
        Box(
            modifier = Modifier
                .zIndex(10F),
            contentAlignment = Alignment.TopCenter
        ) {
            Popup(
                alignment = Alignment.Center,
                properties = PopupProperties(
                    excludeFromSystemGesture = true
                ),
                onDismissRequest = { onClickOutside() }
            ) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(25))
                        .padding(horizontal = dimensionResource(id = R.dimen.large_padding))
                        .background(Red.copy(alpha = 0.9f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.padding(
                            horizontal = dimensionResource(id = R.dimen.small_padding),
                            vertical = 4.dp
                        ),
                        text = stringResource(id = R.string.just_refah_card_supported),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun CardListBottomSheet(
    modifier: Modifier = Modifier,
    visibility: Boolean,
    searchValue: TextFieldValue,
    onSearchValueChange: (TextFieldValue) -> Unit,
    isJustRefahSourceCardEnabled: Boolean,
    leadingIconName: String?,
    onLeadingIconChange: (String?) -> Unit,
    bankName: String?,
    onBankNameChange: (String?) -> Unit,
    trailingButtonVisibility: Boolean,
    onTrailingButtonVisibilityChange: (Boolean) -> Unit,
    searchedCardsList: List<CardShotItemInfo>,
    onSearchedCardsListChange: (List<CardShotItemInfo>) -> Unit,
    cardsList: List<CardShotItemInfo>,
    onSelectedCardChange: (CardShotItemInfo?) -> Unit,
    onSelectedCardForOptionsChange: (CardShotItemInfo?) -> Unit,
    backToDefault: () -> Unit,
    isCardNumberInFabMode: Boolean,
    onCardAdd: (CardShotItemInfo) -> Unit,
    dismiss: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    DynamicSheet(
        isVisible = visibility,
        onDismiss = {
            backToDefault()
            dismiss()
        },
        title = stringResource(id = if (!isCardNumberInFabMode) R.string.card_selection_title else R.string.add_card_st),
        icon = R.drawable.ic_card_shot,
    ) {
        OutlinedTextField(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
            .padding(top = dimensionResource(id = R.dimen.medium_padding)),
            value = searchValue,
            onValueChange = { value ->
                val formatted = panFormatter(value.text, " ")
                if (formatted.length >= 7) {
                    Banks.entries.filter {
                        it.cardNumberPrefix == formatted.substring(0, 7).filter { it.isDigit() }
                    }.let { enumList ->
                        if (enumList.isNotEmpty()) {
                            if (!isJustRefahSourceCardEnabled) {
                                onBankNameChange(enumList[0].persianName)
                                onLeadingIconChange(enumList[0].bankIcon)
                            } else {
                                if (enumList[0].bankIcon == Banks.REFAH.bankIcon) {
                                    onBankNameChange(enumList[0].persianName)
                                    onLeadingIconChange(enumList[0].bankIcon)
                                } else {
                                    onBankNameChange(null)
                                    onLeadingIconChange(null)
                                }
                            }
                        } else {
                            onBankNameChange(null)
                            onLeadingIconChange(null)
                        }
                    }
                } else {
                    onBankNameChange(null)
                    onLeadingIconChange(null)
                }

                onTrailingButtonVisibilityChange(formatted.length == 19 && searchedCardsList.none {
                    it.pan == formatted.filter { it.isDigit() }
                } && leadingIconName != null)

                onSearchedCardsListChange(cardsList.filter { it.pan.contains(formatted.filter { it.isDigit() }) })

                onSearchValueChange(
                    value.copy(
                        text = formatted, selection = TextRange(formatted.length)
                    )
                )
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center, textDirection = TextDirection.Ltr
            ),
            label = {
                Text(
                    text = stringResource(id = R.string.card_number_st),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(0.7f)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
            ),
            shape = RoundedCornerShape(percent = 50),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(onAny = {
                keyboardController?.hide()
            }),
            singleLine = true,
            isError = leadingIconName == null && searchValue.text.length >= 7,
            supportingText = {
                if (leadingIconName == null && searchValue.text.length >= 7)
                    Text(
                        text = if (!isJustRefahSourceCardEnabled) stringResource(id = R.string.not_supported_card_st) else stringResource(
                            id = R.string.just_refah_card_supported
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        modifier = Modifier.alpha(0.9f)
                    )
                else if (searchValue.text.length == 19 && !searchedCardsList.none {
                        it.pan == searchValue.text.filter { it.isDigit() }
                    } && leadingIconName != null && isCardNumberInFabMode)
                    Text(
                        text = stringResource(id = R.string.card_added_already_st),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        modifier = Modifier.alpha(0.9f)
                    )
            },
            leadingIcon = if (leadingIconName != null) {
                @Composable {
                    IconButton(modifier = Modifier.padding(
                        dimensionResource(id = R.dimen.small_padding)
                    ), onClick = { }) {
                        Icon(
                            modifier = Modifier.size(16.dp), painter = painterResource(
                                id = findImageResources(imageIcon = leadingIconName)
                            ), contentDescription = "", tint = Color.Unspecified
                        )
                    }
                }
            } else null,
            trailingIcon = if (trailingButtonVisibility) {
                @Composable {
                    OutlinedButton(modifier = Modifier.padding(end = dimensionResource(id = R.dimen.medium_padding)),
                        border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.primary),
                        onClick = {
                            bankName?.let {
                                val cardShot = CardShotItemInfo(
                                    pan = searchValue.text.filter { it.isDigit() },
                                    bankIcon = leadingIconName.toString(),
                                    bankName = bankName,
                                    isRefahSource = searchValue.text.substring(
                                        0, 7
                                    ).filter { it.isDigit() } == Banks.REFAH.cardNumberPrefix,
                                )
                                onSelectedCardChange(cardShot)
                                onCardAdd(cardShot)
                                backToDefault()
                                dismiss()
                            }
                        }) {
                        Text(
                            text = stringResource(id = R.string.add_st),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall.copy(
                                textAlign = TextAlign.Center, textDirection = TextDirection.Ltr
                            ),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            } else null)
        if (!isCardNumberInFabMode)
            LazyColumn(modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.medium_padding))) {
                items(items = searchedCardsList, key = { it.id }) { cardShotItemInfo ->
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
                        .padding(bottom = dimensionResource(id = R.dimen.medium_padding))
                        .border(
                            border = BorderStroke(
                                width = 1.dp, color = MaterialTheme.colorScheme.outline
                            ), shape = RoundedCornerShape(percent = 50)
                        )
                        .clickable {
                            onSelectedCardChange(cardShotItemInfo)
                            backToDefault()
                            dismiss()
                        }
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(percent = 50)
                        ), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(modifier = Modifier.padding(
                            dimensionResource(id = R.dimen.small_padding)
                        ), onClick = { }) {
                            Icon(
                                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                                painter = painterResource(
                                    id = findImageResources(
                                        imageIcon = cardShotItemInfo.bankIcon
                                    )
                                ),
                                contentDescription = "",
                                tint = Color.Unspecified
                            )
                        }
                        Text(
                            modifier = Modifier.weight(1f),
                            text = panFormatter(
                                cardShotItemInfo.pan, " "
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall.copy(
                                textAlign = TextAlign.Center, textDirection = TextDirection.Ltr
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        if (cardShotItemInfo.default) {
                            Icon(
                                modifier = Modifier
                                    .size(16.dp)
                                    .wrapContentWidth(),
                                painter = painterResource(id = R.drawable.ic_default_card),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(modifier = Modifier.padding(
                            dimensionResource(id = R.dimen.small_padding)
                        ), onClick = {
                            onSelectedCardForOptionsChange(cardShotItemInfo)
                        }) {
                            Icon(
                                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

    }
}

@Composable
fun CardOptionsBottomSheet(
    modifier: Modifier = Modifier,
    visibility: Boolean,
    clickedCardShotItemInfo: CardShotItemInfo?,
    selectedCardShotItemInfo: CardShotItemInfo?,
    backToDefault: () -> Unit,
    clearSelectedCard: () -> Unit,
    onCardRemoved: (CardShotItemInfo) -> Unit,
    onCardSetToDefault: (CardShotItemInfo) -> Unit,
    setCardToEdit: (CardShotItemInfo) -> Unit,
    dismiss: () -> Unit
) {
    val itemsList =
        arrayListOf(stringResource(id = R.string.edit), stringResource(id = R.string.delete))
    if (clickedCardShotItemInfo?.default == false) itemsList.add(stringResource(id = R.string.set_default))
    DynamicSheet(
        isVisible = visibility,
        onDismiss = dismiss,
        title = stringResource(id = R.string.card_modification_sheet_title),
        icon = R.drawable.ic_card_shot,
    ) {
        LazyColumn {
            itemsIndexed(items = itemsList) { index, title ->
                Card(shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    onClick = {
                        when (index) {
                            0 -> clickedCardShotItemInfo?.let {
                                dismiss()
                                setCardToEdit(it)
                            }

                            1 -> clickedCardShotItemInfo?.let {
                                if (it.id == selectedCardShotItemInfo?.id) {
                                    clearSelectedCard()
                                }
                                backToDefault()
                                onCardRemoved(it)
                                dismiss()
                            }

                            else -> clickedCardShotItemInfo?.let {
                                onCardSetToDefault(it)
                                dismiss()
                            }
                        }
                    }) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
                            .padding(
                                vertical = dimensionResource(
                                    id = R.dimen.medium_padding
                                )
                            ),
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (index != itemsList.lastIndex) CustomLine(
                        modifier = Modifier.padding(
                            horizontal = dimensionResource(id = R.dimen.medium_padding)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun CardEditBottomSheet(
    modifier: Modifier = Modifier,
    visibility: Boolean,
    selectedCardForEdit: CardShotItemInfo?,
    isJustRefahSourceCardEnabled: Boolean,
    onCardEdited: (CardShotItemInfo) -> Unit,
    isCardNumberInFabMode: Boolean,
    actionsAfterSelectedCardForEditInFabMode: (isSuccessEditOrInDismissMode: Boolean) -> Unit,
    dismiss: () -> Unit
) {
    var cardNoValue by remember {
        mutableStateOf(TextFieldValue())
    }
    var bankName by remember {
        mutableStateOf<String?>(null)
    }
    var leadingIconName by remember {
        mutableStateOf<String?>(null)
    }
    var trailingButtonVisibility by remember {
        mutableStateOf(false)
    }
    val months =
        stringArrayResource(id = R.array.month).toList()
    val years =
        stringArrayResource(id = R.array.years).toList()
    var monthSelectionSheet by remember {
        mutableStateOf(false)
    }
    var yearSelectionSheet by remember {
        mutableStateOf(false)
    }
    var selectedMonth by remember {
        mutableStateOf("")
    }
    var selectedYear by remember {
        mutableStateOf("")
    }
    SideEffect {
        cardNoValue = cardNoValue.copy(text = panFormatter(selectedCardForEdit?.pan, " "))
        bankName = selectedCardForEdit?.bankName.toString()
        leadingIconName = selectedCardForEdit?.bankIcon
        trailingButtonVisibility = true
        selectedMonth = selectedCardForEdit?.panExpiryMonth ?: ""
        selectedYear = if (!selectedCardForEdit?.panExpiryYear.isNullOrEmpty()) years.first {
            it.contains(selectedCardForEdit?.panExpiryYear!!)
        } else ""
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    DynamicSheet(
        isVisible = visibility,
        onDismiss = {
            dismiss()
            if (isCardNumberInFabMode)
                actionsAfterSelectedCardForEditInFabMode(false)
        },
        title = stringResource(id = R.string.card_modification_sheet_title),
        icon = R.drawable.ic_card_shot,
    ) {
        Column {
            OutlinedTextField(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen.medium_padding))
                .padding(top = dimensionResource(id = R.dimen.medium_padding)),
                value = cardNoValue,
                onValueChange = { value ->
                    val formatted = panFormatter(value.text, " ")
                    if (formatted.length >= 7) {
                        Banks.entries.filter {
                            it.cardNumberPrefix == formatted.substring(0, 7).filter { it.isDigit() }
                        }.let { enumList ->
                            if (enumList.isNotEmpty()) {
                                if (!isJustRefahSourceCardEnabled) {
                                    bankName = enumList[0].persianName
                                    leadingIconName = enumList[0].bankIcon
                                } else {
                                    if (enumList[0].bankIcon == Banks.REFAH.bankIcon) {
                                        bankName = enumList[0].persianName
                                        leadingIconName = enumList[0].bankIcon
                                    } else {
                                        bankName = null
                                        leadingIconName = null
                                    }
                                }
                            } else {
                                bankName = null
                                leadingIconName = null
                            }
                        }
                    } else {
                        bankName = null
                        leadingIconName = null
                    }
                    trailingButtonVisibility =
                        formatted.length == 19 && leadingIconName != null /*&& selectedCardForEdit?.pan != formatted.filter { it.isDigit() }*/
                    cardNoValue =
                        value.copy(
                            text = formatted, selection = TextRange(formatted.length)
                        )
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Center, textDirection = TextDirection.Ltr
                ),
                label = {
                    Text(
                        text = stringResource(id = R.string.card_number_st),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.alpha(0.7f)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                ),
                shape = RoundedCornerShape(percent = 50),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(onAny = {
                    keyboardController?.hide()
                }),
                singleLine = true,
                isError = leadingIconName == null && cardNoValue.text.length >= 7,
                supportingText = {
                    if (leadingIconName == null && cardNoValue.text.length >= 7)
                        Text(
                            text = if (!isJustRefahSourceCardEnabled) stringResource(id = R.string.not_supported_card_st) else stringResource(
                                id = R.string.just_refah_card_supported
                            ),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            modifier = Modifier.alpha(0.9f)
                        )
                },
                leadingIcon = if (leadingIconName != null) {
                    @Composable {
                        IconButton(modifier = Modifier.padding(
                            dimensionResource(id = R.dimen.small_padding)
                        ), onClick = { }) {
                            Icon(
                                modifier = Modifier.size(16.dp), painter = painterResource(
                                    id = findImageResources(imageIcon = leadingIconName)
                                ), contentDescription = "", tint = Color.Unspecified
                            )
                        }
                    }
                } else null,
                trailingIcon = if (trailingButtonVisibility) {
                    @Composable {
                        OutlinedButton(modifier = Modifier.padding(end = dimensionResource(id = R.dimen.medium_padding)),
                            border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.primary),
                            onClick = {
                                selectedCardForEdit?.let { selectedCard ->
                                    bankName?.let {
                                        onCardEdited(
                                            selectedCard.copy(
                                                pan = cardNoValue.text.filter { it.isDigit() },
                                                bankName = it,
                                                bankIcon = leadingIconName.toString(),
                                                maskedPan = panMaskFormatter(cardNoValue.text.filter { it.isDigit() }),
                                                isRefahSource = cardNoValue.text.substring(0, 7)
                                                    .filter { it.isDigit() }
                                                        == Banks.REFAH.cardNumberPrefix,
                                                panExpiryMonth = selectedMonth.ifEmpty { null },
                                                panExpiryYear = if (selectedYear.isNotEmpty()) selectedYear.substring(2,4) else null
                                            )
                                        )
                                        if (isCardNumberInFabMode)
                                            actionsAfterSelectedCardForEditInFabMode(true)
                                        dismiss()
                                    }
                                }
                            }) {
                            Text(
                                text = stringResource(id = R.string.edit),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    textAlign = TextAlign.Center, textDirection = TextDirection.Ltr
                                ),
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                } else null)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen.extra_large_padding))
                .padding(top = dimensionResource(id = R.dimen.small_padding))
        ) {
            Combo(
                modifier = Modifier.weight(0.5f),
                mValue = selectedMonth,
                onClick = { monthSelectionSheet = true },
                onValueChange = {
                    selectedMonth = it
                },
                isLoading = false,
                label = stringResource(id = R.string.month),
                placeHolder = stringResource(id = R.string.month)
            )
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.medium_padding)))
            Combo(
                modifier = Modifier.weight(0.5f),
                mValue = selectedYear,
                onClick = { yearSelectionSheet = true },
                onValueChange = {
                    selectedYear = it
                },
                isLoading = false,
                label = stringResource(id = R.string.year),
                placeHolder = stringResource(id = R.string.year),
            )
        }

        SearchSheet(sheetVisible = monthSelectionSheet,
            onDismiss = { monthSelectionSheet = false },
            items = months.map { monthStr ->
                SearchSheetItemModel(value = monthStr)
            },
            onItemClick = { itemModel ->
                selectedMonth = itemModel.value
                monthSelectionSheet = false
            })
        SearchSheet(sheetVisible = yearSelectionSheet,
            onDismiss = { yearSelectionSheet = false },
            items = years.map { yearStr ->
                SearchSheetItemModel(value = yearStr)
            },
            onItemClick = { itemModel ->
                selectedYear = itemModel.value
                yearSelectionSheet = false
            })
    }
}

@Composable
fun findImageResources(imageIcon: String?): Int {
    val context = LocalContext.current
    return context.resources.getIdentifier(
        imageIcon ?: "ic_undefined", "drawable", context.packageName
    )
}


@Composable
private fun CardNumberShotPreview() {
    ApplicationTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            CardNumberShotWidget(selectedCard = null, onSelectedCardChange = {})
        }
    }
}
