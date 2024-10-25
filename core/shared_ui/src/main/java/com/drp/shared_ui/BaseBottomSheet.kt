package com.drp.shared_ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.drp.utils.hideKeyboard
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


open class BaseBottomSheet<VB : ViewBinding>(val inflate: Inflate<VB>) : BottomSheetDialogFragment() {
    private var _binding: VB? = null
    val binding get() = _binding!!
    lateinit var activityResultLauncher:
            ActivityResultLauncher<Intent>
    var requestCode = -1

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hideKeyboard()
        activityResultLauncher =
            (this as DialogFragment).registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (requestCode != -1)
                    activityResult(requestCode, result)
            }
//        ChangeLanguage.wrap(requireContext(), Locale("fa"))
        dataObserver()
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        hideKeyboard()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        hideKeyboard()
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        return dialog
    }



    open fun dataObserver() {
        // observing data inside BottomSheets
    }

    protected open fun activityResult(requestCode: Int, result: ActivityResult) {
        // getting activity result inside BottomSheets
    }
}
typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T