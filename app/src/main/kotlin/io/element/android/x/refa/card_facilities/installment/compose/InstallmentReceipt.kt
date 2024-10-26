package com.drp.card_facilities.presentation.installment.compose

import com.drp.refah.card_facilities.data.model.installment.inquiry.FacilityInquiryResult
import com.drp.shared_ui.model.receipt.ReceiptItem
import com.drp.shared_ui.model.receipt.ReceiptType
import com.drp.utils.currencyFormatter
import com.drp.utils.panFormatter

fun loanInquiryReceipt(
    loanInstallment: FacilityInquiryResult,
    cardNumber: String,
): List<ReceiptItem> {

    val items = ArrayList<ReceiptItem>()
    items.add(ReceiptItem(0, "نوع تراکنش", "پرداخت اقساط / تسهیلات"))
    items.add(ReceiptItem(1, "کارت مبدا", panFormatter(cardNumber)))
    items.add(ReceiptItem(2, "نام", loanInstallment.facilityOwnerFirstName))
    items.add(ReceiptItem(2, "نام خانوادگی", loanInstallment.facilityOwnerLastName))
    items.add(
        ReceiptItem(
            6,
            "مبلغ قابل پرداخت",
            currencyFormatter(loanInstallment.amount),
            ReceiptType.AMOUNT
        )
    )
    items.add(ReceiptItem(7, "شماره تسهیلات", loanInstallment.paymentId))
    return items
}