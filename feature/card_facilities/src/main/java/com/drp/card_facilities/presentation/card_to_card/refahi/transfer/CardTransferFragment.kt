package com.drp.card_facilities.presentation.card_to_card.refahi.transfer

//import com.google.android.gms.auth.api.phone.SmsRetriever

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.drp.card_facilities.R
import com.drp.card_facilities.databinding.FragmentCardTransferBinding
import com.drp.data.network.CustomResponse
import com.drp.refah.card_facilities.data.model.CardInfo
import com.drp.refah.card_facilities.data.model.Trk2EquivData
import com.drp.refah.card_facilities.data.model.bill.otp.DestinationCardInfo
import com.drp.refah.card_facilities.data.model.card_to_card.refahi.inquiry.InquiryCardResult
import com.drp.refah.ui.data.enums.SecondAuthenticationMethod
import com.drp.shared_ui.model.CardShotItemInfo
import com.drp.shared_ui.model.receipt.ReceiptItem
import com.drp.shared_ui.receipt.ReceiptAdapter
import com.drp.shared_ui.BaseBottomSheet
import com.drp.shared_ui.ShowMessage.showErrorMessage
import com.drp.shared_ui.showNetworkFailDialog
import com.drp.shared_ui.widget.xml.PasswordEditText
import com.drp.utils.ITEMS
import com.drp.utils.RECEIPTMODE
import com.drp.utils.RESULT
import com.drp.utils.TIMER
import com.drp.utils.hideKeyboard
import com.drp.utils.millisToDateConvert
import com.drp.utils.millisToTimeConvert
import com.drp.utils.parcelableArrayList
import com.drp.utils.serializable
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CardTransferFragment(private val cardShotItemInfo: CardShotItemInfo) :
    BaseBottomSheet<FragmentCardTransferBinding>(FragmentCardTransferBinding::inflate) {
    lateinit var bundle: Bundle
    lateinit var inquiryResult: InquiryCardResult
    lateinit var receiptItems: MutableList<ReceiptItem>
    lateinit var adapter: ReceiptAdapter

    val viewModel: CardTransferViewModel by viewModels()

    companion object {
        const val TAG = "card_transfer_fragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecycler()
        bundle = requireArguments()
        bundle.serializable<InquiryCardResult>(RESULT)?.let {
            inquiryResult = it
        }
        bundle.parcelableArrayList<ReceiptItem>(ITEMS)?.let {
            receiptItems = it
            if (::adapter.isInitialized)
                adapter.updateList(receiptItems)
        }
        binding.btnClosePopUp.setOnClickListener {
            dismiss()
        }
        binding.layoutPassword.setPasswordType(SecondAuthenticationMethod.DYNAMIC.value)
        binding.layoutPassword.goneTooltip()
        binding.btnDismiss.setOnClickListener { dismiss() }
        binding.layoutPassword.selectTopUpButton(object : PasswordEditText.ClickButton {
            override fun setOnClickListener() {
//                SmsRetriever.getClient(requireContext()).startSmsUserConsent(null)
//                readSMS()
                if (::inquiryResult.isInitialized) {
                    val card = CardInfo(
                        sourceAccount = inquiryResult.fundTransfer.sourceAccount,
                        sourceCardNumber = inquiryResult.fundTransfer.source
                    )
                    val trk2EquivData = Trk2EquivData(
                        inquiryResult.trk2EquivData.expireDate,
                        inquiryResult.trk2EquivData.cvv2,
                        ""
                    )
                    val destCard = DestinationCardInfo(
                        inquiryResult.fundTransfer.destination
                    )
                    viewModel.totp(
                        card,
                        Gson().toJson(destCard),
                        trk2EquivData,
                        inquiryResult.amount
                    )
                }
            }

        }, requireActivity())
        binding.btnAccept.setOnClickListener {
            hideKeyboard()
            val pin: String = binding.layoutPassword.text().toString().trim()
            if (pin.length < 5) {
                binding.layoutPassword.showError()
                showErrorMessage(getString(R.string.data_validation_pin), binding.root)
                return@setOnClickListener
            }
            if (::inquiryResult.isInitialized) {
                inquiryResult.trk2EquivData.pin = pin
                inquiryResult.fundTransfer.personName = inquiryResult.customerName
                viewModel.transfer(
                    inquiryResult.fundTransfer,
                    inquiryResult.trk2EquivData,
                    cardShotItemInfo
                )
            }
        }


    }

    override fun dataObserver() {
        super.dataObserver()

        viewModel.totpResult.observe(viewLifecycleOwner) {
            binding.layoutPassword.revertBtnAnimation()
            when (it.status) {
                CustomResponse.Status.SUCCESS -> {
                    binding.layoutPassword.startTimeCounter((TIMER * 1000).toLong())
                }

                CustomResponse.Status.ERROR -> {
                    it.message?.let { errorMessage -> showErrorMessage(errorMessage, binding.root) }
                }

                else -> {
                    requireActivity().showNetworkFailDialog()
                }
            }
        }
        viewModel.transferResult.observe(viewLifecycleOwner) {
            when (it.status) {
                CustomResponse.Status.SUCCESS -> {
                    binding.btnAccept.revertAnimation()
                    dismiss()
                    bundle.putBoolean(RECEIPTMODE, true)
                    if (::receiptItems.isInitialized) {
                        receiptItems.add(
                            ReceiptItem(
                                5,
                                getString(R.string.date),
                                it.data?.fundTransfer?.date?.let { it1 -> millisToDateConvert(it1) }
                            )
                        )
                        receiptItems.add(
                            ReceiptItem(
                                6,
                                getString(R.string.hour),
                                it.data?.fundTransfer?.date?.let { it1 -> millisToTimeConvert(it1) }
                            )
                        )
                        receiptItems.add(
                            ReceiptItem(
                                7, getString(R.string.cheque_followup_code),
                                it.data?.fundTransfer?.followupCode?.trim()?.takeLast(6)
                            )
                        )
                    }
                    // TODO: fix it
//                    requireActivity().instanceBottomSheet(
//                        ReceiptFragment(),
//                        ReceiptFragment.TAG,
//                        bundle
//                    )
                    dismiss()
                }

                CustomResponse.Status.ERROR -> {
                    binding.btnAccept.revertAnimation()
                    it.message?.let { errorMessage -> showErrorMessage(errorMessage, binding.root) }
                }

                CustomResponse.Status.Fail -> {
                    binding.btnAccept.revertAnimation()
                    requireActivity().showNetworkFailDialog()


                }

                CustomResponse.Status.LOADING -> {
                    hideKeyboard()
                    binding.btnAccept.startAnimation()
                }
            }
        }

    }

    private fun setRecycler() {
        val layoutManager = LinearLayoutManager(requireContext())
        adapter = ReceiptAdapter()
        binding.rvReceipt.adapter = adapter
        binding.rvReceipt.layoutManager = layoutManager
    }


    override fun onStop() {
        super.onStop()
        binding.layoutPassword.onStop()
    }

    override fun onResume() {
        super.onResume()
        binding.layoutPassword.onResume()
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
            bottomSheetBehavior.isDraggable = false
        }
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

}
