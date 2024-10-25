package com.drp.shared_ui.widget.xml

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.drp.shared_ui.R
import com.drp.shared_ui.databinding.MobileEditTxtBinding
import com.drp.utils.phoneNumberFromatter


@SuppressLint("SetTextI18n")
class PhoneNumberEditText(context: Context, attrs: AttributeSet?) : ConstraintLayout(
    context,
    attrs
), TextWatcher {

    interface TextChangeListener {
        fun txtChange()
    }

    val binding = MobileEditTxtBinding.bind(inflate(context, R.layout.mobile_edit_txt, this))
    private lateinit var textChangeListener: TextChangeListener

    init {
        binding.txtValue.setText("09")
        binding.txtValue.addTextChangedListener(this)
        val myAttrs = context.theme.obtainStyledAttributes(attrs, R.styleable.bank_view, 0, 0)
        binding.txtValueIl.hint = myAttrs.getString(R.styleable.bank_view_android_hint)
        if (Build.VERSION.SDK_INT < 23) {
            binding.txtValue.setTextAppearance(context, R.style.normal_edit_text)
        } else {
            binding.txtValue.setTextAppearance(R.style.normal_edit_text)
        }
    }

    fun text(): Editable? {
        return binding.txtValue.text
    }

    fun setText(value: String) {
        binding.txtValue.setText(value)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //nothing
    }

    override fun afterTextChanged(s: Editable?) {
        if (binding.txtValue.text?.length!! > 2) {
            super.setEnabled(true)
            binding.txtValue.removeTextChangedListener(this)
            s?.replace(0, s.length, phoneNumberFromatter(s.toString()))
            binding.txtValue.setSelection(binding.txtValue.text!!.toString().length)
            binding.txtValue.addTextChangedListener(this)
        } else {
            binding.txtValue.removeTextChangedListener(this)
            binding.txtValue.setText("09")
            binding.txtValue.setSelection(binding.txtValue.text!!.toString().length)
            binding.txtValue.addTextChangedListener(this)
        }
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        dismissError()
        if (::textChangeListener.isInitialized)
            textChangeListener.txtChange()
    }

    fun setOnTextChangeListener(watcher: TextChangeListener) {
        this.textChangeListener = watcher
    }

    fun showError() {
        binding.txtValueIl.requestFocus()
        binding.txtValueIl.isErrorEnabled = true
        binding.txtValueIl.error = " "
        if (binding.txtValueIl.childCount == 2)
            binding.txtValueIl.getChildAt(1).visibility = View.GONE
    }

    fun dismissError() {
        binding.txtValueIl.isErrorEnabled = false
        binding.txtValueIl.error = null
    }

}