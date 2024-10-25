package com.drp.shared_ui.widget.xml

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Base64
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.drp.shared_ui.AutoCompleteFragment
import com.drp.shared_ui.R
import com.drp.shared_ui.databinding.PanEditTxtBinding
import com.drp.shared_ui.model.AutoCompleteAdapter
import com.drp.shared_ui.model.AutoCompleteItem
import com.drp.utils.hideKeyboard
import com.drp.utils.isProbablyArabic
import com.drp.utils.panFormatter
import com.drp.utils.panFormatterHub
import timber.log.Timber

class PanEditText(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs),
    TextWatcher {
    lateinit var bundle: Bundle

    private val binding =
        PanEditTxtBinding.bind(inflate(context, R.layout.pan_edit_txt, this))
    private var hubRegisterCardCallBackOnFullLength: ((String) -> Unit)? = null
    private var hubCallBackOnNonFullLength: (() -> Unit)? = null
    private var exposeBankInfo: ((String) -> Unit)? = null
    private var onDetectIban: ((String) -> Unit)? = null
    private var decodedBitmap: Bitmap? = null

    init {
        val myAttrs =
            context.theme.obtainStyledAttributes(attrs, R.styleable.bank_view, 0, 0)
        binding.txtPan.addTextChangedListener(this)
        myAttrs.getString(R.styleable.bank_view_android_hint)?.let {
            binding.txtValueIl.hint = it
        }
        binding.txtPan.inputType = myAttrs.getInteger(
            R.styleable.bank_view_android_inputType,
            InputType.TYPE_CLASS_TEXT
        )
    }

    fun setHubRegisterCallbackOnFullLength(callBack: (String) -> Unit) {
        hubRegisterCardCallBackOnFullLength = callBack
    }

    fun setHubCallbackNonFullLength(callBack: () -> Unit) {
        hubCallBackOnNonFullLength = callBack
    }

    fun setExposeBankData(callBack: (String) -> Unit) {
        exposeBankInfo = callBack
    }

    fun callDetectIban(detectIban: (String) -> Unit) {
        onDetectIban = detectIban

    }

    fun setBankImage(imageURL: String?) {
        if (imageURL != null) {
            val decodedString: ByteArray = Base64.decode(
                imageURL.split(",")[1],
                Base64.DEFAULT
            )
            decodedBitmap =
                BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            binding.ivDestCard.visibility = View.VISIBLE
            binding.ivDestCard.setImageBitmap(decodedBitmap)
            exposeBankInfo?.invoke(imageURL)
        } else
            binding.ivDestCard.visibility = View.GONE
    }

    fun text(): Editable? {
        return binding.txtPan.text
    }

    fun setText(value: String) {
        return binding.txtPan.setText(value)
    }

    fun setData(
        autoCompleteItems: List<AutoCompleteItem>,
        activity: AppCompatActivity,
        showEditContactBtn: Boolean = true
    ) {
        if (autoCompleteItems.isNotEmpty()) {
            binding.ivContact.visibility = View.VISIBLE
            val autoCompleteAdapter =
                AutoCompleteAdapter(
                    context,
                    R.layout.item_auto_complete,
                    autoCompleteItems
                )
            binding.txtPan.setAdapter(autoCompleteAdapter)
            binding.txtPan.threshold = 1
            binding.txtPan.setOnItemClickListener { parent, _, position, _ ->
                val item: AutoCompleteItem? = parent.adapter.getItem(position) as AutoCompleteItem?
                binding.txtPan.setText(item?.value)
                binding.txtPan.setSelection(binding.txtPan.text.length)
            }

            binding.ivContact.setOnClickListener {
                context.hideKeyboard(binding.root)
                val fragment = AutoCompleteFragment(showEditContactBtn = showEditContactBtn) {
                    binding.txtPan.setText(it.value)
                }
                // TODO: do it
//                activity.instanceBottomSheet(fragment, AutoCompleteFragment.TAG, Bundle().apply {
//                    this.putSerializable(CONTACTLIST, autoCompleteItems as Serializable)
//                    this.putString(REQUEST, binding.txtPan.text.toString())
//                    this.putString(
//                        TITLE_SHEET,
//                        context.getString(R.string.card_list_title)
//                    )
//                    hubRegisterCardCallBackOnFullLength?.let {
//
//                        this.putBoolean(SHOW_EDIT_MODE, false)
//                    }
//
//                })
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //nothing
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        dismissError()
    }

    override fun afterTextChanged(s: Editable?) {
        binding.txtPan.removeTextChangedListener(this)
        if (!isProbablyArabic(s.toString()))
            if (!s.isNullOrEmpty()) {
                val temp = s.toString()
                if (hubRegisterCardCallBackOnFullLength == null) {
                    s.clear()
                    s.append(panFormatter(temp, " - "))
                } else {
                    s.clear()
                    s.append(panFormatterHub(temp, " - "))
                }
                if (hubRegisterCardCallBackOnFullLength == null) {
                    if (decodedBitmap == null && onDetectIban != null
                    ) {
                        onDetectIban!!.invoke(binding.txtPan.text.toString().replace("-", "").filter { it.isDigit() })
                    }
                    if (binding.txtPan.text.toString().replace("-", "").filter { it.isDigit() }.length <= 6) {
                        decodedBitmap = null
                        binding.ivDestCard.visibility = View.GONE
                    }

                }
            }

        binding.txtPan.setSelection(binding.txtPan.text!!.toString().length)
        binding.txtPan.addTextChangedListener(this)

        if (hubRegisterCardCallBackOnFullLength != null) {
            val maskedPan = binding.txtPan.text.filter { it.isDigit() || it == '*' }
            if (maskedPan.length == 16) {
                Timber.tag("HUB_LOGGER").d("rawPan is $maskedPan")
                hubRegisterCardCallBackOnFullLength?.invoke(maskedPan.toString())
            } else {
                hubCallBackOnNonFullLength?.invoke()
            }
        }
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