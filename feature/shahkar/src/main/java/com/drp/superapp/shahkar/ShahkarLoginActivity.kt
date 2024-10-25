package com.drp.superapp.shahkar

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.drp.data.model.ShahkarUserData
import com.drp.data.network.CustomResponse
import com.drp.shared_ui.BaseActivity
import com.drp.shared_ui.ShowMessage.showErrorMessage
import com.drp.shared_ui.showNetworkFailDialog
import com.drp.shared_ui.widget.xml.DateInput
import com.drp.superapp.callback.OpenShahkarLogin
import com.drp.superapp.shahkar.databinding.ActivityShahkarLoginBinding
import com.drp.superapp.shahkar.otp.ShahkarOtpSheetFragment
import com.drp.superapp.util.Constants
import com.drp.superapp.util.Constants.REQUEST_ID
import com.drp.utils.hideKeyboard
import com.drp.utils.logger
import com.xdev.arch.persiancalendar.datepicker.calendar.PersianCalendar
import dagger.hilt.android.AndroidEntryPoint
import saman.zamani.persiandate.PersianDate

@AndroidEntryPoint
internal class ShahkarLoginActivity :
    BaseActivity<ActivityShahkarLoginBinding>(ActivityShahkarLoginBinding::inflate) {
    private val viewModel: ShahkarLoginViewModel by viewModels()
    private lateinit var nationalCode: String
    private lateinit var phoneNumber: String
    private lateinit var birthDate: PersianDate
    private lateinit var imei: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checkPermission()
        setupViews()
        dataObserver()
    }
    @SuppressLint("HardwareIds")
    private fun setupViews() {
        onBackPressedDispatcher.addCallback(
            this@ShahkarLoginActivity,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    logger(" ShahkarLoginActivity")
                    finishAffinity()
                }
            })
        binding.birthDateEt.dateSelected(object : DateInput.SelectDate {

            override fun chooseDate(persianCalendar: PersianDate) {
                birthDate = persianCalendar
            }
        }, this)
        binding.loginLoadingBtn.setOnClickListener {
            hideKeyboard()
            nationalCode = binding.nationalCodeEt.text().toString().trim()
            phoneNumber = binding.phoneNumberEt.text().toString().filter { it.isDigit() }
            if (nationalCode.isEmpty() || !viewModel.checkNationalCode(nationalCode)) {
                binding.nationalCodeEt.showError()
                showErrorMessage(
                    getString(R.string.data_validation_national_code_st),
                    binding.root
                )
                return@setOnClickListener
            }
            if (phoneNumber.isEmpty() || !viewModel.checkMobileNo(phoneNumber)) {
                binding.phoneNumberEt.showError()
                showErrorMessage(
                    getString(R.string.mobile_no_register_failure_title_st),
                    binding.root
                )
                return@setOnClickListener
            }
            if (!::birthDate.isInitialized) {
                showErrorMessage(
                    getString(R.string.data_validation_birth_date_st),
                    binding.root
                )
                return@setOnClickListener
            }
            /*OpenShahkarLogin.getExposeLoggedInUserData()?.let {
                it(ShahkarUserData(nationalCode, phoneNumber))
                finish()
            }*/
            try {
                imei = Settings.Secure.getString(
                    contentResolver,
                    Settings.Secure.ANDROID_ID
                )
                viewModel.inquiry(
                    nationalCode,
                    phoneNumber,
                    convertToLongPersianDate(birthDate),
                    imei
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun checkPermission() {
        if (PackageManager.PERMISSION_GRANTED !=
            ContextCompat.checkSelfPermission(
                this@ShahkarLoginActivity,
                Manifest.permission.RECEIVE_SMS
            )
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(Manifest.permission.RECEIVE_SMS), 1000
                )
            }
        }
    }

    override fun dataObserver() {
        viewModel.inquiryResult.observe(this@ShahkarLoginActivity) { response ->
            when (response.status) {
                CustomResponse.Status.LOADING -> {
                    binding.loginLoadingBtn.startAnimation()
                }

                CustomResponse.Status.ERROR -> {
                    response.message?.let { errorMessage ->
                        showErrorMessage(errorMessage, binding.root)
                    }
                    binding.loginLoadingBtn.revertAnimation()
                }

                CustomResponse.Status.Fail -> {
                    showNetworkFailDialog()
                    binding.loginLoadingBtn.revertAnimation()
                }

                CustomResponse.Status.SUCCESS -> {
                    if (::imei.isInitialized) {
                        binding.loginLoadingBtn.revertAnimation()
                        val bundle = Bundle()
                        bundle.putString(Constants.NATIONAL_CODE, nationalCode)
                        bundle.putString(Constants.PHONE_NUMBER, phoneNumber)
                        bundle.putString(Constants.IMEI, imei)
                        bundle.putString(REQUEST_ID, response.data?.result?.pRqId?.toString())
                        bundle.putString(Constants.BIRTH_DATE, convertToLongPersianDate(birthDate))
                        ShahkarOtpSheetFragment { accessToken, walletId, personId ->
                            val shahkarUserData = ShahkarUserData(
                                nationalCode,
                                phoneNumber,
                                accessToken,
                                walletId,
                                personId
                            )
                            OpenShahkarLogin.getExposeShahkarLoginState()?.let {
                                OpenShahkarLogin.setShahkarUserData(shahkarUserData)
                                it.onSuccess(shahkarUserData)
                                finish()
                            }
                        }.apply {
                            arguments = bundle
                        }.show(supportFragmentManager, ShahkarOtpSheetFragment.TAG)
                    }

                }
            }
        }
    }

    private fun convertToLongPersianDate(date: PersianDate): String {
        return "${date.shYear}".plus(if (date.shMonth < 10) "0${date.shMonth}" else "${date.shMonth}")
            .plus(if (date.shDay < 10) "0${date.shDay}" else "${date.shDay}")
    }
}