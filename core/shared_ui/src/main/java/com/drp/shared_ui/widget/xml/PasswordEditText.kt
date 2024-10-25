package com.drp.shared_ui.widget.xml

//import com.google.android.gms.auth.api.phone.SmsRetriever
import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.drp.refah.ui.data.enums.SecondAuthenticationMethod
import com.drp.shared_ui.BuildConfig
import com.drp.shared_ui.R
import com.drp.shared_ui.databinding.LayoutSecondPasswordBinding
import com.google.android.material.textfield.TextInputLayout
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltipUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import java.util.StringTokenizer
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates


@SuppressLint("ResourceAsColor")
class PasswordEditText(context: Context, attrs: AttributeSet?) : ConstraintLayout(
    context,
    attrs
), TextWatcher {
    interface ClickButton {
        fun setOnClickListener()
    }

    lateinit var myPasswordType: String
    lateinit var activity: FragmentActivity
    lateinit var clickButton: ClickButton
    private var total by Delegates.notNull<Long>()
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var updateUIReceiver: BroadcastReceiver
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    val binding =
        LayoutSecondPasswordBinding.bind(inflate(context, R.layout.layout_second_password, this))

    init {
        val myAttrs = context.theme.obtainStyledAttributes(attrs, R.styleable.bank_view, 0, 0)
        val maxlength = myAttrs.getInteger(R.styleable.bank_view_android_maxLength, 18)
        binding.etActivationCode.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxlength))
        binding.etActivationCode.transformationMethod = PasswordTransformationMethod.getInstance()
        binding.ivInfoPassword.setOnClickListener {
            SimpleTooltip.Builder(context)
                .anchorView(binding.ivInfoPassword)
                .text(context.getString(R.string.tooltip_txt))
                .gravity(Gravity.TOP)
                .textColor(Color.WHITE)
                .animated(true)
                .transparentOverlay(false)
                .arrowHeight(SimpleTooltipUtils.pxFromDp(20F))
                .arrowWidth(SimpleTooltipUtils.pxFromDp(30F))
                .build()
                .show()

        }
        binding.etActivationCode.addTextChangedListener(this)
        binding.topupBtn.setOnClickListener {
            binding.topupBtn.startAnimation()
            if (::clickButton.isInitialized)
                clickButton.setOnClickListener()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun setReadOnlyText(readonly: Boolean) {
        if (readonly) {
            binding.txtValueIl.endIconMode = TextInputLayout.END_ICON_NONE
        }
        binding.txtValueIl.isEnabled = BuildConfig.DEBUG
    }

    fun goneTooltip() {
        binding.ivInfoPassword.visibility = View.GONE
    }

    fun text(): Editable? {
        return binding.etActivationCode.text
    }

    fun setHint(hint: String) {
        binding.txtValueIl.hint = hint
    }

    fun setText(value: String) {
        binding.etActivationCode.setText(value)
    }

    fun startTimeCounter(totalTime: Long? = null) {
        binding.topupBtn.revertAnimation()
        binding.topupBtn.visibility = View.INVISIBLE
        binding.timerCowntTxt.visibility = View.VISIBLE
        binding.timerImg.visibility = View.VISIBLE
        getSmsVerification()
        if (totalTime != null)
            total = totalTime
        job.cancelChildren()
        scope.launch {
            while (true) {
                total -= 1000
                Log.d("timer", "startTimeCounter: $total")
                delay(1000)
            }
        }
        countDownTimer = object : CountDownTimer(total, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                /*val time = millisUntilFinished - 1000
                total = millisUntilFinished*/
                binding.timerCowntTxt.text = String.format(
                    Locale.getDefault(), "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(total),
                    TimeUnit.MILLISECONDS.toSeconds(total) -
                            TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    total
                                )
                            )
                )
            }

            override fun onFinish() {
                job.cancel()
                binding.topupBtn.visibility = View.VISIBLE
                binding.timerCowntTxt.visibility = View.GONE
                binding.timerImg.visibility = View.GONE

            }

        }.start()
    }

    fun onStop() {
        if (::countDownTimer.isInitialized)
            countDownTimer.cancel()
    }
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job.cancel()
        unregisterReceiver()
    }

    fun onResume() {
        if (::countDownTimer.isInitialized)
            startTimeCounter()
    }

    fun selectTopUpButton(clickButton: ClickButton, activity: FragmentActivity) {
        this.clickButton = clickButton
        this.activity = activity
        if (PackageManager.PERMISSION_GRANTED !=
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.RECEIVE_SMS
            )
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.RECEIVE_SMS), 1000
            )
        }
    }

    fun revertBtnAnimation() {
        binding.topupBtn.revertAnimation()
    }


    private fun getSmsVerification() {
        if (ContextCompat.checkSelfPermission(
                context,
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
                        if (messageTxt.isNotEmpty()) {
                            val tokenizer = StringTokenizer(messageTxt, "\r\n")
                            while (tokenizer.hasMoreTokens()) {
                                val nextLine = tokenizer.nextToken()
                                if (nextLine!!.contains("رمز"))
                                    binding.etActivationCode.setText(
                                        nextLine.substringAfter("رمز").filter { it.isDigit() })
                            }
                        }

                    }
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                activity.registerReceiver(
                    updateUIReceiver,
                    filter,
                    Manifest.permission.INTERNET,
                    null,
                    Context.RECEIVER_EXPORTED
                )
            } else
                activity.registerReceiver(
                    updateUIReceiver,
                    filter,
                    Manifest.permission.INTERNET,
                    null
                )
        }
    }

    fun unregisterReceiver() {
        try {
            if (::updateUIReceiver.isInitialized && ::activity.isInitialized)
                activity.unregisterReceiver(updateUIReceiver)
        } catch (ex: Exception) {
            Timber.i(ex)
        }
    }

    fun setPasswordType(passwordType: String) {
        binding.topupBtn.visibility = View.GONE
        myPasswordType = passwordType
        when (passwordType) {
            SecondAuthenticationMethod.STATIC_PASSWORD.value -> {
                binding.txtValueIl.hint = context.getString(R.string.static_pass)
            }

            SecondAuthenticationMethod.OTP.value -> {
                binding.txtValueIl.hint = context.getString(R.string.pass_creator)
                binding.etActivationCode.inputType = InputType.TYPE_CLASS_NUMBER
                binding.etActivationCode.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(6))
            }

            SecondAuthenticationMethod.SMS.value -> {
                binding.txtValueIl.hint = context.getString(R.string.two_factor_sms_pass)
                binding.topupBtn.visibility = View.VISIBLE
                binding.etActivationCode.inputType = InputType.TYPE_CLASS_NUMBER
                binding.etActivationCode.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(6))
            }

            SecondAuthenticationMethod.DYNAMIC.value -> {
                binding.txtValueIl.hint = context.getString(R.string.dynamic_pass)
                binding.topupBtn.visibility = View.VISIBLE
                binding.etActivationCode.inputType = InputType.TYPE_CLASS_NUMBER
                binding.etActivationCode.filters =
                    arrayOf<InputFilter>(InputFilter.LengthFilter(12))
            }

            SecondAuthenticationMethod.HUB_OTP.value -> {
                binding.txtValueIl.hint = context.getString(R.string.two_factor_sms_pass)
                binding.topupBtn.visibility = View.VISIBLE
                binding.etActivationCode.inputType = InputType.TYPE_CLASS_NUMBER
            }
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        // do nothing
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        dismissError()
    }

    override fun afterTextChanged(p0: Editable?) {
        // do nothing
    }

    fun showError() {
        binding.txtValueIl.requestFocus()
        binding.txtValueIl.isErrorEnabled = true
        binding.txtValueIl.error = " "
        binding.txtValueIl.errorIconDrawable = null
        if (binding.txtValueIl.childCount == 2)
            binding.txtValueIl.getChildAt(1).visibility = View.GONE
    }

    private fun dismissError() {
        binding.txtValueIl.isErrorEnabled = false
        binding.txtValueIl.error = null
    }
}