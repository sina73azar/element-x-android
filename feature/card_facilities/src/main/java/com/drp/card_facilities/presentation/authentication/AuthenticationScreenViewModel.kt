package com.drp.card_facilities.presentation.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drp.data.repository.CardFacilitiesUserRepository
import com.drp.data.model.FacilityServiceItem
import com.drp.shared_ui.enums.AuthenticationType
import com.drp.shared_ui.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationScreenViewModel @Inject constructor(
    private val userRepository: CardFacilitiesUserRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthenticationScreenState())
    val uiState: StateFlow<AuthenticationScreenState>
        get() = _uiState

    private val _errorChannel = Channel<UiText>()
    val errors = _errorChannel.receiveAsFlow()

    private fun addServiceAuthenticationId(id: Int) {
        userRepository.addServiceAuthenticationId(id)
    }

    private fun removeServiceAuthenticationId(id: Int) {
        userRepository.removeServiceAuthenticationId(id)
    }

    private fun changeAuthType(authType: AuthenticationType) {

        val previousAuthType = userRepository.getAuthenticationType()

        when (authType) {
            AuthenticationType.PASSWORD -> {
                if (previousAuthType == AuthenticationType.NONE) {
                    // done
                    changePasswordSpecificationBottomSheetVisibility(true)
                    uiState.value.actionType = ActionType.EnablePass
                } else {
                    changeBiometricPromptVisibility(true)
                    uiState.value.actionType = ActionType.DisableBio_EnablePass
                }
            }

            AuthenticationType.BIOMETRIC -> {
                if (previousAuthType == AuthenticationType.NONE) {
                    // done
                    changeBiometricPromptVisibility(true)
                    uiState.value.actionType = ActionType.EnableBio
                } else {
                    changePasswordCheckBottomSheetVisibility(true)
                    uiState.value.actionType = ActionType.DisablePass_EnableBio
                }
            }

            AuthenticationType.NONE -> {
                if (previousAuthType == AuthenticationType.PASSWORD) {
                    // done
                    changePasswordCheckBottomSheetVisibility(true)
                    uiState.value.actionType = ActionType.DisablePass
                } else {
                    // done
                    changeBiometricPromptVisibility(true)
                    uiState.value.actionType = ActionType.DisableBio
                }

                // TODO do below codes after deactivation
                /*userRepository.setAuthenticationType(authType)
                userRepository.clearServiceAuthenticationList()
                setItemsAuthenticationEnabled()*/
            }
        }
    }

    private fun changeBiometricPromptVisibility(visibility: Boolean) {
        _uiState.value = _uiState.value.copy(biometricPromptVisibility = visibility)
    }

    private fun changeAuthenticationInLocal(authType: AuthenticationType) {
        userRepository.setAuthenticationType(authType)
        setSelectedAuthType(authType)
        if (authType == AuthenticationType.NONE) {
            userRepository.clearServiceAuthenticationList()
            setItemsAuthenticationEnabled()
        }
    }

    private fun setBiometricPromptVisibility(visibility: Boolean) {
        _uiState.value =
            _uiState.value.copy(biometricPromptVisibility = visibility)
    }

    private fun changePasswordSpecificationBottomSheetVisibility(visibility: Boolean) {
        _uiState.value =
            _uiState.value.copy(passwordSpecificationBottomSheetVisibility = visibility)
    }

    private fun changePasswordCheckBottomSheetVisibility(visibility: Boolean) {
        _uiState.value =
            _uiState.value.copy(passwordCheckBottomSheetVisibility = visibility)
    }

    private fun setSelectedAuthType(authType: AuthenticationType) {
        _uiState.value = _uiState.value.copy(selectedAuthType = authType)
    }

    private fun addItems(facilityServiceItem: FacilityServiceItem) {
        _uiState.value.items.add(facilityServiceItem)
    }

    private fun setItemsAuthenticationEnabled() {
        userRepository.getAuthenticationType().let {
            setSelectedAuthType(it)
            if (it != AuthenticationType.NONE) {
                val ids = userRepository.getAuthenticationServiceIds()
                _uiState.value.items.forEachIndexed { index, facilityServiceItem ->
                    if (ids.contains(facilityServiceItem.id))
                        _uiState.value.items[index] =
                            facilityServiceItem.copy(authenticationEnabled = true)
                }
            } else {
                _uiState.value.items.forEachIndexed { index, facilityServiceItem ->
                    _uiState.value.items[index] =
                        facilityServiceItem.copy(authenticationEnabled = false)
                }
            }
        }
    }

    fun sendEvent(event: AuthenticationScreenEvents) {
        when (event) {
            is AuthenticationScreenEvents.AddServiceIdToAuthList -> {
                addServiceAuthenticationId(event.id)
            }

            is AuthenticationScreenEvents.RemoveServiceIdFromAuthList -> {
                removeServiceAuthenticationId(event.id)
            }

            is AuthenticationScreenEvents.SetSelectedAuthenticationType -> {
                setSelectedAuthType(event.authType)
            }

            is AuthenticationScreenEvents.ChangeAuthenticationType -> {
                changeAuthType(event.authType)
            }

            is AuthenticationScreenEvents.AddItems -> {
                addItems(event.facilityServiceItem)
            }

            is AuthenticationScreenEvents.SetItemsAuthenticationEnabled -> {
                setItemsAuthenticationEnabled()
            }

            is AuthenticationScreenEvents.DismissPasswordSpecificationBottomSheet -> {
                changePasswordSpecificationBottomSheetVisibility(visibility = false)
            }

            is AuthenticationScreenEvents.DismissPasswordCheckBottomSheet -> {
                changePasswordCheckBottomSheetVisibility(visibility = false)
            }

            is AuthenticationScreenEvents.DismissBiometricPrompt -> {
                setBiometricPromptVisibility(false)
            }

            is AuthenticationScreenEvents.ShowPasswordSpecificationBottomSheet -> {
                changePasswordSpecificationBottomSheetVisibility(visibility = true)
            }

            is AuthenticationScreenEvents.ShowBiometricPrompt -> {
                changeBiometricPromptVisibility(visibility = true)
            }

            is AuthenticationScreenEvents.ChangeAuthTypeInLocal -> {
                changeAuthenticationInLocal(event.authType)
            }

            is AuthenticationScreenEvents.ShowError -> {
                showError(event.message)
            }
        }
    }

    private fun showError(message: UiText) {
        viewModelScope.launch(dispatcher) {
            _errorChannel.send(message)
        }
    }
}

sealed class AuthenticationScreenEvents {
    data class AddServiceIdToAuthList(val id: Int) : AuthenticationScreenEvents()
    data class RemoveServiceIdFromAuthList(val id: Int) : AuthenticationScreenEvents()
    data class SetSelectedAuthenticationType(val authType: AuthenticationType) :
        AuthenticationScreenEvents()

    data class ChangeAuthenticationType(val authType: AuthenticationType) :
        AuthenticationScreenEvents()

    data class AddItems(val facilityServiceItem: FacilityServiceItem) : AuthenticationScreenEvents()
    data object DismissPasswordSpecificationBottomSheet : AuthenticationScreenEvents()
    data object DismissPasswordCheckBottomSheet :
        AuthenticationScreenEvents()

    data object DismissBiometricPrompt : AuthenticationScreenEvents()
    data object ShowPasswordSpecificationBottomSheet : AuthenticationScreenEvents()
    data object ShowBiometricPrompt : AuthenticationScreenEvents()
    data object SetItemsAuthenticationEnabled : AuthenticationScreenEvents()
    data class ChangeAuthTypeInLocal(val authType: AuthenticationType) :
        AuthenticationScreenEvents()

    data class ShowError(val message: UiText) : AuthenticationScreenEvents()

}