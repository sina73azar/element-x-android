package com.drp.shared_ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings.ACTION_WIRELESS_SETTINGS
import android.view.View
import androidx.fragment.app.FragmentActivity
import io.element.android.x.databinding.DialogNetworkBinding

class FailNetworkDialog : BaseDialog<DialogNetworkBinding>(DialogNetworkBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnDismiss.setOnClickListener { dismiss() }
        binding.btnConfirm.setOnClickListener {
            val intent = Intent(ACTION_WIRELESS_SETTINGS)
            startActivity(intent)
            dismiss()
        }
    }
}

fun FragmentActivity.showNetworkFailDialog() {
    FailNetworkDialog().show(supportFragmentManager, "network_dialog")
}
