package com.drp.card_facilities.presentation.wallet_add

import com.drp.shared_ui.model.receipt.ReceiptItem
import com.drp.shared_ui.model.receipt.ReceiptType
import com.drp.utils.currencyFormatter
import com.drp.utils.panFormatter

fun walletAddReceipt(
    cardNumber: String,
    walletId: String,
    amount: Long,
    description: String = ""
): List<ReceiptItem> {
    val items = ArrayList<ReceiptItem>()
    items.add(ReceiptItem(0, "کارت مبدا", panFormatter(cardNumber)))
    items.add(ReceiptItem(0, "شناسه کیف پول", walletId))
    items.add(
        ReceiptItem(
            0,
            "مبلغ",
            currencyFormatter(amount),
            ReceiptType.AMOUNT
        )
    )
    if (description.isNotEmpty())
        items.add(ReceiptItem(0, "توضیحات", description))
    return items
}