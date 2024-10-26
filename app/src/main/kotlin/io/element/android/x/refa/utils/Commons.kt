package com.drp.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Size
import android.view.View
import android.view.WindowManager
import android.view.WindowMetrics
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import io.element.android.x.BuildConfig
//import com.xdev.arch.persiancalendar.datepicker.calendar.PersianCalendar
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.text.NumberFormat
import java.util.Locale
import javax.crypto.Cipher

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun convertP2EDigits(digits: String): String {
    return digits
        //For persian digits
        .replace("۰", "0")
        .replace("۱", "1")
        .replace("۲", "2")
        .replace("۳", "3")
        .replace("۴", "4")
        .replace("۵", "5")
        .replace("۶", "6")
        .replace("۷", "7")
        .replace("۸", "8")
        .replace("۹", "9")
        //For arabic digits
        .replace("٠", "0")
        .replace("١", "1")
        .replace("٢", "2")
        .replace("٣", "3")
        .replace("٤", "4")
        .replace("٥", "5")
        .replace("٦", "6")
        .replace("٧", "7")
        .replace("٨", "8")
        .replace("٩", "9")
}

fun phoneNumberFromatter(phoneNumber: String?): String {
    val blockLengths = intArrayOf(4, 3, 4)
    var unFormattedPan = ""
    if (phoneNumber == null || phoneNumber.isEmpty()) return ""
    val unFormattedSeq = convertP2EDigits(phoneNumber).filter { it.isDigit() }
    if (unFormattedPan.length == unFormattedSeq.length) {
        return ""
    }
    unFormattedPan = unFormattedSeq
    if (unFormattedPan.length > 26) {
        unFormattedPan = unFormattedSeq.substring(0, 26)
    }
    val formatted = StringBuilder()
    var blockIndex = 0
    var currentBlock = 0
    for (element in unFormattedPan) {
        if (currentBlock == blockLengths[blockIndex]) {
            formatted.append("-")
            currentBlock = 0
            blockIndex++
        }
        formatted.append(element)
        currentBlock++
    }
    return formatted.toString()
}

fun isValidNationalCode(nationalCode: String): Boolean {
    var nationalCodeCumulative = 0
    val nationalCodeMod: Int
    val exceptionsNationalCode = arrayOf(
        "0000000000",
        "2222222222",
        "3333333333",
        "4444444444",
        "5555555555",
        "6666666666",
        "7777777777",
        "8888888888",
        "9999999999"
    )
    if (nationalCode.length < 10) {
        return false
    }
    for (exceptionNationalCode in exceptionsNationalCode) {
        if (nationalCode.equals(exceptionNationalCode, ignoreCase = true)) {
            return false
        }
    }
    val nationalCodeIntArray = IntArray(10)
    val nationalCodeLength = nationalCode.length
    if (nationalCodeLength != 10) {
        return false
    } else {
        for (i in 0..9) {
            if (!Character.isDigit(nationalCode[i])) {
                return false
            }
            nationalCodeIntArray[i] = nationalCode[i].toString().toInt() * (10 - i)
        }
    }
    for (i in 0..8) {
        nationalCodeCumulative += nationalCodeIntArray[i]
    }
    nationalCodeMod = nationalCodeCumulative % 11
    return nationalCodeMod < 2 && nationalCodeIntArray[9] == nationalCodeMod || nationalCodeMod >= 2 && 11 - nationalCodeMod == nationalCodeIntArray[9]
}

fun isValidMobileNo(mobileNo: String): Boolean {
    if (mobileNo.isDigitsOnly() && mobileNo.startsWith("09") && mobileNo.length == 11) return true
    return false
}

@Suppress("DEPRECATION")
fun getScreenSize(context: Context): Size {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val metrics: WindowMetrics =
            context.getSystemService(WindowManager::class.java).currentWindowMetrics
        Size(metrics.bounds.width(), metrics.bounds.height())
    } else {
        val display =
            ContextCompat.getSystemService(context, WindowManager::class.java)?.defaultDisplay
        val metrics = if (display != null) {
            DisplayMetrics().also { display.getRealMetrics(it) }
        } else {
            Resources.getSystem().displayMetrics
        }
        Size(metrics.widthPixels, metrics.heightPixels)
    }
}

fun currencyFormatter(value: Long?): String {
    return if (value != null) {
        NumberFormat.getNumberInstance(Locale.US).format(value)
    } else ""
}

fun String.addRial(): String {
    return "$this ریال "
}

