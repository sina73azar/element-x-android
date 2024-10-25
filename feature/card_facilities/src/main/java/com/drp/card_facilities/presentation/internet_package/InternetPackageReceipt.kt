package com.drp.refah.card_facilities.presentation.internet_package

import com.drp.data.model.internet_package.inquiry.PackageItem
import com.drp.refah.card_facilities.utility.enums.MobileOperatorTab
import com.drp.shared_ui.model.receipt.ReceiptItem
import com.drp.shared_ui.model.receipt.ReceiptType
import com.drp.utils.currencyFormatter
import com.drp.utils.panFormatter


fun internetPackageReceipt(
    packageItem: PackageItem?,
    cardNumber: String,
    walletId: String,
    phoneNumber: String,
    operator: MobileOperatorTab
): ArrayList<ReceiptItem> {
    val items = ArrayList<ReceiptItem>()

    items.add(ReceiptItem(0, "نوع تراکنش", "خرید بسته اینترنتی"))
    if (cardNumber.isNotEmpty())
        items.add(ReceiptItem(0, "کارت مبدا", panFormatter(cardNumber)))
    if (walletId.isNotEmpty())
        items.add(ReceiptItem(0, "شناسه کیف پول مبدا", walletId))
    packageItem?.let {
        items.add(
            ReceiptItem(
                0,
                "شماره همراه",
                phoneNumber.filter { it.isDigit() }
            )
        )
    }
    when (operator) {
        MobileOperatorTab.MCI -> items.add(ReceiptItem(0, "اپراتور", "همراه اول"))
        MobileOperatorTab.MTN -> items.add(ReceiptItem(0, "اپراتور", "ایرانسل"))
        MobileOperatorTab.RIGHTEL -> items.add(ReceiptItem(0, "اپراتور", "رایتل"))
    }
    packageItem?.title?.let {
        items.add(
            ReceiptItem(0, "نوع", packageItem.title)
        )
    }
    items.add(
        ReceiptItem(
            0,
            "مبلغ",
            currencyFormatter(packageItem?.price),
            ReceiptType.AMOUNT
        )
    )
    items.add(
        ReceiptItem(
            0,
            "مبلغ با مالیات",
            currencyFormatter(packageItem?.let { it.price + it.tax }),
            ReceiptType.AMOUNT
        )
    )
    return items
}