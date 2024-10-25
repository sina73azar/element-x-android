package com.drp.shared_ui.navigation.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity

inline fun <reified T : Activity> FragmentActivity.navigateInternalActivity(
    activty: T,
    bundle: Bundle? = Bundle(),
    launcher: ActivityResultLauncher<Intent>? = null
) {
    val intent = Intent(this, activty::class.java)
    if (bundle != null) {
        intent.putExtras(bundle)
    }
    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
    if (launcher != null)
        launcher.launch(intent)
    else
        this.startActivity(intent)
}