package com.drp.shared_ui.widget.xml

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.FragmentActivity
import com.drp.shared_ui.enums.ContactType

import com.drp.shared_ui.AutoCompleteFragment
import com.drp.shared_ui.R
import com.drp.shared_ui.databinding.MobileAutocompTxtBinding
import com.drp.shared_ui.model.AutoCompleteAdapter
import com.drp.shared_ui.model.AutoCompleteItem
import com.drp.utils.CONTACTLIST
import com.drp.utils.REQUEST
import com.drp.utils.hideKeyboard
import com.drp.utils.phoneNumberFromatter
import java.io.Serializable

@SuppressLint("AppCompatCustomView")
@Suppress("DEPRECATION")
class PhoneNumberAutoComplete(context: Context, attrs: AttributeSet?) : ConstraintLayout(
    context,
    attrs
), TextWatcher {
    lateinit var phone: String

    interface SetContactClick {
        fun openContact(contactType: String)
    }

    interface SetOnTextChangeListener {
        fun onTextChange(
            text: String
        )
    }

    private var isFixedTelephone = false
    lateinit var setContactClick: SetContactClick
    lateinit var onTextChangeListener: SetOnTextChangeListener
    lateinit var autoCompleteItems: List<AutoCompleteItem>
    val binding =
        MobileAutocompTxtBinding.bind(inflate(context, R.layout.mobile_autocomp_txt, this))
    var showContactOnly = true

    init {
        binding.txtValue.addTextChangedListener(this)
        val myAttrs = context.theme.obtainStyledAttributes(attrs, R.styleable.bank_view, 0, 0)
//        try {
            binding.txtValueIl.hint = myAttrs.getString(R.styleable.bank_view_android_hint)
            binding.txtValue.inputType =
                myAttrs.getInteger(
                    R.styleable.bank_view_android_inputType,
                    InputType.TYPE_CLASS_PHONE
                )
            binding.txtValue.textDirection =
                myAttrs.getInteger(
                    R.styleable.bank_view_android_textDirection,
                    TEXT_DIRECTION_LTR
                )
            if (binding.txtValue.textDirection == TEXT_DIRECTION_RTL)
                binding.txtValueIl.layoutDirection = LAYOUT_DIRECTION_RTL
            else
                binding.txtValueIl.layoutDirection = LAYOUT_DIRECTION_LTR
    }


    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //nothing
    }

    override fun afterTextChanged(s: Editable?) {
        super.setEnabled(true)
        if (s.toString().contains("Auto"))
            return
        binding.txtValue.removeTextChangedListener(this)
        if (s != null  && !isFixedTelephone) {
            if (s.isDigitsOnly() || s.contains("-")) {
                s.replace(0, s.length, phoneNumberFromatter(s.toString()))
            } else {
                s.replace(0,s.length,s.toString())
            }
        }
        binding.txtValue.addTextChangedListener(this)
        if (::onTextChangeListener.isInitialized)
            onTextChangeListener.onTextChange(s.toString())
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        dismissError()

    }

    fun text(): Editable? {
        return binding.txtValue.text
    }

    fun setText(value: String) {
        binding.txtValue.setText(value)
    }

    fun setData(autoCompleteItems: List<AutoCompleteItem>, setContactClick: SetContactClick) {
        if (autoCompleteItems.isNotEmpty()) {
            binding.ivContact.visibility = VISIBLE
            val autoCompleteAdapter =
                AutoCompleteAdapter(context, R.layout.item_auto_complete, autoCompleteItems)
            binding.txtValue.setAdapter(autoCompleteAdapter)
            binding.txtValue.threshold = 1
            binding.txtValue.setOnItemClickListener { parent, _, position, _ ->
                val item: AutoCompleteItem? = parent.adapter.getItem(position) as AutoCompleteItem?
                binding.txtValue.setText(item?.value)
                binding.txtValue.setSelection(binding.txtValue.text.length)
            }
            this.autoCompleteItems=autoCompleteItems
            showContactOnly=false
        }
        this.setContactClick=setContactClick
        binding.ivContact.visibility= View.VISIBLE
        binding.ivContact.setOnClickListener {
            context.hideKeyboard(binding.root)
            if(showContactOnly){
                setContactClick.openContact(ContactType.PHONE.value)

            }else {
                setContactClick.openContact(ContactType.DIALOG.value)
            }
        }
    }
    fun setHeader(text: String) {
        binding.txtValueIl.hint = text
    }
    fun showContact(activity: FragmentActivity,showEditContactBtn:Boolean?=true) {
        if(::autoCompleteItems.isInitialized) {
            context.hideKeyboard(binding.root)
            val fragment = AutoCompleteFragment(showEditContactBtn = showEditContactBtn!!) {
                binding.txtValue.setText(it.value)
            }
            val bundle = Bundle()
            bundle.putSerializable(CONTACTLIST, autoCompleteItems as Serializable)
            bundle.putString(REQUEST, binding.txtValue.text.toString())
            fragment.arguments = bundle
            fragment.show(activity.supportFragmentManager, "autocomplete_fragment")
        }
    }
    fun setonTextChangeListener(watcher: SetOnTextChangeListener) {
        this.onTextChangeListener = watcher
    }
    fun setPhoneNumber(phone:String,changeTab:(Boolean)->Unit){
        binding.simCard.visibility=View.VISIBLE
        binding.simCard.setOnClickListener {
            changeTab(false)
            binding.txtValue.setText(phone)
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

    fun setIsFixedTelephone(isFixedTelephone: Boolean) {
        this.isFixedTelephone = isFixedTelephone
    }

}