package com.drp.shared_ui.widget.xml

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import com.drp.shared_ui.R
import com.drp.shared_ui.databinding.BankEditTextBinding
import com.drp.shared_ui.model.AutoCompleteAdapter
import com.drp.shared_ui.model.AutoCompleteItem
import com.drp.utils.convertP2EDigits
import com.drp.utils.hideKeyboard


@SuppressLint("ClickableViewAccessibility")
@Suppress("DEPRECATION")
class BankEditText(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs),
    TextWatcher {
    interface SetOnTextChangeListener {
        fun onTextChange(
            text: String
        )
    }

    lateinit var onTextChangeListener: SetOnTextChangeListener
    private var myAttrs: TypedArray
    var binding: BankEditTextBinding =
        BankEditTextBinding.bind(inflate(context, R.layout.bank_edit_text, this))

    init {
        binding.txtValue.addTextChangedListener(this)
        myAttrs = context.theme.obtainStyledAttributes(attrs, R.styleable.bank_view, 0, 0)
        try {
            binding.txtValueIl.hint = myAttrs.getString(R.styleable.bank_view_android_hint)
            binding.txtValue.textDirection =
                myAttrs.getInteger(
                    R.styleable.bank_view_android_textDirection,
                    TEXT_DIRECTION_LTR
                )
            if (binding.txtValue.textDirection == TEXT_DIRECTION_RTL)
                binding.txtValueIl.layoutDirection = LAYOUT_DIRECTION_RTL
            else
                binding.txtValueIl.layoutDirection = LAYOUT_DIRECTION_LTR
            binding.txtValue.inputType =
                myAttrs.getInteger(
                    R.styleable.bank_view_android_inputType,
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                )
            val maxlenght = myAttrs.getInteger(R.styleable.bank_view_android_maxLength, 100)
            binding.txtValue.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxlenght))
            if (!myAttrs.getString(R.styleable.bank_view_android_hint).isNullOrEmpty()) {
                binding.txtValueIl.hint = myAttrs.getString(R.styleable.bank_view_android_hint)
            }
            if (Build.VERSION.SDK_INT < 23) {
                binding.txtValue.setTextAppearance(context, R.style.normal_edit_text)
            } else {
                binding.txtValue.setTextAppearance(R.style.normal_edit_text)
            }

        } finally {
            myAttrs.recycle()
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //nothing
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (::onTextChangeListener.isInitialized)
            onTextChangeListener.onTextChange(s.toString())
        dismissError()
    }

    override fun afterTextChanged(s: Editable?) {
        binding.txtValue.removeTextChangedListener(this)
        s?.replace(0, s.length, convertP2EDigits(s.toString()))
        if (::onTextChangeListener.isInitialized)
            onTextChangeListener.onTextChange(s.toString())
        binding.txtValue.addTextChangedListener(this)
    }

    fun getText() = binding.txtValue
    fun text(): Editable? {
        return binding.txtValue.text
    }

    fun setText(value: String) {
        binding.txtValue.setText(value)
    }

    fun setData(autoCompleteItems: List<AutoCompleteItem>, activity: FragmentActivity, showEditContactBtn:Boolean?=true) {
        if (autoCompleteItems.isNotEmpty()) {
            binding.ivContact.visibility = View.VISIBLE
            val autoCompleteAdapter =
                AutoCompleteAdapter(context, R.layout.item_auto_complete, autoCompleteItems)
            binding.txtValue.setAdapter(autoCompleteAdapter)
            binding.txtValue.threshold = 1
            binding.txtValue.setOnItemClickListener { parent, _, position, _ ->
                val item: AutoCompleteItem? = parent.adapter.getItem(position) as AutoCompleteItem?
                binding.txtValue.setText(item?.value)
                binding.txtValue.setSelection(binding.txtValue.text.length)
            }
            binding.ivContact.setOnClickListener {
                activity.hideKeyboard()
                // TODO: we should fix it in future 
//                val fragment = AutoCompleteFragment (showEditContactBtn = showEditContactBtn!!){
//                    binding.txtValue.setText(it.value)
//                }
//                val bundle = Bundle()
//                bundle.putSerializable(CONTACTLIST, autoCompleteItems as Serializable)
//                bundle.putString(REQUEST, binding.txtValue.text.toString())
//                fragment.arguments = bundle
//                fragment.show(activity.supportFragmentManager, "autocomplete_fragment")
            }
        }
    }

    fun setMaxLength(maxlength: Int) {
        binding.txtValue.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxlength))

    }

    fun setonTextChangeListener(watcher: SetOnTextChangeListener) {
        this.onTextChangeListener = watcher
    }

    fun setTextReadOnly(readOnly: Boolean) {
        if (readOnly) {
            binding.root.alpha = 0.6f
        } else {
            binding.root.alpha = 1.0f
        }
        binding.txtValue.isEnabled = !readOnly
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