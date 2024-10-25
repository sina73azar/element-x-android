package com.drp.shared_ui.widget.xml

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.drp.shared_ui.R
import com.drp.shared_ui.databinding.PriceEditTextBinding
import com.drp.utils.currencyFormatter
import com.drp.utils.extractDigits
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltipUtils

import ir.yamin.digits.Digits


@SuppressLint("ClickableViewAccessibility")
class AmountEditText(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs),
    TextWatcher {
    var binding: PriceEditTextBinding =
        PriceEditTextBinding.bind(inflate(context, R.layout.price_edit_text, this))
    val amount: Long
        get() = if (binding.txtValue.text != null && binding.txtValue.length() > 0) {
            java.lang.Long.parseLong(binding.txtValue.text.toString().filter { it.isDigit() })
        } else {
            0
        }

    interface OnAmountTextChanged {
        fun onChanged()
    }

    private lateinit var onAmountTextChanged: OnAmountTextChanged

    fun setTextChangedListener(listener: OnAmountTextChanged) {
        onAmountTextChanged = listener
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        //do nothing
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        dismissError()
        if (::onAmountTextChanged.isInitialized)
            onAmountTextChanged.onChanged()
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun afterTextChanged(s: Editable) {
        binding.txtValue.removeTextChangedListener(this)
        val value = extractDigits(s.toString())
        val amount: Long?
        if (value.isEmpty()) {
            amount = null
            binding.tvNumeric.text = ""

        } else {
            amount = value.toLong()
            var price = ""
            if (amount / 10 > 0)
                price = Digits().spellToFarsi(amount / 10).plus(" ")
                    .plus(context.getString(R.string.toman_currency)).plus(" ")
            if (amount % 10 > 0) {
                if (price.isNotEmpty())
                    price += "و".plus(" ")
                price += Digits().spellToFarsi((amount % 10)).plus(" ")
                    .plus(context.getString(R.string.irr_currency))
            }
            binding.tvNumeric.text = price
        }
        binding.txtValue.setText(currencyFormatter(amount))
        binding.txtValue.setSelection(binding.txtValue.text.toString().length)
        binding.txtValue.addTextChangedListener(this)
    }

    init {
        binding.txtValue.addTextChangedListener(this)
        val myAttrs = context.theme.obtainStyledAttributes(attrs, R.styleable.bank_view, 0, 0)
        try {
            val maxlenght = myAttrs.getInteger(R.styleable.bank_view_android_maxLength, 18)
            binding.txtValue.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxlenght))
            val hint = myAttrs.getString(R.styleable.bank_view_android_hint)
            if (!hint.isNullOrEmpty()) {
                binding.txtValueIl.hint = hint
            }
        } catch (ex: Exception) {
            myAttrs.recycle()
        }

    }

    fun text(): Editable? {
        return binding.txtValue.text
    }

    fun setText(value: String) {
        binding.txtValue.setText(value)
    }

    fun showHint(text: String) {
        binding.ivInfoPassword.visibility = View.VISIBLE
        binding.ivInfoPassword.setOnClickListener {
            SimpleTooltip.Builder(context)
                .anchorView(binding.ivInfoPassword)
                .text(text)
                .gravity(Gravity.TOP)
                .textColor(Color.WHITE)
                .animated(true)
                .transparentOverlay(false)
                .arrowHeight(SimpleTooltipUtils.pxFromDp(20F))
                .arrowWidth(SimpleTooltipUtils.pxFromDp(30F))
                .build()
                .show()

        }
    }

    fun showError() {
        binding.txtValueIl.requestFocus()
        binding.txtValueIl.isErrorEnabled = true
        binding.txtValueIl.error = " "
        if (binding.txtValueIl.childCount == 2)
            binding.txtValueIl.getChildAt(1).visibility = View.GONE
    }

    private fun dismissError() {
        binding.txtValueIl.isErrorEnabled = false
        binding.txtValueIl.error = null
    }
}