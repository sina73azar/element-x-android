package com.drp.card_facilities.presentation.wallet_minus

import com.drp.shared_ui.model.receipt.ReceiptItem
import com.drp.shared_ui.model.receipt.ReceiptType
import com.drp.utils.currencyFormatter
import com.drp.utils.ibanFormatterForEditText
import com.drp.utils.panFormatter

fun walletMinusReceipt(
    walletId: String,
    destinationIban: String,
    destinationCardNumber: String,
    amount: Long,
    description: String
): List<ReceiptItem> {
    val items = ArrayList<ReceiptItem>()
    items.add(ReceiptItem(0, "شناسه کیف پول", walletId))
    if (destinationIban.isNotEmpty())
        items.add(
            ReceiptItem(
                0,
                "شماره شبا مقصد",
                "IR".plus(ibanFormatterForEditText(destinationIban))
            )
        )
    if (destinationCardNumber.isNotEmpty())
        items.add(
            ReceiptItem(
                0,
                "شماره کارت مقصد",
                panFormatter(destinationCardNumber)
            )
        )
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