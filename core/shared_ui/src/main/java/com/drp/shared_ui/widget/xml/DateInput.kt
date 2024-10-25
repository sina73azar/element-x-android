package com.drp.shared_ui.widget.xml

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import com.drp.shared_ui.R
import com.drp.shared_ui.databinding.DateInputBinding
import ir.erfandm.persiandatepicker.datepicker.CalendarConstraints
import ir.erfandm.persiandatepicker.datepicker.DateValidatorPointForward
import ir.erfandm.persiandatepicker.datepicker.MaterialDatePicker
import ir.erfandm.persiandatepicker.datepicker.MaterialPickerOnPositiveButtonClickListener
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat
import java.text.SimpleDateFormat
import java.util.Date
//import com.xdev.arch.persiancalendar.datepicker.CalendarConstraints
//import com.xdev.arch.persiancalendar.datepicker.DateValidatorPointForward
//import com.xdev.arch.persiancalendar.datepicker.MaterialDatePicker
//import com.xdev.arch.persiancalendar.datepicker.MaterialPickerOnPositiveButtonClickListener
//import com.xdev.arch.persiancalendar.datepicker.calendar.PersianCalendar
import kotlin.properties.Delegates

class DateInput(context: Context, attrs: AttributeSet?) : ConstraintLayout(
    context,
    attrs
) {
    interface SelectDate {
        fun chooseDate(persianCalendar: PersianDate)
    }

    private lateinit var datePicker: MaterialDatePicker<Long?>
    private lateinit var selectDate: SelectDate
    val binding = DateInputBinding.bind(inflate(context, R.layout.date_input, this))
    private var openAt = PersianDate.today().time
    private var start = PersianDate.today().time
    var selectedTime by Delegates.notNull<Long>()
    lateinit var activity: FragmentActivity

    init {
        val myAttrs = context.theme.obtainStyledAttributes(attrs, R.styleable.bank_view, 0, 0)
        binding.txtValueIl.hint = myAttrs.getString(R.styleable.bank_view_android_hint)
        if (!myAttrs.getString(R.styleable.bank_view_android_hint).isNullOrEmpty()) {
            binding.txtValueIl.hint = myAttrs.getString(R.styleable.bank_view_android_hint)
        }
        binding.txtValue.setOnClickListener{
            val constraints = CalendarConstraints.Builder()
                .setOpenAt(openAt)

            if (myAttrs.getBoolean(R.styleable.bank_view_hasValidator, false)) {
                constraints.setValidator(DateValidatorPointForward.from(start))
            }
            datePicker = MaterialDatePicker.Builder
                .datePicker()
                .setTitleText("تاریخ را انتخاب کنید.")
                .setCalendarConstraints(constraints.build()).build()
            datePicker.addOnPositiveButtonClickListener { selection ->
                val persianDate = PersianDate(selection)
                val persianDateFormat = PersianDateFormat("Y/m/d")
                binding.txtValue.setText(persianDateFormat.format(persianDate))
                selectedTime = selection!!
                openAt = selection
                if (::selectDate.isInitialized)
                    selectDate.chooseDate(persianDate)
            }
            datePicker.show(activity.supportFragmentManager, "DatePickerTag")
        }

    }

    fun dateSelected(selectDate: SelectDate, activity: FragmentActivity) {
        this.selectDate = selectDate
        this.activity = activity
    }

    fun getSelectedDate() = selectedTime

    fun text(): String {
        return binding.txtValue.text.toString()
    }

    fun setText(value: String) {
        binding.txtValue.setText(value)
    }





}