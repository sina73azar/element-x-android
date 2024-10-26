package com.drp.shared_ui.navigation.sheet

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


inline fun <reified T : BottomSheetDialogFragment> FragmentActivity.instanceBottomSheet(
    bottomSheetDialogFragment: T,
    tag: String,
    bundle: Bundle = Bundle(),
    isCancelable: Boolean? = true
) {
    val myFragment = supportFragmentManager.findFragmentByTag(tag)
    if (myFragment != null && myFragment.isAdded) {
        return
    } else {
        val sheetFragment =
            supportFragmentManager.findFragmentByTag(tag) ?: bottomSheetDialogFragment
        (sheetFragment as BottomSheetDialogFragment).apply {
            this.arguments = bundle
            this.isCancelable = isCancelable!!
            supportFragmentManager?.let {
                this.show(it, tag)
            }

        }
    }
}