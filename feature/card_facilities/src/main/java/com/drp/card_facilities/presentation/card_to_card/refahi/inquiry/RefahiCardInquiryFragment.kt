package com.drp.card_facilities.presentation.card_to_card.refahi.inquiry

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.drp.card_facilities.R
import com.drp.card_facilities.databinding.FragmentRefahiCardInquiryBinding
import com.drp.card_facilities.presentation.app_shared_viewmodel.SharedViewModelEvents
import com.drp.card_facilities.presentation.app_source_card_handler.SourceCardEvents
import com.drp.card_facilities.presentation.card_to_card.refahi.transfer.CardTransferFragment
import com.drp.card_facilities.presentation.card_to_card.transferInquiry
import com.drp.data.network.CustomResponse
import com.drp.refah.card_facilities.data.model.Trk2EquivData
import com.drp.refah.card_facilities.data.model.card_to_card.FundTransfer
import com.drp.refah.card_facilities.data.model.card_to_card.refahi.inquiry.InquiryCardResult
import com.drp.shared_ui.BaseFragment
import com.drp.shared_ui.ShowMessage.showErrorMessage
import com.drp.shared_ui.model.CardShotItemInfo
import com.drp.shared_ui.model.receipt.ReceiptItem
import com.drp.shared_ui.showNetworkFailDialog
import com.drp.shared_ui.theme.ApplicationTheme
import com.drp.shared_ui.widget.CardNumberShotWidget
import com.drp.utils.ITEMS
import com.drp.utils.RESULT
import com.drp.utils.SELECTED_CARD_KEY
import com.drp.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import ir.arefdev.irdebitcardscanner.ScanActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import com.drp.shared_ui.R as UIResources

