package com.drp.superapp.shahkar.otp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import com.drp.data.network.CustomResponse
import com.drp.shared_ui.ShowMessage.showErrorMessage
import com.drp.shared_ui.ShowMessage.showMessage
import com.drp.superapp.shahkar.R
import com.drp.superapp.shahkar.databinding.FragmentShahkarOtpBinding
import com.drp.superapp.util.ChangeLanguage
import com.drp.superapp.util.Commons.parseCode
import com.drp.superapp.util.Constants.BIRTH_DATE
import com.drp.superapp.util.Constants.IMEI
import com.drp.superapp.util.Constants.NATIONAL_CODE
import com.drp.superapp.util.Constants.PHONE_NUMBER
import com.drp.superapp.util.Constants.REQUEST_ID
import com.drp.superapp.util.Constants.TIMER
import com.drp.utils.hideKeyboard
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

@AndroidEntryPoint
class ShahkarOtpSheetFragment(val success: (accessToken: String, walletId: Long, personId: Long) -> Unit) :
    BottomSheetDialogFragment() {
    private lateinit var countDownTimer: CountDownTimer
    private val shahkarOtpSheetViewModel: ShahkarOtpSheetViewModel by viewModels()
    private var total by Delegates.notNull<Long>()
    lateinit var nationalCode: String
    lateinit var updateUIReceiver: BroadcastReceiver
    private var semaphore = true
    lateinit var mobileNo: String
    lateinit var imei: String
    lateinit var requestId: String
    lateinit var birthDate: String

    companion object {
        const val TAG = "ShahkarOtpSheetFragment"
    }

    private var _binding: FragmentShahkarOtpBinding? = null
    private val binding get() = _binding!!
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideKeyboard()
        ChangeLanguage.wrap(requireContext(), Locale("fa"))
        dataObserver()
        _binding = FragmentShahkarOtpBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        requireArguments().getString(NATIONAL_CODE)?.let {
            nationalCode = it
        }
        requireArguments().getString(PHONE_NUMBER)?.let {
            mobileNo = it
        }
        requireArguments().getString(IMEI)?.let {
            imei = it
        }
        requireArguments().getString(REQUEST_ID)?.let {
            requestId = it
        }
        requireArguments().getString(BIRTH_DATE)?.let {
            birthDate = it
        }
        readMessage()
        total = (TIMER * 1000).toLong()
        startTimeCounter()
        // TODO activate this for production mode
//        binding.activationCodeEt.setReadOnlyText(true)
        binding.dataConstraint.visibility = View.VISIBLE
        binding.verificationCodeTv.text =
            HtmlCompat.fromHtml(
                getString(R.string.activation_number_title_st, mobileNo),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        binding.dismissBtn.setOnClickListener {
            dismiss()
        }
        binding.retryLoadingBtn.setOnClickListener {
            showRetryBtn(true)
            hideKeyboard()
            if (::nationalCode.isInitialized && ::birthDate.isInitialized && ::mobileNo.isInitialized && ::imei.isInitialized)
                shahkarOtpSheetViewModel.authRegister(
                    nationalCode,
                    mobileNo,
                    imei,
                    birthDate
                )
            readMessage()
        }

        binding.submitBtn.setOnClickListener {
            val token = binding.activationCodeEt.text().toString().trim()
            if (shahkarOtpSheetViewModel.isValidSMSToken(token) && ::requestId.isInitialized) {
                shahkarOtpSheetViewModel.authValidate(mobileNo, token, requestId)
            } else {
                binding.activationCodeEt.showError()
                showErrorMessage(
                    getString(R.string.otp_register_failure_description_st),
                    binding.root
                )
            }
        }
    }

    private fun dataObserver() {
        shahkarOtpSheetViewModel.register.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                CustomResponse.Status.LOADING -> binding.retryLoadingBtn.startAnimation()
                CustomResponse.Status.ERROR -> {
                    response.message?.let { errorMessage ->
                        showErrorMessage(
                            errorMessage,
                            binding.root
                        )
                    }
                    binding.retryLoadingBtn.revertAnimation()
                }

                CustomResponse.Status.Fail -> {
                    showErrorMessage(
                        getString(R.string.network_error_st),
                        binding.root
                    )
                    binding.retryLoadingBtn.revertAnimation()
                }

                CustomResponse.Status.SUCCESS -> {
                    showRetryBtn(false)
                    requestId = response.data?.requestID!!
                    total = (TIMER * 1000).toLong()
                    startTimeCounter()
                    binding.retryLoadingBtn.revertAnimation()
                }
            }
        }
        shahkarOtpSheetViewModel.validate.observe(viewLifecycleOwner) {
            when (it.status) {
                CustomResponse.Status.LOADING -> {
                    binding.submitBtn.startAnimation()
                    hideKeyboard()
                }

                CustomResponse.Status.ERROR -> {
                    semaphore = true
                    binding.submitBtn.revertAnimation()
                    it.message?.let { errorMessage ->
                        showErrorMessage(
                            errorMessage,
                            binding.root
                        )
                    }
                }

                CustomResponse.Status.Fail -> {
                    semaphore = true
                    binding.submitBtn.revertAnimation()
                    showErrorMessage(
                        getString(R.string.network_error_st),
                        binding.root
                    )
                }

                CustomResponse.Status.SUCCESS -> {
                    binding.submitBtn.revertAnimation()
                    it.data?.let { data ->
                        success.invoke(
                            data.result.pAccessToken!!,
                            data.result.pWalletId!!,
                            data.result.pPersonId!!
                            /*result.refreshToken,
                            (result.iat + result.expiresIn) * 1000*/
                        )
                    }
                    dismiss()
                }

            }
        }
    }

    private fun checkPermission() {
        if (PackageManager.PERMISSION_GRANTED !=
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECEIVE_SMS
            )
        ) {
            binding.permissionLayout.visibility = View.VISIBLE
            val ss = SpannableString(getString(R.string.activation_otp_info_st))
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(p0: View) {
                    requestPermissions(
                        arrayOf(Manifest.permission.RECEIVE_SMS), 1000
                    )
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = true
                }
            }
            ss.setSpan(clickableSpan, 25, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.infoSmsTv.text = ss
            binding.infoSmsTv.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun readMessage() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECEIVE_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val filter = IntentFilter()
            filter.addAction("service.to.activity.transfer")
            updateUIReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    //UI update here
                    if (intent != null) {
                        val messageTxt = intent.getStringExtra("message").toString()
                        if (messageTxt.isNotEmpty() && semaphore) {
                            semaphore = false
                            val token = parseCode(messageTxt, 4)
                            if (shahkarOtpSheetViewModel.isValidSMSToken(token)) {
                                // TODO valid otp functionality
//                                shahkarOtpSheetViewModel.authValidate(username, mobileNo, token)
                            } else {
                                binding.activationCodeEt.showError()
                                showErrorMessage(
                                    getString(R.string.otp_register_failure_description_st),
                                    binding.root
                                )
                            }
                            binding.activationCodeEt.setText(token)

                        }
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requireContext().registerReceiver(
                    updateUIReceiver,
                    filter,
                    Manifest.permission.INTERNET,
                    null,
                    Context.RECEIVER_EXPORTED
                )
            } else
                requireContext().registerReceiver(
                    updateUIReceiver,
                    filter,
                    Manifest.permission.INTERNET,
                    null
                )
        }
    }

    private fun startTimeCounter() {
        countDownTimer = object : CountDownTimer(total, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val time = millisUntilFinished - 1000
                total = millisUntilFinished
                binding.timerCountTv.text = String.format(
                    Locale.getDefault(), "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(time),
                    TimeUnit.MILLISECONDS.toSeconds(time) -
                            TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    time
                                )
                            )
                )
            }

            override fun onFinish() {
                showRetryBtn(true)
                binding.retryLoadingBtn.text = getString(R.string.retry_code_st)

            }

        }.start()
    }

    fun showRetryBtn(show: Boolean) {
        if (show) {
            binding.timerCountTv.visibility = View.INVISIBLE
            binding.timerIconImg.visibility = View.INVISIBLE
            binding.retryLoadingBtn.visibility = View.VISIBLE
        } else {
            binding.timerCountTv.visibility = View.VISIBLE
            binding.timerIconImg.visibility = View.VISIBLE
            binding.retryLoadingBtn.visibility = View.INVISIBLE
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        hideKeyboard()
    }

    override fun onStop() {
        super.onStop()
        if (::countDownTimer.isInitialized)
            countDownTimer.cancel()
    }

    override fun onResume() {
        super.onResume()
        if (::countDownTimer.isInitialized)
            startTimeCounter()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000 && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.permissionLayout.visibility = View.GONE
                showMessage(
                    getString(R.string.request_again_st),
                    binding.root
                )
            }
        }
    }
}