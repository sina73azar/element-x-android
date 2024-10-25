package com.drp.shared_ui.widget.xml

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.drp.shared_ui.R
import com.drp.shared_ui.databinding.MySpinnerLayoutBinding
import com.drp.shared_ui.model.ComboModel
import com.drp.shared_ui.rotateArrow
import com.drp.utils.ITEMS
import com.drp.utils.TITLE
import com.drp.utils.TYPE


class MySpinner(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    lateinit var bundle: Bundle
    val binding = MySpinnerLayoutBinding.bind(inflate(context, R.layout.my_spinner_layout, this))
    val list = mutableListOf<ComboModel>()
    lateinit var activity: FragmentActivity
    lateinit var myOnItemSelected: (ComboModel) -> Unit
    var showDefault: Boolean = true

    init {
        val myAttrs = context.theme.obtainStyledAttributes(attrs, R.styleable.bank_view, 0, 0)
        binding.txtValueIl.hint = myAttrs.getString(R.styleable.bank_view_android_hint)

        val textDirection =
            myAttrs.getInteger(R.styleable.bank_view_android_textDirection, TEXT_DIRECTION_RTL)
        binding.spinner.textDirection = textDirection
        if (textDirection == 3) {
            binding.imgDownArrow.layoutDirection = View.LAYOUT_DIRECTION_LTR
            binding.spinkitView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        }

    }

    fun getSelectedItem(): String {
        return binding.spinner.text.toString()
    }

    fun setSelection(selection: Int) {
        binding.spinner.setText(list[selection].title)

    }

    fun getSelectedItemPosition(): Int {
        if (list.isNotEmpty())
            list.forEachIndexed { index, comboModel ->
                if (comboModel.title == binding.spinner.text.toString())
                    return index
            }
        return -1
    }

    fun setData(
        items: List<String>,
        direction: String? = "LTR",
        activity: FragmentActivity,
        showDefault: Boolean? = true
    ) {
        this.showDefault=showDefault!!
        list.clear()
        items.onEachIndexed { index, item ->
            list.add(ComboModel(index, item))
        }
        if (list.isNotEmpty()&&showDefault)
            binding.spinner.setText(list[0].title)
        this.activity = activity
        binding.spinner.setOnClickListener {
            val bundle = Bundle().apply {
                this.putParcelableArrayList(ITEMS, list as ArrayList<out Parcelable>)
                this.putString(
                    TITLE,
                    (binding.txtValueIl.hint
                        ?: context.getString(R.string.information_list)).toString()
                )
                this.putString(TYPE, direction)
            }
            if (::activity.isInitialized) {
                val myFragment=activity.supportFragmentManager.findFragmentByTag(
                    BottomSheetSearchable.TAG)
                if(myFragment!=null&&myFragment.isAdded){
                    activity.supportFragmentManager.beginTransaction().show(myFragment).commitAllowingStateLoss()
                    return@setOnClickListener
                }else{
                    val fragment= BottomSheetSearchable {
                        binding.spinner.setText(it.title)
                        if (::myOnItemSelected.isInitialized)
                            myOnItemSelected(it)
                    }
                    fragment.arguments=bundle
                    fragment.show(activity.supportFragmentManager, BottomSheetSearchable.TAG)
                }
            }
        }

    }

    fun showSpinkitView() {
        binding.spinkitView.visibility = View.VISIBLE
    }

    fun hideSpinkitView() {
        binding.spinkitView.visibility = View.GONE
    }

    fun setOnItemClickListener(onItemSelected: (ComboModel) -> Unit) {
        myOnItemSelected = onItemSelected
        if (list.isNotEmpty()&&this.showDefault)
            myOnItemSelected(list[0])
    }

    fun refreshData(refreshData: () -> Unit) {
        binding.ivRefresh.visibility = View.VISIBLE
        binding.ivRefresh.setOnClickListener {
            rotateArrow(binding.ivRefresh, 0f, 360f, 1000)
            refreshData()
            showSpinkitView()
        }
    }

    fun hideRefresh() {
        if (binding.ivRefresh.isVisible) {
            binding.ivRefresh.clearAnimation()
            binding.ivRefresh.visibility = View.GONE
        }
        hideSpinkitView()
    }

    fun showError() {
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