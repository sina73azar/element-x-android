package com.drp.card_facilities.presentation.wallet_to_wallet

import com.drp.shared_ui.model.receipt.ReceiptItem
import com.drp.shared_ui.model.receipt.ReceiptType
import com.drp.utils.currencyFormatter

fun walletToWalletReceipt(
    sourceWalletId: String,
    destinationWalletId: String,
    amount: Long,
    description: String = ""
): List<ReceiptItem> {
    val items = ArrayList<ReceiptItem>()
    items.add(ReceiptItem(0, "شناسه کیف مبدا", sourceWalletId))
    items.add(ReceiptItem(0, "شناسه کیف مقصد", destinationWalletId))
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