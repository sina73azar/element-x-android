package io.element.android.x.Receipt

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.drp.shared_ui.BaseBottomSheet
import com.drp.shared_ui.model.receipt.ReceiptItem
import com.drp.utils.IMAGE
import com.drp.utils.ITEMS
import com.drp.utils.RECEIPTTITLE
import com.drp.utils.parcelableArrayList
import io.element.android.x.R
import io.element.android.x.databinding.FragmentShareBinding

class ShareFragment : BaseBottomSheet<FragmentShareBinding>(FragmentShareBinding::inflate) {
    lateinit var bundle: Bundle
    lateinit var receiptItems: List<ReceiptItem>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imgShare.iconImg.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_camera
            )
        )
        binding.txtShare.iconImg.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_info
            )
        )
        binding.imgShare.tvTitleSheet.text = getString(R.string.receipt_share_image)
        binding.txtShare.tvTitleSheet.text = getString(R.string.receipt_share_text)
        binding.btnClosePopUp.setOnClickListener {
            dismiss()
        }
        bundle = requireArguments()
        bundle.parcelableArrayList<ReceiptItem>(ITEMS)?.let {
            receiptItems = it
        }
        binding.imgShare.root.setOnClickListener {
            shareImage(requireActivity(), bundle.getString(IMAGE, ""))
            dismiss()
        }
        binding.txtShare.root.setOnClickListener {
            var title = getString(R.string.receipt_title)
            if (bundle.containsKey(RECEIPTTITLE)) {
                bundle.getString(RECEIPTTITLE)?.let { receiptTitle ->
                    title = receiptTitle
                }
            }
            if (::receiptItems.isInitialized) {
                if (receiptItems.any { it.title == "سری/سریال" }) {
                    val value =
                        receiptItems.findLast { it.title == "سری/سریال" }?.value
                    receiptItems.findLast { it.title == "سری/سریال" }?.value =
                        if (value?.split("\\")?.size!! > 1) {
                            val data = value.split("\\")
                            "${data.get(1)}/${data.get(0)}"
                        } else {
                            value
                        }
                }
                shareContent(
                    requireContext(), binding.tvTitleSheet.text.toString(),
                    shareTextReceipt(receiptItems, title)
                )
            }
            dismiss()
        }
    }

    companion object {
        const val TAG: String = "share_fragment"
    }
}
