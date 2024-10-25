package com.drp.shared_ui.navigation.activity


sealed class NavigationActivity(val path: String) {
    /*data object ExternalTransaction :
        NavigationActivity("com.drp.refah.transaction.external.ExternalTransferActivity")

    data object InternalTransaction :
        NavigationActivity("com.drp.refah.transaction.internal.InternalTransferActivity")

    data object TransactionHistory :
        NavigationActivity("com.drp.refah.transaction.history.TransactionHistoryActivity")

    data object RecurringReportActivity :
        NavigationActivity("com.drp.refah.transaction.report.RecurringReportActivity")

    data object PaymentOrderActivity :
        NavigationActivity("com.drp.refah.transaction.payment_order.PaymentOrderActivity")

    data object PayOrdInqAct :
        NavigationActivity("com.drp.refah.transaction.payment_order.inquiry.PayOrdInqAct")

    data object CardPassMngActivity :
        NavigationActivity("com.drp.refah.card.card_pass_mng.CardPassMngActivity")

    data object CardToCardActivity :
        NavigationActivity("com.drp.refah.card.card_to_card.CardToCardActivity")*/
    data object SuperAppCardToCard :
        NavigationActivity("com.drp.refah.card_facilities.presentation.card_to_card.SuperAppCardToCardActivity")

    /*data object DefaultCardActivity :
        NavigationActivity("com.drp.refah.card.card_actions.default_card.DefaultCardActivity")*/

    data object LandingPageActivity :
        NavigationActivity("com.drp.refah.card_facilities.LandingPageActivity")

    data object KahrobaManageActivity :
        NavigationActivity("com.drp.refah.ui.KahrobaManageActivity")

    data object ContactActivity :
        NavigationActivity("com.drp.refah.mobile_bank.ui.module.contact.ContactActivity")

    data object InsuranceInquiryActivity :
        NavigationActivity("com.drp.refah.card_facilities.presentation.insurance.inquiry.InsuranceInquiryActivity")

    /*data object TopUpPaymentActivity :
        NavigationActivity("com.drp.refah.card_facilities.presentation.topup.inquiry.TopUpCardActivity")

    data object InstallmentInquiryActivity :
        NavigationActivity("com.drp.refah.card_facilities.presentation.installment.inquiry.InstallmentInquiryActivity")

    data object InternetPackageActivity :
        NavigationActivity("com.drp.refah.card_facilities.presentation.internet_package.InternetPackageCardActivity")*/

    data object GuidActivity :
        NavigationActivity("com.drp.refah.mobile_bank.ui.module.guid.GuidActivity")

    data object VersionActivity :
        NavigationActivity("com.drp.refah.mobile_bank.ui.module.version.VersionActivity")
}