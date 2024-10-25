package com.drp.card_facilities.presentation.card_to_card

import com.drp.refah.card_facilities.data.model.card_to_card.refahi.inquiry.InquiryCardResult
import com.drp.shared_ui.model.receipt.ReceiptItem
import com.drp.shared_ui.model.receipt.ReceiptType
import com.drp.utils.currencyFormatter
import com.drp.utils.panFormatter


fun transferInquiry(result: InquiryCardResult): List<ReceiptItem> {
    val items = ArrayList<ReceiptItem>()
    items.add(ReceiptItem(0, "نوع تراکنش", "انتقال وجه کارت به کارت"))
    items.add(
        ReceiptItem(
            1,
            "مبدا",
            panFormatter(result.fundTransfer.source),
            ReceiptType.CARD
        )
    )
    items.add(
        ReceiptItem(
            2, "مقصد", panFormatter(result.fundTransfer.destination),
            ReceiptType.CARD
        )
    )
    items.add(
        ReceiptItem(
            3,
            "نام دارنده کارت",
            result.customerName.firstName + " " + result.customerName.lastName
        )
    )
    items.add(ReceiptItem(5, "مبلغ", currencyFormatter(result.amount), ReceiptType.AMOUNT))
    if (result.card.imageUrl != null)
        try {
            items.add(
                ReceiptItem(
                    4,
                    "نام بانک ",
                    result.card.destinationBankName,
                    resIcon = result.card.imageUrl!!.split(",")[1]
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    else {
        items.add(ReceiptItem(4, "نام بانک ", result.card.destinationBankName))
    }
    return items
}

fun cardToCardReceipt(
    sourceCardNumber: String,
    destinationCardNumber: String,
    amount: Long
): List<ReceiptItem> {
    val items = ArrayList<ReceiptItem>()
    items.add(ReceiptItem(0, "نوع تراکنش", "انتقال وجه کارت به کارت"))
    items.add(
        ReceiptItem(
            1,
            "شماره کارت مبدا",
            panFormatter(sourceCardNumber),
            ReceiptType.CARD
        )
    )
    items.add(
        ReceiptItem(
            2, "شماره کارت مقصد", panFormatter(destinationCardNumber),
            ReceiptType.CARD
        )
    )
    items.add(ReceiptItem(5, "مبلغ", currencyFormatter(amount), ReceiptType.AMOUNT))
    return items
}