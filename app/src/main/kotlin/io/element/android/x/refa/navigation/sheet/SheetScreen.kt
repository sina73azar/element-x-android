package com.drp.shared_ui.navigation.sheet

sealed class NavigationSheet(val path: String) {
    data object TopUpPaymentFragment :
        NavigationSheet("com.drp.refah.mobile_bank.ui.module.topup.payment.TopUpPaymentFragment")

    data object CardActionFragment :
        NavigationSheet("com.drp.refah.card.card_actions.action.CardActionFragment")

    data object ShareFragment :
        NavigationSheet("io.element.android.x.Receipt.ShareFragment")

    data object ReceiptFragment :
        NavigationSheet("io.element.android.x.Receipt.ReceiptFragment")
}
