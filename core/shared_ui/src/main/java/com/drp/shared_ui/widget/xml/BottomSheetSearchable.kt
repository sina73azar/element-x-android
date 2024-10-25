package com.drp.shared_ui.widget.xml

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.SearchView
import com.drp.shared_ui.BaseBottomSheet
import com.drp.shared_ui.databinding.BottomSearchableItemsBinding
import com.drp.shared_ui.model.ComboModel
import com.drp.utils.ITEMS
import com.drp.utils.TITLE
import com.drp.utils.TYPE
import com.drp.utils.hideKeyboard
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.internal.ViewUtils.hideKeyboard


class BottomSheetSearchable(val onItemSelected: (ComboModel) -> Unit) :
    BaseBottomSheet<BottomSearchableItemsBinding>(BottomSearchableItemsBinding::inflate) {

    lateinit var data: List<ComboModel>
    lateinit var header: String
    lateinit var direction: String
    private val comboAdapter: ComboAdapter by lazy {
        ComboAdapter {
            onItemSelected.invoke(it)
            dismissAllowingStateLoss()
        }
    }

override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    hideKeyboard()
    val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
    dialog.setOnShowListener {
        val d = it as BottomSheetDialog
        val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }
    return dialog
}

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        hideKeyboard()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        data = requireArguments().getParcelableArrayList<ComboModel>(ITEMS)!!
        header = requireArguments().getString(TITLE)!!
        direction = requireArguments().getString(TYPE)!!
        binding.tvTitleSheet.text = header
        binding.rvSpinner.adapter = comboAdapter
        if (::data.isInitialized && ::direction.isInitialized)
            comboAdapter.updateList(data, direction)
        binding.btnClosePopUp.setOnClickListener {
            dismissAllowingStateLoss()
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    comboAdapter.updateList(data, direction)
                    return false
                }
                val filtered = data.filter { model -> model.title.contains(newText, true) }
                comboAdapter.updateList(filtered, direction)
                return true
            }
        })
        hideKeyboard()
    }

    companion object {
        const val TAG: String = "searchable_sheet"
    }


}