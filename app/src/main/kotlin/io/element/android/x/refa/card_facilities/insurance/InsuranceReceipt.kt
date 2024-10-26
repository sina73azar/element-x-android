package com.drp.card_facilities.presentation.insurance

import com.drp.shared_ui.model.receipt.ReceiptItem
import com.drp.shared_ui.model.receipt.ReceiptType
import com.drp.utils.currencyFormatter
import com.drp.utils.panFormatter


fun billInsuranceReceipt(
    paymentId: String,
    amount: Long,
    cardNumber: String,
    walletId: String,
): List<ReceiptItem> {
    val items = ArrayList<ReceiptItem>()
    items.add(ReceiptItem(0, "نوع تراکنش", "پرداخت حق بیمه "))
    if (cardNumber.isNotEmpty())
        items.add(ReceiptItem(0, "کارت مبدا", panFormatter(cardNumber)))
    if (walletId.isNotEmpty())
        items.add(ReceiptItem(0, "شماره کیف پول", walletId))
    items.add(ReceiptItem(0, "شناسه پرداخت", paymentId))
    items.add(ReceiptItem(0, "مبلغ", currencyFormatter(amount), ReceiptType.AMOUNT))
    return items
}
