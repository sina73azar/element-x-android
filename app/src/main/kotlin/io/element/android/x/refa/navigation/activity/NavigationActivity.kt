package com.drp.shared_ui.navigation.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity



fun FragmentActivity.navigationToDestinationActivity(
    navigationPath: String,
    bundle: Bundle? = Bundle(),
    launcher: ActivityResultLauncher<Intent>? = null
) {
    try {
        val intent = Intent(
            this,
            Class.forName(navigationPath)
        )
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        if (launcher != null)
            launcher.launch(intent)
        else
            this.startActivity(intent)
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }
}

