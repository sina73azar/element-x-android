package com.drp.shared_ui.receipt

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager

import com.drp.shared_ui.BaseBottomSheet
import com.drp.shared_ui.R
import com.drp.shared_ui.databinding.FragmentReceiptBinding
import com.drp.shared_ui.model.receipt.ReceiptItem
import com.drp.shared_ui.model.receipt.ReceiptType

import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.drp.shared_ui.navigation.sheet.NavigationSheet
import com.drp.shared_ui.navigation.sheet.navigationToDestinationSheet
import com.drp.utils.CHEQUE_SUBMIT_RECEIPT
import com.drp.utils.COMPOSE_RECEIPT
import com.drp.utils.HISTORYRECEIPT
import com.drp.utils.IMAGE
import com.drp.utils.ITEMS
import com.drp.utils.PICHACK
import com.drp.utils.RECEIPTMODE
import com.drp.utils.RECEIPTTITLE
import com.drp.utils.accountMaskFormatter
import com.drp.utils.panMaskFormatter
import com.drp.utils.parcelableArrayList

class ReceiptFragment : BaseBottomSheet<FragmentReceiptBinding>(FragmentReceiptBinding::inflate) {
    lateinit var bundle: Bundle
    lateinit var receiptItems: List<ReceiptItem>
    lateinit var adapter: ReceiptAdapter

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (PackageManager.PERMISSION_GRANTED !=
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 2000
            )
        }
        setRecycler()
        binding.btnClosePopUp.setOnClickListener {
            dismiss()
        }
        bundle = requireArguments()

        bundle.parcelableArrayList<ReceiptItem>(ITEMS)?.let {
            receiptItems = it
            if (::receiptItems.isInitialized) {
                if (receiptItems.any { it.title == getString(R.string.receipt_fragment_source_account_title) }) {
                    val value =
                        receiptItems.findLast { it.title == getString(R.string.receipt_fragment_source_account_title) }?.value
                    receiptItems.findLast { it.title == getString(R.string.receipt_fragment_source_account_title) }?.value =
                        accountMaskFormatter(value)
                }
                receiptItems.onEach { receiptItem ->
                    if (receiptItem.type == ReceiptType.CARD) {
                        val value = receiptItem.value?.filter { it.isDigit() }
                        receiptItem.value = panMaskFormatter(value)
                    }
                }
                if (::adapter.isInitialized && ::receiptItems.isInitialized)
                    adapter.updateList(receiptItems)
            }
        }
        if (bundle.containsKey(RECEIPTTITLE)) {
            binding.tvTitleSheet.text = bundle.getString(RECEIPTTITLE)
        }
        if (bundle.containsKey(RECEIPTMODE) && bundle.getBoolean(RECEIPTMODE) || bundle.containsKey(
                HISTORYRECEIPT
            ) && bundle.getBoolean(HISTORYRECEIPT)
        ) {
            binding.rvReceipt.visibility = View.GONE
            binding.transactionReceipt.constraintReceiptTrans.visibility = View.VISIBLE
            if (::receiptItems.isInitialized)
                binding.transactionReceipt.tvAmount.text =
                    receiptItems.find { item -> item.type == ReceiptType.AMOUNT }?.value + "  " + getString(
                        R.string.irr_currency
                    )
            val layoutManager = LinearLayoutManager(requireContext())
            binding.transactionReceipt.rvReceiptTrans.adapter = adapter
            binding.transactionReceipt.rvReceiptTrans.layoutManager = layoutManager
            binding.shareLinear.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
        }
        binding.shareLinear.setOnClickListener {
            if (binding.transactionReceipt.constraintReceiptTrans.visibility == View.VISIBLE)
                bundle.putString(IMAGE, captureImage(binding.transactionReceipt.root))
            else
                bundle.putString(IMAGE, captureImage(binding.rvReceipt))
            requireActivity().navigationToDestinationSheet(
                NavigationSheet.ShareFragment.path,
                bundle
            )
        }

        if (bundle.containsKey(CHEQUE_SUBMIT_RECEIPT) && bundle.getBoolean(CHEQUE_SUBMIT_RECEIPT)) {
            binding.shareLinear.visibility = View.GONE
        }

    }

    private fun setRecycler() {
        val layoutManager = LinearLayoutManager(requireContext())
        adapter = ReceiptAdapter()
        binding.rvReceipt.adapter = adapter
        binding.rvReceipt.layoutManager = layoutManager
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (bundle.containsKey(PICHACK) && bundle.getBoolean(PICHACK)) {
            requireActivity().finish()
        }
        if (bundle.containsKey(COMPOSE_RECEIPT) && bundle.getBoolean(COMPOSE_RECEIPT)) {
            requireActivity().supportFragmentManager.popBackStack()
            return
        }
        if (bundle.containsKey(RECEIPTMODE) && bundle.getBoolean(RECEIPTMODE)) {
            requireActivity().finish()
        }
        if (bundle.containsKey(CHEQUE_SUBMIT_RECEIPT) && bundle.getBoolean(CHEQUE_SUBMIT_RECEIPT)) {
            requireActivity().supportFragmentManager.popBackStack()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { layout ->
                val behaviour = BottomSheetBehavior.from(layout)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        return dialog
    }

    companion object {
        val TAG: String = "receipt_sheet"
    }
}