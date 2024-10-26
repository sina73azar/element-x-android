package com.drp.shared_ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.drp.utils.getScreenSize

//@AndroidEntryPoint
open class BaseDialog<VB: ViewBinding>(val inflate: Inflate<VB>) : DialogFragment() {
    private var _binding:VB?=null
    val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=inflate.invoke(inflater,container,false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = RelativeLayout(activity)
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        setDisplayMetrics(dialog)
        dialog.setCanceledOnTouchOutside(false)
        return dialog

    }
    private fun setDisplayMetrics(dialog: Dialog){
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setLayout(
                (getScreenSize(requireContext()).width * 0.85).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }
}