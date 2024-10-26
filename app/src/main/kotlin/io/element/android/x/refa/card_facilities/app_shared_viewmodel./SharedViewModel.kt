package com.drp.card_facilities.presentation.app_shared_viewmodel

import androidx.lifecycle.ViewModel
import com.drp.data.network.CustomResponse
import com.drp.data.repository.CardFacilitiesRepository
import com.drp.refah.card_facilities.data.model.card_info.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

open class SharedViewModel @Inject constructor(
    private val cardFacilitiesRepository: CardFacilitiesRepository
) : ViewModel() {

    suspend fun getCards(cardNumber: String): CardNumberResult? {
        val validCardNum = cardNumber.trim().filter { it.isDigit() }
        if (validCardNum.length == 6 || validCardNum.length == 16) {
            val request = CardNumberRequest(validCardNum)
            return withContext(Dispatchers.IO) {
                cardFacilitiesRepository.getCards(request)
                    .first().let {
                        if (it.status == CustomResponse.Status.SUCCESS) {
                            return@withContext it.data
                        } else return@withContext null
                    }
            }
        } else
            return null


    }
}