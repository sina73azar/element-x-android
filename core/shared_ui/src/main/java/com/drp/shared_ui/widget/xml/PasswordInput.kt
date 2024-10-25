package com.drp.shared_ui.widget.xml

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.drp.shared_ui.BuildConfig
import com.drp.shared_ui.R
import com.drp.shared_ui.databinding.PasswordInputBinding
import com.google.android.material.textfield.TextInputLayout

class PasswordInput(context: Context, attrs: AttributeSet?) : ConstraintLayout(
    context,
    attrs
) {
    interface TextChanged {
        fun afterTextChanged(s: Editable?)
    }

    private lateinit var textChanged: TextChanged
    val binding = PasswordInputBinding.bind(inflate(context, R.layout.password_input, this))

    init {
        val myAttrs = context.theme.obtainStyledAttributes(attrs, R.styleable.bank_view, 0, 0)
        val maxlenght = myAttrs.getInteger(R.styleable.bank_view_android_maxLength, 18)
        binding.txtValue.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxlenght))
        binding.txtValueIl.hint = myAttrs.getString(R.styleable.bank_view_android_hint)
        binding.txtValue.textDirection =
            myAttrs.getInteger(
                R.styleable.bank_view_android_textDirection,
                TEXT_DIRECTION_RTL
            )
        binding.txtValue.textAlignment = TEXT_ALIGNMENT_VIEW_START
        binding.txtValueIl.layoutDirection = myAttrs.getInteger(
            R.styleable.bank_view_constraint_direction,
            LAYOUT_DIRECTION_RTL
        )
        binding.txtValue.inputType =
            myAttrs.getInteger(
                R.styleable.bank_view_android_inputType,
                InputType.TYPE_TEXT_VARIATION_PASSWORD
            )

    }

    fun text(): Editable? {
        return binding.txtValue.text
    }

    fun setText(value: String) {
        binding.txtValue.setText(value)
    }
    fun setHint(hint: String) {
        binding.txtValueIl.hint = hint
    }
    @SuppressLint("SuspiciousIndentation")
    fun setReadOnlyText(readonly: Boolean) {
        if (readonly) {
            binding.txtValueIl.endIconMode = TextInputLayout.END_ICON_NONE
            binding.txtValue.isLongClickable = false
        }
        if (!BuildConfig.DEBUG)
            binding.txtValue.isEnabled = !readonly
    }

    fun hidePasswordEye() {
        binding.txtValueIl.endIconMode = TextInputLayout.END_ICON_NONE
    }

    fun txtChange(textChanged: TextChanged) {
        this.textChanged = textChanged
        binding.txtValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                dismissError()
            }

            override fun afterTextChanged(s: Editable?) {
                textChanged.afterTextChanged(s)
            }

        })
    }

    fun showError() {
        binding.txtValueIl.requestFocus()
        binding.txtValueIl.isErrorEnabled = true
        binding.txtValueIl.error = " "
        binding.txtValueIl.errorIconDrawable = null
        if (binding.txtValueIl.childCount == 2)
            binding.txtValueIl.getChildAt(1).visibility = View.GONE
    }

    fun dismissError() {
        binding.txtValueIl.isErrorEnabled = false
        binding.txtValueIl.error = null
    }
}