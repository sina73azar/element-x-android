package com.drp.card_facilities.presentation.authentication

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.drp.data.model.FacilityServiceItem
import com.drp.shared_ui.enums.AuthenticationType

data class AuthenticationScreenState(
    var selectedAuthType: AuthenticationType = AuthenticationType.NONE,
    var items: SnapshotStateList<FacilityServiceItem> = mutableStateListOf(),

    /** password bottom sheet parameters */
    var passwordSpecificationBottomSheetVisibility: Boolean = false,
    var passwordCheckBottomSheetVisibility: Boolean = false,

    /** finger print bottom sheet parameters */
    var biometricPromptVisibility: Boolean = false,

    /** action type is for handling authentication bottom sheets visibility sequence
     * and handling each bottom sheet callback */
    var actionType: ActionType = ActionType.Idle
)

enum class ActionType {
    Idle,
    EnablePass,
    DisableBio_EnablePass,
    EnableBio,
    DisablePass_EnableBio,
    DisablePass,
    DisableBio
}