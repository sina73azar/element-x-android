package com.drp.superapp.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.core.text.isDigitsOnly
import com.drp.superapp.shahkar.BuildConfig
import java.io.IOException
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.zip.ZipFile

object Commons {
    fun parseCode(message: String?, digitSize: Int): String {
        val p: Pattern = Pattern.compile("\\b\\d{$digitSize}\\b")
        val m: Matcher = p.matcher(message)
        var code = ""
        while (m.find()) {
            code = m.group(0)
        }
        return code
    }

    fun isValidSMSTokenNo(token: String): Boolean {
        if (token.isDigitsOnly() && token.length >= 4) return true
        return false
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

    fun createSecureRandomNumber(min: Int, max: Int): Int {
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
}
