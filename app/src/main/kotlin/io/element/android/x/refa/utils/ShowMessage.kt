package com.drp.shared_ui

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import io.element.android.x.R
import io.github.muddz.styleabletoast.StyleableToast

object ShowMessage {
    fun showErrorMessage(
        message: String,
        view: View,
        length: Int = Snackbar.LENGTH_SHORT,
        actionText: String? = null,
        action: (() -> Unit)? = null
    ) {
        val snackBar = Snackbar.make(view, message, length)
            .setTextColor(ContextCompat.getColor(view.context, R.color.white))
            .setBackgroundTint(ContextCompat.getColor(view.context, R.color.red))
        actionText?.let { s ->
            action?.let {
                snackBar.setActionTextColor(ContextCompat.getColor(view.context, R.color.snackbar_blue))
                    .setAction(s) {
                        it()
                    }
            }
        }
        snackBar.show()
    }

    fun showMessage(
        message: String,
        view: View,
        length: Int = Snackbar.LENGTH_SHORT,
        actionText: String? = null,
        action: (() -> Unit)? = null
    ) {
        val snackBar = Snackbar.make(view, message, length)
            .setTextColor(ContextCompat.getColor(view.context, R.color.white))
            .setBackgroundTint(ContextCompat.getColor(view.context, R.color.green))
        actionText?.let { s ->
            action?.let {
                snackBar.setActionTextColor(ContextCompat.getColor(view.context, R.color.white))
                    .setAction(s) {
                        it()
                    }
            }
        }
        snackBar.show()
    }

    fun showErrorMessage(message: String, context: Context) {
        StyleableToast.Builder(context)
            .text(message)
            .textColor(ContextCompat.getColor(context, R.color.white))
            .backgroundColor(ContextCompat.getColor(context, R.color.red))
            .iconStart(R.drawable.ic_toast_cancel)
            .font(R.font.font_regular)
            .show()
    }

    fun showMessage(message: String, context: Context, length: Int = Toast.LENGTH_SHORT) {
        StyleableToast.Builder(context)
            .text(message)
            .textColor(ContextCompat.getColor(context, R.color.white))
            .backgroundColor(ContextCompat.getColor(context, R.color.green))
            .iconStart(R.drawable.ic_baseline_check_circle_24)
            .font(R.font.font_regular)
            .length(length)
            .show()
    }
}
