package com.drp.card_facilities.presentation.setting

import androidx.lifecycle.ViewModel
import com.drp.data.repository.CardFacilitiesUserRepository

class SettingScreenViewModel(
    private val cardFacilitiesUserRepository: CardFacilitiesUserRepository,
) :
    ViewModel() {

    fun logout() {
        cardFacilitiesUserRepository.logout()
    }
}
