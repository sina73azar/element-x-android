package com.drp.refah.card_facilities.data.model.card_to_card.hub.transaction_logs

// TODO set PAGE_SIZE_TRANSACTION_LOGS const to size
data class HubTransactionLogsRequest(
    var sourcePAN: String? = null,
    var page: Int? = null,
    var size: Int = 10,
    var fromDate: Long? = null,
    var toDate: Long? = null,
)

fun HubTransactionLogsRequest.reInitFilterFields(hubTransactionLogsRequest: HubTransactionLogsRequest) {
    this.sourcePAN = hubTransactionLogsRequest.sourcePAN
    this.fromDate = hubTransactionLogsRequest.fromDate
    this.toDate = hubTransactionLogsRequest.toDate
    this.page = null
}
