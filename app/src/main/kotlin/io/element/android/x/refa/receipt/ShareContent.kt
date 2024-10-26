package io.element.android.x.Receipt

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import com.drp.shared_ui.model.receipt.ReceiptItem
import com.drp.shared_ui.model.receipt.ReceiptType
import com.drp.utils.convertP2EDigits
import com.drp.utils.reversePan
import io.element.android.x.R
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

fun shareImage(context: Context, encodedBitmap: String) {
    val decodedString: ByteArray = Base64.decode(encodedBitmap, Base64.DEFAULT)
    val decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    try {
        val intent = Intent(Intent.ACTION_SEND).setType("image/*")
        intent.putExtra(Intent.EXTRA_STREAM, getUriFromBitmap(decodedBitmap, context))
        context.startActivity(
            Intent.createChooser(intent, context.getString(R.string.receipt_share_image))
        )
    } catch (exception: Exception) {
        Timber.e(exception.toString())
    }
}

@Suppress("DEPRECATION")
fun getUriFromBitmap(bitmap: Bitmap, context: Context): Uri {
    val bytes = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path: String =
        MediaStore.Images.Media.insertImage(
            context.contentResolver,
            bitmap,
            Calendar.getInstance().getTime().toString(),
            "رسید"
        )
//    val values = ContentValues()
//    values.put(MediaStore.Images.Media.TITLE, "Title")
//    values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
//    val path =
//        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    return Uri.parse(path)
}

fun captureImage(view: View): String {
    val builder = StrictMode.VmPolicy.Builder()
    StrictMode.setVmPolicy(builder.build())
    val bitmap: Bitmap = takeScreenshot(view)
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun shareContent(context: Context, title: String?, body: String) {
    val myBody: String = body
    val intent = Intent()
    intent.action = Intent.ACTION_SEND
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, myBody)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    if (title != null) {
        context.startActivity(Intent.createChooser(intent, title))
    }
}

private fun takeScreenshot(view: View): Bitmap {
    val bitmap = if (view.measuredHeight <= 0) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
    } else {
        Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    }
    val canvas = Canvas(bitmap)
    view.draw(canvas)
    return bitmap
}

/**
 * AMB-108 - construct shorter text receipt message for different transactions
 * */
fun shareTextReceipt(receipts: List<ReceiptItem>, title: String): String {
    if (receipts.any { it.title == "حساب مبدا" }) {
        val value = receipts.findLast { it.title == "حساب مبدا" }?.value
        if (value?.length!! > 4)
            receipts.findLast { it.title == "حساب مبدا" }?.value =
                "${value.substring(value.length - 4, value.length)}***"
        else
            receipts.findLast { it.title == "حساب مبدا" }?.value =
                "${value.substring(value.length - 2, value.length)}***"
    }
    /** generate different receipt message for ach & rtgs & IP */
    if (!receipts.any { it.title == "تعداد دفعات" } && receipts.any {
            it.value == "انتقال وجه پایا"
                ||
                it.value == "انتقال وجه ساتنا" || it
                .value == "انتقال وجه پل"
        }) {
        return constructAchReceiptMessage(receipts)
    }

    /** generate different receipt message for card to card */
    if (receipts.any { it.value?.contains("کارت به کارت") == true })
        return constructCardToCardReceiptMessage(receipts)

    /** generate different receipt message for internal & mobile */
    if (receipts.any { it.value == "انتقال وجه موبایلی" || it.value == "انتقال وجه داخلی" })
        return constructInternalReceiptMessage(receipts)

    /** generate different receipt message for other transactions */
    val plainVoucher = StringBuilder()
    plainVoucher.append(title).append("\n")
    for (receipt: ReceiptItem in receipts) {
        if (receipt.isSharable) {
            if (receipt.type == ReceiptType.AMOUNT) {
                plainVoucher.append(receipt.title.trim()).append(" : ")
                var str: String = NumberFormat.getNumberInstance(Locale.US).format(
                    convertP2EDigits(
                        receipt.value?.trim()?.filter { it.isDigit() }!!
                    ).toLong()
                )
                str = convertP2EDigits(str)
                plainVoucher.append(str + "ریال")
                plainVoucher.append("\n")
            } else {
                plainVoucher.append(receipt.title.trim()).append(" : ")
                var value: String
                if (receipt.type == ReceiptType.IBAN)
                    value = receipt.value?.trim()!!
                else
                    value = convertP2EDigits(receipt.value?.trim()!!)
                value = "\u200F" + value
                if (receipt.type == ReceiptType.CARD)
                    value = reversePan(value)
                plainVoucher.append(value)
                plainVoucher.append("\n")
            }
        }
    }
    return plainVoucher.toString()
}

