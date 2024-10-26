package com.drp.card_facilities.presentation.topup

import com.drp.refah.card_facilities.utility.enums.MobileOperatorTab
import com.drp.shared_ui.model.receipt.ReceiptItem
import com.drp.shared_ui.model.receipt.ReceiptType
import com.drp.utils.currencyFormatter
import com.drp.utils.panFormatter


fun topUpInquiryReceipt(
    cardNumber: String,
    walletId: String,
    operatorType: MobileOperatorTab,
    mobileNumber: String,
    chargeType: String,
    amount: Long
): List<ReceiptItem> {
    val items = ArrayList<ReceiptItem>()
    items.add(ReceiptItem(0, "نوع تراکنش", "خرید شارژ"))
    if (cardNumber.isNotEmpty())
        items.add(ReceiptItem(0, "کارت مبدا", panFormatter(cardNumber)))
    if (walletId.isNotEmpty())
        items.add(ReceiptItem(0, "شناسه کیف پول مبدا", walletId))
    when (operatorType) {
        MobileOperatorTab.MCI -> items.add(ReceiptItem(0, "اپراتور", "همراه اول"))
        MobileOperatorTab.MTN -> items.add(ReceiptItem(0, "اپراتور", "ایرانسل"))
        MobileOperatorTab.RIGHTEL -> items.add(ReceiptItem(0, "اپراتور", "رایتل"))
    }
    items.add(ReceiptItem(0, "شماره همراه", mobileNumber.filter { it.isDigit() }))
    items.add(ReceiptItem(0, "نوع شارژ", chargeType))
    items.add(
        ReceiptItem(
            0,
            "مبلغ",
            currencyFormatter(amount),
            ReceiptType.AMOUNT
        )
    )
    return items
}