fun panFormatter(pan: String?, format: String? = "-"): String {
    val blockLengths = intArrayOf(4, 4, 4, 4)
    var unFormattedPan = ""
    if (pan == null || pan.isEmpty()) return ""
    val unFormattedSeq = convertP2EDigits(pan).filter { it.isDigit() }
    if (unFormattedPan.length == unFormattedSeq.length) {
        return ""
    }
    unFormattedPan = unFormattedSeq
    if (unFormattedPan.length > 16) {
        unFormattedPan = unFormattedSeq.substring(0, 16)
    }
    val formatted = StringBuilder()
    var blockIndex = 0
    var currentBlock = 0
    for (element in unFormattedPan) {
        if (currentBlock == blockLengths[blockIndex]) {
            formatted.append(format)
            currentBlock = 0
            blockIndex++
        }
        formatted.append(element)
        currentBlock++
    }
    return formatted.toString()
}

fun ibanFormatterForEditText(iban: String?): String {
    val blockLengths = intArrayOf(2, 4, 4, 4, 4, 4, 2)
    var unFormattedPan = ""
    if (iban == null || iban.isEmpty()) return ""
    val unFormattedSeq = convertP2EDigits(iban).filter { it.isDigit() }
    if (unFormattedPan.length == unFormattedSeq.length) {
        return ""
    }
    unFormattedPan = unFormattedSeq
    if (unFormattedPan.length > 26) {
        unFormattedPan = unFormattedSeq.substring(0, 26)
    }
    val formatted = StringBuilder()
    var blockIndex = 0
    var currentBlock = 0
    for (element in unFormattedPan) {
        if (currentBlock == blockLengths[blockIndex]) {
            formatted.append("-")
            currentBlock = 0
            blockIndex++
        }
        formatted.append(element)
        currentBlock++
    }
    return formatted.toString()
}

fun extractDigits(raw: String?): String {
    if (raw != null && raw.isNotEmpty()) {
        var value = convertP2EDigits(raw.trim { it <= ' ' })
        value = value.trim({ it <= ' ' }).filter { it.isDigit() }
            .replace("(\\D)\\1+".toRegex(), "$1")
        return value
    } else {
        return ""
    }
}

fun isProbablyArabic(s: String): Boolean {
    var i = 0
    while (i < s.length) {
        val c = s.codePointAt(i)
        if (c in 0x0600..0x06E0) return true
        i += Character.charCount(c)
    }
    return false
}

fun panFormatterHub(pan: String?, format: String? = "-"): String {
    val blockLengths = intArrayOf(4, 4, 4, 4)
    var unFormattedPan = ""
    if (pan == null || pan.isEmpty()) return ""
    val unFormattedSeq = convertP2EDigits(pan).filter { it.isDigit() || it == '*' }
    if (unFormattedPan.length == unFormattedSeq.length) {
        return ""
    }
    unFormattedPan = unFormattedSeq
    if (unFormattedPan.length > 16) {
        unFormattedPan = unFormattedSeq.substring(0, 16)
    }
    val formatted = StringBuilder()
    var blockIndex = 0
    var currentBlock = 0
    for (element in unFormattedPan) {
        if (currentBlock == blockLengths[blockIndex]) {
            formatted.append(format)
            currentBlock = 0
            blockIndex++
        }
        formatted.append(element)
        currentBlock++
    }
    return formatted.toString()
}

fun isValidCardPan(pan: String?): Boolean {
    var myPan: String? = pan
    return if (myPan != null) {
        myPan = myPan.filter { it.isDigit() }
        var sum = 0
        var isOdd = true
        for (ch in myPan.toCharArray()) {
            var digit: Int
            digit = try {
                ch.toString().toInt()
            } catch (e: NumberFormatException) {
                return false
            }
            sum += if (isOdd) if (digit * 2 > 9) digit * 2 - 9 else digit * 2 else digit
            isOdd = !isOdd
        }
        sum % 10 == 0
    } else {
        false
    }
}

@Throws(java.lang.Exception::class)
fun getPublic(keyString: String?): PublicKey? {
    val data = Base64.decode(keyString, Base64.DEFAULT)
    val spec = X509EncodedKeySpec(data)
    val fact = KeyFactory.getInstance("RSA")
    return fact.generatePublic(spec)
}

@Throws(java.lang.Exception::class)
fun encryptWithRsa(publicKey: PublicKey?, text: ByteArray?): ByteArray? {
    val rsa = Cipher.getInstance(BuildConfig.encryptRSA)
    rsa.init(Cipher.ENCRYPT_MODE, publicKey)
    return rsa.doFinal(text)
}

fun organizeDate(date: String): String {
    return date.substring(0, 4).plus("/").plus(date.substring(4, 6)).plus("/")
        .plus(date.substring(6, 8))
}

fun diffTwoTime(date: Long, dateTime: Long): String {
    val diff: Long = date - dateTime
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    return ""/*if (days > 0) {
        PersianCalendar(dateTime).day.toString() + " " + PersianCalendar(dateTime).getMonthName() + " " + PersianCalendar(
            dateTime
        ).year.toString()
    } else if (hours in 1..24)
        "$hours ساعت قبل "
    else if (minutes in 1..60)
        "$minutes دقیقه قبل "
    else
        "$seconds ثانیه قبل "*/
}

