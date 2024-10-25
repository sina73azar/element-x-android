package com.drp.refah.card_facilities.utility

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import com.drp.card_facilities.BuildConfig
import com.drp.utils.extractDigits
import java.io.IOException
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.zip.ZipFile

object Commons {
    fun correlation(timeStamp: String): String {
        val seed: String = ("0123456789$timeStamp")
        val length = 14
        val stringBuilder = StringBuilder(length)
        for (x in 1..length) {
            val index: Int = createSecureRandomNumber(0, seed.length)
            stringBuilder.append(seed[index])
        }
        return "MB$stringBuilder"
    }

    private fun createSecureRandomNumber(min: Int, max: Int): Int {
        val secureRandom = SecureRandom()
        return secureRandom.nextInt(max - min) + min

    }

    fun getAppVersion(context: Context): String {
        return if (BuildConfig.BUILD_TYPE == "debug")
            "PWA"
        else
            "MB"
    }

    @SuppressLint("PackageManagerGetSignatures")
    @Suppress("DEPRECATION")
    fun getApplicationSignature(context: Context): List<String> {
        val packageName: String = context.packageName
        val signatureList: List<String>
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // New signature
                val sig = context.packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                ).signingInfo
                signatureList = if (sig.hasMultipleSigners()) {
                    // Send all with apkContentsSigners
                    sig.apkContentsSigners.map {
                        val digest = MessageDigest.getInstance("SHA512")
                        digest.update(it.toByteArray())
                        bytesToHex(digest.digest()) + "-" + getDxKey(context)
                    }
                } else {
                    // Send one with signingCertificateHistory
                    sig.signingCertificateHistory.map {
                        val digest = MessageDigest.getInstance("SHA512")
                        digest.update(it.toByteArray())
                        bytesToHex(digest.digest()) + "-" + getDxKey(context)
                    }
                }
            } else {
                val sig = context.packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES
                ).signatures
                signatureList = sig.map {
                    val digest = MessageDigest.getInstance("SHA512")
                    digest.update(it.toByteArray())
                    bytesToHex(digest.digest()) + "-" + getDxKey(context)
                }
            }

            return signatureList
        } catch (e: Exception) {
            // Handle error
        }
        return emptyList()
    }

    fun bytesToHex(bytes: ByteArray): String {
        val hexArray = charArrayOf(
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            'A',
            'B',
            'C',
            'D',
            'E',
            'F'
        )
        val hexChars = CharArray(bytes.size * 2)
        var v: Int
        for (j in bytes.indices) {
            v = bytes[j].toInt() and 0xFF
            hexChars[j * 2] = hexArray[v.ushr(4)]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }

    fun getDxKey(context: Context?): String? {
        val key = java.lang.StringBuilder()
        val zf: ZipFile
        return try {
            zf = ZipFile(context?.packageCodePath)
            var ze = zf.getEntry("classes.dex")
            key.append(ze.crc)
            ze = zf.getEntry("AndroidManifest.xml")
            key.append("-").append(ze.crc)
            key.toString()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            null
        }
    }

    fun checkInternet(context: Context): Boolean {
        var result = 0 // Returns connection type. 0: none; 1: mobile data; 2: wifi; 3: vpn
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        result = 1
                    } else if (hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        result = 2
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_MOBILE) {
                        result = 1
                    } else if (type == ConnectivityManager.TYPE_WIFI) {
                        result = 2
                    }
                }
            }
        }
        return result != 0
    }

    fun customSearchView(searchView: SearchView, context: Context) {
        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        val searchTxt = searchView.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
        searchIcon.setColorFilter(
            ContextCompat.getColor(context, com.drp.shared_ui.R.color.colorPrimary),
            PorterDuff.Mode.SRC_IN
        )
//        val myCustomFont = Typeface.createFromAsset(context.assets, "font/font_reqular.ttf")
//        searchTxt.typeface = myCustomFont
    }

    fun mobileNoFormatter(mobileNo: String): String {
        var reformat = extractDigits(mobileNo)
        if (reformat.startsWith("98")) {
            reformat = "0" + reformat.substring(2)
        } else if (reformat.startsWith("0098")) {
            reformat = "0" + reformat.substring(4)
        } else if (reformat.startsWith("+98")) {
            reformat = "0" + reformat.substring(3)
        }
        return reformat
    }

    fun isValidFixedTelephoneNo(telNo: String): Boolean {
        if (telNo.isDigitsOnly() && telNo.startsWith("0") && telNo.length == 11) return true
        return false
    }

       fun generateNumber(): String = createSecureRandomNumber(100000, 1000000).toString()

    fun isValidCvv2(cvv2: String): Boolean {
        return (cvv2.length in 3..4)
    }

    fun checkPin(pin: String): Boolean {
        return pin.length > 1
    }

    fun checkPaymentId(paymentId: String): Boolean {
        return paymentId.isNotEmpty() || paymentId.length > 15
    }

}