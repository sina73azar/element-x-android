package com.drp.card_facilities.utility

import com.drp.refah.card_facilities.data.model.bill.inquiry.unified.InquiryBillResult
import com.drp.data.enums.BillType
import com.drp.shared_ui.model.receipt.ReceiptItem
import com.drp.shared_ui.model.receipt.ReceiptType
import com.drp.utils.currencyFormatter
import com.drp.utils.panFormatter


object ReceiptGenerator {
    fun billConfirmReceipt(
        result: InquiryBillResult,
        billId: String,
        paymentId: String,
        cardNumber: String,
        walletId: String

    ): List<ReceiptItem> {
        val items = ArrayList<ReceiptItem>()
        items.add(ReceiptItem(0, "نوع تراکنش", "پرداخت قبض " + result.payment.billerPersianName))
        if (cardNumber.isNotEmpty())
            items.add(ReceiptItem(0, "کارت مبدا", panFormatter(cardNumber)))
        if (walletId.isNotEmpty())
            items.add(ReceiptItem(0, "شماره کیف پول", walletId))
        items.add(ReceiptItem(0, "شناسه قبض", billId))
        if (paymentId.isNotEmpty())
            items.add(ReceiptItem(0, "شناسه پرداخت", paymentId))
        items.add(
            ReceiptItem(
                0,
                "مبلغ",
                currencyFormatter(result.payment.amount),
                ReceiptType.AMOUNT
            )
        )
        return items
    }

    fun phoneBillReceipt(
        billType: BillType,
        billId: String,
        paymentId: String,
        phoneNumber: String,
        amount: Long,
        cardNumber: String,
        walletId: String
    ): List<ReceiptItem> {
        val items = ArrayList<ReceiptItem>()
        items.add(ReceiptItem(0, "نوع تراکنش", billType.PaymentText))
        if (cardNumber.isNotEmpty())
            items.add(ReceiptItem(0, "کارت مبدا", panFormatter(cardNumber)))
        if (walletId.isNotEmpty())
            items.add(ReceiptItem(0, "شماره کیف پول", walletId))
        items.add(ReceiptItem(0, "شناسه قبض", billId))
        if (billType == BillType.FIXEDLINE || billType == BillType.FIXEDLINEEXTENDED)
            items.add(ReceiptItem(0, "شماره تلفن", phoneNumber))
        else
            items.add(ReceiptItem(0, "شماره موبایل", phoneNumber))
        if (paymentId.isNotEmpty())
            items.add(ReceiptItem(0, "شناسه پرداخت", paymentId))
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
}