package com.drp.data.network

object EndPoints {
    const val BILL_INQUIRY_END_POINT = "/services/mbserver/api/billPresentmentInquiry"
    const val PHONE_BILL_INQUIRY_END_POINT = "/services/mbserver/api/billInquiry"
    const val UTILITY_BILL_INQUIRY_END_POINT = "/services/mbserver/api/billInquiry"
    const val OTP_REQUEST_END_POINT = "/services/mbserver/api/serviceSendOtpSms"
    const val CARD_PASSWORD_INQUIRY_END_POINT = "/services/cardservice/public/cardPasswordInquiry"
    const val CARD_BILL_PAYMENT_END_POINT = "services/mbserver/direct/api/CardBillPaymentAdd"
    const val CARD_BALANCE_INQUIRY_END_POINT = "services/mbserver/direct/api/CardBalanceInquiry"
    const val LAST_TEN_STATEMENT_INQUIRY_END_POINT =
        "ApiGateway/Neo/GetStatement"
    const val DETECT_TOP_UP_OPERATOR_END_POINT = "/services/mbserver/api/detectTopupOperator"
    const val CARD_PASSWORD_INQUIRY = "/services/cardservice/public/cardPasswordInquiry"
    const val TOP_UP_INQUIRY_END_POINT = "/services/mbserver/api/checkAmount"
    const val TOP_UP_PAYMENT_END_POINT = "/services/mbserver/direct/api/CardTopupPayment"
    const val INTERNET_PACKAGE_INQUIRY_END_POINT =
        "/services/mbserver/direct/topupProduct/TopUpProductListInquiry"
    const val INTERNET_PACKAGE_PAYMENT_END_POINT =
        "/services/mbserver/direct/api/CardPackagePayment"
    const val FACILITY_INQUIRY_END_POINT = "/services/facilityservice/api/facilityInquiry"
    const val FACILITY_PAYMENT_END_POINT = "/services/mbserver/direct/api/CardLoanPayment"

    /** Wallet EndPoints */
    const val WALLET_ENDPOINT_FIRST_PART = "/ApiGateway/Wallet/Service/"

    const val ADD_TO_WALLET_OTP_END_POINT =
        "/ApiGateway/E_Wallet/ChargeWalletByCardRequest"
    const val ADD_TO_WALLET_END_POINT =
        "/ApiGateway/E_Wallet/ChargeWalletByCardProcess"

    const val WALLET_TO_WALLET_END_POINT =
        "/ApiGateway/E_Wallet/TransferWalletToWallet"

    const val MINUS_FROM_WALLET_OTP_END_POINT =
        "/ApiGateway/E_Wallet/DeChargeWalletToIbanRequest"
    const val MINUS_FROM_WALLET_END_POINT =
        "/ApiGateway/E_Wallet/DeChargeWalletToIbanProcess"

    const val GET_WALLET_BALANCE_END_POINT = "/ApiGateway/E_Wallet/GetWalletBalance"
    const val CARD_TO_CARD_OTP_END_POINT = "/ApiGateway/E_Wallet/Card2CardRequest"
    const val CARD_TO_CARD_TRANSFER_END_POINT = "/ApiGateway/E_Wallet/Card2CardProcess"

    const val PAYMENT_WALLET_OTP_END_POINT = "/ApiGateway/E_Wallet/RQSubmit"
    const val PAYMENT_WALLET_END_POINT = "/ApiGateway/E_Wallet/RQProcess"

    const val INTERNET_PACKAGE_INQUIRY_WITH_WALLET_END_POINT =
        "/ApiGateway/Bills/TopUpGetProductsAsync"

    const val BILL_INQUIRY_WITH_WALLET_END_POINT = "/ApiGateway/Neo/BillInquiry"

    const val DYN_PIN_END_POINT = "/ApiGateway/Neo/DynPin"
    const val GET_BALANCE_END_POINT = "/ApiGateway/Neo/GetBalance"
    const val GET_WALLET_TRANS_END_POINT = "/ApiGateway/E_Wallet/GetWalletTrans"

    const val LICENCE_NEGATIVE_SCORE_INQUIRY_END_POINT = "/ApiGateway/Bills/NajiServiceDrivingLicenseNegativePointInquiryAsync"
    const val TRACKING_POST_INQUIRY_END_POINT = "/ApiGateway/Bills/PostTrackingInquiryAsync"

    const val CARD_TO_IBAN_END_POINT = "/ApiGateway/Bills/ShebaInquiryCardToShebaAsync"
    const val ACCOUNT_TO_IBAN_END_POINT = "/ApiGateway/Bills/ShebaInquiryAccountToShebaAsync"
    const val IBAN_TO_ACCOUNT_END_POINT = "/ApiGateway/Bills/ShebaInquiryShebaToAccountAsync"

    const val VEHICLE_VIOLATION_INQUIRY_END_POINT = "/ApiGateway/Bills/TrafficFinesInquiryByPlateNumberNoDetailAsync"
    const val MOTOR_VIOLATION_INQUIRY_END_POINT = "/ApiGateway/Bills/MotorTrafficFinesInquiryByPlateNumberNoDetailAsync"

    /**
     * card to card refahi
     * */

    const val CARD_INQUIRY = "/services/cardservice/api/cardInquiry"
    const val CARD_FUND_TRANSFER = "/services/cardservice/api/cardFundTransfer"

    /** card info */

    const val GET_CARDS = "/services/ibanchecker/api/card"

    /**
     * Hub Region
     */
    const val HUB_ENROLLMENT = "/services/mbserver/direct/hubfanavaran/HubCardEnrollment"
    const val HUB_CARD_INFO_ADD = "/services/mbserver/direct/hubfanavaran/HubCardInfoAdd"
    const val ALL_HUB_CARD_INFO = "/services/mbserver/direct/hubfanavaran/HubCardsList"
    const val HUB_CARD_HOLDER_INQUIRY =
        "/services/mbserver/direct/hubfanavaran/HubCardHolderInquiry"
    const val HUB_SEND_OTP = "/services/mbserver/direct/hubfanavaran/HubSendOtp"
    const val HUB_CARD_TRANSFER = "/services/mbserver/direct/hubfanavaran/HubCardTransfer"
    const val HUB_REACTIVATION = "/services/mbserver/direct/hubfanavaran/HubReActivation"
    const val HUB_TRANSACTION_LOGS = "/services/mbserver/direct/hubfanavaran/HubTransactionLogs"

    /**
     * shahkar
     */
    const val SHAHKAR_INQUIRY_ENDPOINT = "/ApiGateway/Token/CreateWalletRequest"
    const val SHAHKAR_VALIDATE_ENDPOINT = "/ApiGateway/Token/CreateWalletProcess"
    const val SHAHKAR_REFRESH_TOKEN_ENDPOINT = "/auth/refresh"
}