package com.drp.card_facilities.presentation.authentication.password_check

import androidx.lifecycle.ViewModel
import com.drp.card_facilities.R
import com.drp.data.repository.CardFacilitiesUserRepository
import com.drp.shared_ui.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PasswordCheckBottomSheetViewModel @Inject constructor(
    private val userRepository: CardFacilitiesUserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PasswordCheckBottomSheetState())
    val uiState: StateFlow<PasswordCheckBottomSheetState>
        get() = _uiState

    private fun checkPassword() {
        if (!userRepository.checkPasswordMatch(_uiState.value.password)) {
            _uiState.value =
                _uiState.value.copy(passwordValidationMessage = UiText.StringResource(R.string.password_validation_st))
            return
        }
        _uiState.value = _uiState.value.copy(isPasswordCheckActionCompleted = true)
    }

    private fun setPassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    private fun dumpPasswordValidationMessage() {
        _uiState.value = _uiState.value.copy(passwordValidationMessage = UiText.DynamicString(""))
    }

    private fun backToDefault() {
        _uiState.value = PasswordCheckBottomSheetState()
    }

    fun sendEvent(event: PasswordCheckBottomSheetEvents) {
        when (event) {
            is PasswordCheckBottomSheetEvents.SetPassword -> {
                setPassword(event.password)
            }

            is PasswordCheckBottomSheetEvents.DumpPasswordValidationMessage -> {
                dumpPasswordValidationMessage()
            }

            is PasswordCheckBottomSheetEvents.CheckPassword -> {
                checkPassword()
            }

            is PasswordCheckBottomSheetEvents.BackToDefault -> {
                backToDefault()
            }
        }
    }
}

sealed class PasswordCheckBottomSheetEvents {
    data class SetPassword(val password: String) : PasswordCheckBottomSheetEvents()
    data object DumpPasswordValidationMessage : PasswordCheckBottomSheetEvents()
    data object CheckPassword : PasswordCheckBottomSheetEvents()
    data object BackToDefault : PasswordCheckBottomSheetEvents()
}