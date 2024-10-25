package com.drp.shared_ui.widget.xml

import android.annotation.SuppressLint
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.FragmentActivity
import com.drp.shared_ui.AutoCompleteFragment
import com.drp.shared_ui.R
import com.drp.shared_ui.databinding.IbanEditTextBinding
import com.drp.shared_ui.model.AutoCompleteAdapter
import com.drp.shared_ui.model.AutoCompleteItem
import com.drp.utils.CONTACTLIST
import com.drp.utils.REQUEST
import com.drp.utils.hideKeyboard
import com.drp.utils.ibanFormatterForEditText

import java.io.Serializable

@SuppressLint("AppCompatCustomView")
class IbanEditText(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs),
    TextWatcher {
    val binding = IbanEditTextBinding.bind(inflate(context, R.layout.iban_edit_text, this))
    private var onDetectIban: ((String) -> Unit)? = null
    private var decodedBitmap: Bitmap? = null
    fun setBankImage(imageURL: String?) {
        if (imageURL != null) {
            val decodedString: ByteArray = Base64.decode(
                imageURL.split(",")[1],
                Base64.DEFAULT
            )
            decodedBitmap =
                BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            binding.ivDestIban.visibility = View.VISIBLE
            binding.ivDestIban.setImageBitmap(decodedBitmap)
        } else
            binding.ivDestIban.visibility = View.GONE
    }

    fun callDetectIban(detectIban: (String) -> Unit) {
        onDetectIban = detectIban

    }
//    fun setIbanViewModel(viewModel: BaseViewModel, lifecycleOwner: LifecycleOwner) {
//        this.viewModel = viewModel
//        viewModel.detectIban.observe(lifecycleOwner) {
//            when (it.status) {
//                CustomResponse.Status.SUCCESS -> {
//                    if (it.data?.iban?.bank?.imageURL != null) {
//                        val decodedString: ByteArray = Base64.decode(
//                            it.data!!.iban.bank.imageURL.split(",")[1],
//                            Base64.DEFAULT
//                        )
//                        val decodedBitmap =
//                            BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
//                        binding.ivDestIban.visibility = View.VISIBLE
//                        binding.ivDestIban.setImageBitmap(decodedBitmap)
//                    } else
//                        binding.ivDestIban.visibility = View.GONE
//
//                }
//
//                else -> {
//                    binding.ivDestIban.visibility = View.GONE
//                }
//            }
//        }
//    }

    init {
        binding.txtValue.addTextChangedListener(this)
        val attr = context.theme.obtainStyledAttributes(attrs, R.styleable.bank_view, 0, 0)
        binding.txtValue.inputType = attr.getInteger(
            R.styleable.bank_view_android_inputType,
            InputType.TYPE_CLASS_TEXT
        )

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //nothing
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        dismissError()
    }

    override fun afterTextChanged(s: Editable?) {
        if (s.toString().contains("Auto"))
            return
        binding.txtValue.removeTextChangedListener(this)
//        if (!isProbablyArabic(s.toString()))
        if (!s.isNullOrEmpty()) {
            val temp = s.toString()
            if (s.isDigitsOnly() || s.contains("-")) {
                s.clear()
                s.append(ibanFormatterForEditText(temp))
            } else {
                s.clear()
                s.append(temp)
            }
        }
        binding.txtValue.setSelection(binding.txtValue.text.toString().length)
        binding.txtValue.addTextChangedListener(this)
        if (decodedBitmap == null && onDetectIban != null) {
            onDetectIban!!.invoke("IR" + binding.txtValue.text.toString().replace("-", ""))
        }
        if (binding.txtValue.text.toString().replace("-", "").length <= 4) {
            decodedBitmap = null
            binding.ivDestIban.visibility = View.GONE
        }
    }


    fun text(): Editable? {
        return binding.txtValue.text
    }

    fun setText(value: String) {
        binding.txtValue.setText(value)
    }

    fun setData(autoCompleteItems: List<AutoCompleteItem>, activity: FragmentActivity) {
        if (autoCompleteItems.isNotEmpty()) {
            binding.ivContact.visibility = View.VISIBLE
            val autoCompleteAdapter =
                AutoCompleteAdapter(context, R.layout.item_auto_complete, autoCompleteItems)
            binding.txtValue.setAdapter(autoCompleteAdapter)
            binding.txtValue.threshold = 1
            binding.txtValue.setOnItemClickListener { parent, _, position, _ ->
                val item: AutoCompleteItem? = parent.adapter.getItem(position) as AutoCompleteItem?
                binding.txtValue.setText(item?.value?.filter { it.isDigit() })
            }
            binding.ivContact.setOnClickListener {
                context.hideKeyboard(binding.root)
                val fragment = AutoCompleteFragment {
                    binding.txtValue.setText(it.value.filter { it.isDigit() })
                }
                val bundle = Bundle()
                bundle.putSerializable(CONTACTLIST, autoCompleteItems as Serializable)
                bundle.putString(REQUEST, binding.txtValue.text.toString())
                fragment.arguments = bundle
                fragment.show(activity.supportFragmentManager, "autocomplete_fragment")
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