fun constructInternalReceiptMessage(receipts: List<ReceiptItem>): String {
    val type = receipts.findLast { it.title == "نوع تراکنش" }?.value ?: ""
    val source = receipts.findLast { it.title == "حساب مبدا" }?.value ?: ""
    val dest = receipts.findLast { it.title == "حساب مقصد" }?.value ?: ""
    val nameDest = receipts.findLast { it.title.contains("نام دارنده") }?.value ?: ""
    val amount = receipts.findLast { it.title == "مبلغ" }?.value ?: ""
    val date = receipts.findLast { it.title == "تاریخ" }?.value ?: ""
    val time = receipts.findLast { it.title == "ساعت" }?.value ?: ""

    val plainVoucherAch = StringBuilder()
    plainVoucherAch.append("$type ")
        .append(" از حساب ")
        .append(source)
        .append("به حساب ")
        .append(dest)
        .append(" متعلق به ")
        .append(" $nameDest ")
        .append(" به مبلغ ")
        .append("$amount ریال ")
        .append(" در تاریخ ")
        .append(date)
        .append("ساعت")
        .append(time).append(" با موفقیت انجام شده است ")
    return plainVoucherAch.toString()
}

fun constructAchReceiptMessage(receipts: List<ReceiptItem>): String {

    val type = receipts.findLast { it.title == "نوع تراکنش" }?.value ?: ""
    val source = receipts.findLast { it.title == "حساب مبدا" }?.value ?: ""
    val dest = receipts.findLast { it.title.contains("مقصد") }?.value ?: ""
    val nameDest = receipts.findLast { it.title.contains("نام دارنده") }?.value ?: ""
    val reason = receipts.findLast { it.title == "بابت" }?.value ?: ""
    val amount = receipts.findLast { it.title == "مبلغ" }?.value ?: ""
    val date = receipts.findLast { it.title == "تاریخ" }?.value ?: ""
    val time = receipts.findLast { it.title == "ساعت" }?.value ?: ""

    val reasonSafe = if (reason.length > 12) {
        reason.substring(0, 13)
    } else {
        reason
    }
    val plainVoucherAch = StringBuilder()
    plainVoucherAch
        .append("$type ")
        .append(" از حساب ")
        .append(source)
        .append("به شبا ")
        .append(dest)
        .append(" متعلق به")
        .append(" $nameDest ")
        .append(" به مبلغ ")
        .append("$amount ریال ")
        .append(" در تاریخ ")
        .append(date)
        .append("ساعت")
        .append(time)
    if (reasonSafe.isNotEmpty())
        plainVoucherAch.append(" بابت ").append("${reasonSafe}...")
    plainVoucherAch.append(" با موفقیت انجام شده است ")

    return plainVoucherAch.toString()
}

fun constructCardToCardReceiptMessage(receipts: List<ReceiptItem>): String {

    val type = receipts.findLast { it.title == "نوع تراکنش" }?.value ?: ""
    val source = receipts.findLast { it.title.contains("مبدا") }?.value ?: ""
    val dest = receipts.findLast { it.title.contains("مقصد") }?.value ?: ""
    val nameDest = receipts.findLast { it.title.contains("نام دارنده") }?.value ?: ""
    val amount = receipts.findLast { it.title == "مبلغ" }?.value ?: ""
    val date = receipts.findLast { it.title == "تاریخ" }?.value ?: ""
    val time = receipts.findLast { it.title == "ساعت" }?.value ?: ""

    val plainVoucherCard = StringBuilder()
    plainVoucherCard.append("$type ")
        .append("از کارت ")
        .append(source).append("به کارت ")
        .append(dest)
        .append(" به مبلغ ")
        .append("$amount ریال ").append(" در تاریخ ")
        .append(date).append("ساعت")
        .append(time)
    if (nameDest.isNotEmpty())
        plainVoucherCard.append(" متعلق به ").append(" $nameDest ")
    plainVoucherCard.append(" با موفقیت انجام شده است ")


    return plainVoucherCard.toString()
}
