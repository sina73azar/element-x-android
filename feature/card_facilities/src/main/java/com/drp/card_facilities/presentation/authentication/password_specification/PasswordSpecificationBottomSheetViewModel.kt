package com.drp.card_facilities.presentation.authentication.password_specification

import androidx.lifecycle.ViewModel
import com.drp.card_facilities.R
import com.drp.data.repository.CardFacilitiesUserRepository
import com.drp.shared_ui.enums.AuthenticationType
import com.drp.shared_ui.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PasswordBottomSheetViewModel @Inject constructor(
    private val userRepository: CardFacilitiesUserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PasswordSpecificaionBottomSheetState())
    val uiState: StateFlow<PasswordSpecificaionBottomSheetState>
        get() = _uiState

    private fun savePassword() {
        if (_uiState.value.password.isEmpty()) {
            _uiState.value =
                _uiState.value.copy(passwordValidationMessage = UiText.StringResource(R.string.password_validation_st))
            return
        }
        if (_uiState.value.passwordRepeat.isEmpty() || _uiState.value.password != _uiState.value.passwordRepeat) {
            _uiState.value =
                _uiState.value.copy(passwordRepeatValidationMessage = UiText.StringResource(R.string.repeat_password_validation_st))
            return
        }
        userRepository.setPassword(_uiState.value.password)
        userRepository.setAuthenticationType(AuthenticationType.PASSWORD)
        _uiState.value = _uiState.value.copy(isPasswordSaveActionCompleted = true)
    }

    private fun setPassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    private fun dumpPasswordValidationMessage() {
        _uiState.value = _uiState.value.copy(passwordValidationMessage = UiText.DynamicString(""))
    }

    private fun setPasswordRepeat(passwordRepeat: String) {
        _uiState.value = _uiState.value.copy(passwordRepeat = passwordRepeat)
    }

    private fun dumpPasswordRepeatValidationMessage() {
        _uiState.value =
            _uiState.value.copy(passwordRepeatValidationMessage = UiText.DynamicString(""))
    }

    private fun backToDefault() {
        _uiState.value = PasswordSpecificaionBottomSheetState()
    }

    fun sendEvent(event: PasswordSpecificationBottomSheetEvents) {
        when (event) {
            is PasswordSpecificationBottomSheetEvents.SetPasswordSpecification -> {
                setPassword(event.password)
            }

            is PasswordSpecificationBottomSheetEvents.DumpPasswordValidationMessageSpecification -> {
                dumpPasswordValidationMessage()
            }

            is PasswordSpecificationBottomSheetEvents.SetPasswordRepeatSpecification -> {
                setPasswordRepeat(event.passwordRepeat)
            }

            is PasswordSpecificationBottomSheetEvents.DumpPasswordRepeatValidationMessageSpecification -> {
                dumpPasswordRepeatValidationMessage()
            }

            is PasswordSpecificationBottomSheetEvents.SavePasswordSpecification -> {
                savePassword()
            }

            is PasswordSpecificationBottomSheetEvents.BackToDefault -> {
                backToDefault()
            }
        }
    }
}

sealed class PasswordSpecificationBottomSheetEvents {
    data class SetPasswordSpecification(val password: String) : PasswordSpecificationBottomSheetEvents()
    data object DumpPasswordValidationMessageSpecification : PasswordSpecificationBottomSheetEvents()
    data class SetPasswordRepeatSpecification(val passwordRepeat: String) : PasswordSpecificationBottomSheetEvents()
    data object DumpPasswordRepeatValidationMessageSpecification : PasswordSpecificationBottomSheetEvents()
    data object SavePasswordSpecification : PasswordSpecificationBottomSheetEvents()
    data object BackToDefault : PasswordSpecificationBottomSheetEvents()
}