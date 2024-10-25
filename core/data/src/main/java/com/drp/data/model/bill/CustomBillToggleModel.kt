import com.drp.data.enums.BillType
import com.drp.refah.card_facilities.data.model.bill.inquiry.BillPaymentInfo


data class CustomBillToggleModel(
    var id: Int,
    var billId: String,
    var amount: Long,
    var desc: String,
    var paymentId: String,
    var billType: BillType
)

fun CustomBillToggleModel.toBillPaymentInfo() = BillPaymentInfo(
    billId = this.billId,
    paymentId = this.paymentId,
    amount = this.amount
)