@AndroidEntryPoint
class RefahiCardInquiryFragment :
    BaseFragment<FragmentRefahiCardInquiryBinding>(FragmentRefahiCardInquiryBinding::inflate) {
    private val viewModel: RefahiCardInquiryViewModel by viewModels()
    private var sourceCardShotItem: CardShotItemInfo? = null
    private lateinit var fund: FundTransfer
    private lateinit var trk2EquivData: Trk2EquivData

    companion object {
        const val TAG = "RefahiCardInquiryFragme"
        private const val MY_CAMERA_REQUEST_CODE = 100
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleSourceCard()
        handleDestinationCard()
        handleExpireDate()
        setupDestinationScanner()
        binding.btnInquiry.setOnClickListener {
            hideKeyboard()
            /**
             * source card validation
             * */
            if (sourceCardShotItem == null) {
                showErrorMessage(getString(R.string.invalid_source_card), requireActivity())
                return@setOnClickListener
            }
            /**
             * destination card validation
             * */
            val destination: String =
                binding.txtDestinationPan.text().toString().filter { it.isDigit() }
            if (!viewModel.checkPan(destination)) {
                showErrorMessage(getString(R.string.data_validation_pan), requireActivity())
                return@setOnClickListener
            }
            if (sourceCardShotItem?.pan == destination) {
                showErrorMessage(getString(R.string.same_source_dest_card), requireActivity())
                return@setOnClickListener
            }
            /**
             * amount validation
             * */
            val amount: Long = binding.etPrice.amount
            if (amount < 1) {
                showErrorMessage(getString(R.string.data_validation_amount), requireActivity())
                return@setOnClickListener
            }
            /**
             * cvv2 validation
             * */
            val cvv2: String = binding.etCVV.text().toString().trim()
            if (cvv2.length > 4 || cvv2.length < 2) {
                showErrorMessage(getString(R.string.data_validation_cvv2), requireActivity())
                return@setOnClickListener
            }
            /**
             * expire date
             * */
            val month = binding.spinnerMonth.getSelectedItem()
            val year = binding.spinnerYear.getSelectedItem()
                .substring(2, 4)
            trk2EquivData = Trk2EquivData(
                year + month, cvv2, ""
            )
            fund = FundTransfer(
                amount = amount,
                source = sourceCardShotItem!!.pan,
                sourceAccount = "",
                destination = destination,
            )
            viewModel.inquiry(fund, trk2EquivData)
        }
    }

    private fun handleSourceCard() {
        requireArguments().getString(SELECTED_CARD_KEY)?.let {
            sourceCardShotItem = Json.decodeFromString<CardShotItemInfo>(it)
            viewModel.sendSourceCardEvent(SourceCardEvents.SetSelectedCard(sourceCardShotItem))
        }
        binding.cardComposeShot.setContent {
            val months = resources.getStringArray(R.array.month).toList()
            val years = resources.getStringArray(R.array.years).toList()
            ApplicationTheme {
                val uiState = viewModel.sharedViewModelUiState.collectAsStateWithLifecycle().value
                CardNumberShotWidget(
                    modifier = Modifier.padding(bottom = dimensionResource(id = UIResources.dimen.medium_padding)),
                    selectedCard = uiState.sourceCardUiState.selectedCard,
                    cardsList = uiState.sharedViewModelUiState.cardsList,
                    onSelectedCardChange = { selectedCardShot ->
                        sourceCardShotItem = selectedCardShot
                        viewModel.sendSourceCardEvent(
                            SourceCardEvents.SetSelectedCard(
                                selectedCardShot
                            )
                        )
                        selectedCardShot?.panExpiryMonth?.let { expireMonth ->
                            selectedCardShot.panExpiryYear?.let { expireYear ->
                                years.filter { it.contains(expireYear) }.first()
                                    .let { fullExpireYear ->
                                        binding.spinnerMonth.setSelection(
                                            months.indexOf(
                                                expireMonth
                                            )
                                        )
                                        binding.spinnerYear.setSelection(
                                            years.indexOf(
                                                fullExpireYear
                                            )
                                        )
                                    }
                            }
                        } ?: run {
                            binding.spinnerMonth.setSelection(0)
                            binding.spinnerYear.setSelection(0)
                        }
                    },
                    horizontalPadding = dimensionResource(id = UIResources.dimen.large_padding),
                    onCardRemoved = {
                        viewModel.sendSharedViewModelEvent(SharedViewModelEvents.RemoveCard(it.id))
                    },
                    onCardSetToDefault = {
                        viewModel.sendSharedViewModelEvent(SharedViewModelEvents.SetToDefaultCard(it.id))
                    },
                    onCardEdited = {
                        viewModel.sendSharedViewModelEvent(SharedViewModelEvents.EditCard(it))
                    },
                    onCardAdd = {
                        viewModel.sendSharedViewModelEvent(SharedViewModelEvents.AddCard(it))
                    }
                )
            }
        }
    }

    private fun handleDestinationCard() {
        binding.txtDestinationPan.callDetectIban {
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.getCards(it)?.let {
                    withContext(Dispatchers.Main) {
                        binding.txtDestinationPan.setBankImage(it.cardNum.bank.imageURL)
                    }
                }
            }
        }
    }

    private fun setupDestinationScanner() {
        binding.linearBarcode.setOnClickListener {
            if (PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.CAMERA
                )
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.CAMERA,
                    ), MY_CAMERA_REQUEST_CODE
                )
            } else {
                ScanActivity.start(requireActivity())
            }
        }
    }

    private fun handleExpireDate() {
        binding.spinnerMonth.setData(
            items = resources.getStringArray(R.array.month).toList(),
            activity = requireActivity()
        )
        binding.spinnerYear.setData(
            items = resources.getStringArray(R.array.years).toList(),
            activity = requireActivity()
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (ScanActivity.isScanResult(requestCode) && resultCode == Activity.RESULT_OK && data != null) {
            val scanResult = ScanActivity.debitCardFromResult(data)
            scanResult?.let { debitCard ->
                debitCard.number?.let { number ->
                    binding.txtDestinationPan.setText(number)
                }
            }
        }
    }

    override fun dataObserver() {
        super.dataObserver()
        viewModel.cardInquiryResult.observe(viewLifecycleOwner) {
            when (it.status) {
                CustomResponse.Status.LOADING -> {
                    binding.btnInquiry.startAnimation()
                }

                CustomResponse.Status.ERROR -> {
                    binding.btnInquiry.revertAnimation()
                    it.message?.let { msg ->
                        showErrorMessage(msg, binding.root)
                    }
                }

                CustomResponse.Status.Fail -> {
                    binding.btnInquiry.revertAnimation()
                    requireActivity().showNetworkFailDialog()
                }

                CustomResponse.Status.SUCCESS -> {
                    binding.btnInquiry.revertAnimation()
                    it.data?.let { data ->
                        data.amount = binding.etPrice.amount
                        if (::fund.isInitialized)
                            data.fundTransfer = fund
                        data.trk2EquivData = trk2EquivData
                        showInquiry(data)
                    }
                }
            }
        }
        viewModel.panContacts.observe(viewLifecycleOwner) {
            if (it.status == CustomResponse.Status.SUCCESS) {
                it.data?.let { it1 ->
                    binding.txtDestinationPan.setData(
                        it1,
                        activity = requireActivity() as AppCompatActivity,
                        showEditContactBtn = false
                    )
                }
            }
        }
    }

    private fun showInquiry(inquiry: InquiryCardResult) {
        val receiptItems: List<ReceiptItem> = transferInquiry(inquiry)
        val bundle = Bundle()
        bundle.putParcelableArrayList(ITEMS, receiptItems as java.util.ArrayList<out Parcelable>)
        bundle.putSerializable(RESULT, inquiry)
        sourceCardShotItem?.let { CardTransferFragment(it) }?.let {
            // TODO: do it next
//            requireActivity().instanceBottomSheet(
//                it,
//                CardTransferFragment.TAG,
//                bundle,
//            )
        }
    }
}