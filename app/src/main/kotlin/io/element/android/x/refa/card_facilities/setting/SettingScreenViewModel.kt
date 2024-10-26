package com.drp.card_facilities.presentation.setting

import androidx.lifecycle.ViewModel
import com.drp.data.repository.CardFacilitiesUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingScreenViewModel @Inject constructor(
    private val cardFacilitiesUserRepository: CardFacilitiesUserRepository,
) :
    ViewModel() {

    fun logout() {
        cardFacilitiesUserRepository.logout()
    }
}