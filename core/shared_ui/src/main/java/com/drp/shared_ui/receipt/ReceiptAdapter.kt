package com.drp.shared_ui.receipt

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.drp.refah.ui.data.enums.TimePeriodType
import com.drp.shared_ui.R
import com.drp.shared_ui.databinding.ItemReceiptBinding
import com.drp.shared_ui.model.receipt.ReceiptItem
import com.drp.shared_ui.model.receipt.ReceiptType
import com.drp.utils.ibanFormatterForEditText

class ReceiptAdapter : RecyclerView.Adapter<ReceiptAdapter.DataBinding>() {
    lateinit var context: Context
    var list: MutableList<ReceiptItem> = arrayListOf()

    class DataBinding(val binding: ItemReceiptBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): DataBinding {
        context = parent.context
        val binding = ItemReceiptBinding.bind(
            LayoutInflater.from(parent.context).inflate(R.layout.item_receipt, parent, false)
        )
        return DataBinding(binding)
    }

    override fun onBindViewHolder(holder: DataBinding, position: Int) {
        with(holder) {
            list[position].value?.let {
                if (it.contains("\\d".toRegex()) && it.contains("[آ-ی]".toRegex()))
                    binding.itemReceiptTxt.textDirection = View.TEXT_DIRECTION_ANY_RTL
                else
                    binding.itemReceiptTxt.textDirection = View.TEXT_DIRECTION_LTR
                binding.itemReceiptTxt.text = checkValue(it)
            }
            binding.itemReceiptTxt.text = list[position].value?.let { checkValue(it) }
            binding.titleReceiptTxt.text = list[position].title
            if (list[position].resIcon != null) {
                binding.bankImg.visibility = View.VISIBLE
                val decodedString: ByteArray = Base64.decode(list[position].resIcon, Base64.DEFAULT)
                val decodedBitmap =
                    BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                binding.bankImg.setImageBitmap(decodedBitmap)
            }
            if (position == list.size - 1)
                binding.line.visibility = View.GONE
            else
                binding.line.visibility = View.VISIBLE
            if (list[position].type == ReceiptType.AMOUNT) {
                binding.txtIrcurrency.visibility = View.VISIBLE
                if (::context.isInitialized)
                    binding.txtIrcurrency.background.setTint(
                        ContextCompat.getColor(context, R.color.light_gray)
                    )
            } else
                binding.txtIrcurrency.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<ReceiptItem>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    private fun checkValue(value: String): String {
        return when (value) {
            TimePeriodType.DAILY.name -> context.getString(R.string.daily)
            TimePeriodType.WEEKLY.name -> context.getString(R.string.weekly)
            TimePeriodType.BI_WEEKLY.name -> context.getString(R.string.bi_weekly)
            TimePeriodType.TWICE_MONTHLY.name -> context.getString(R.string.twice_monthly)
            TimePeriodType.MONTHLY.name -> context.getString(R.string.monthly)
            TimePeriodType.END_OF_MONTH.name -> context.getString(R.string.end_of_month)
            TimePeriodType.FOUR_WEEKS.name -> context.getString(R.string.four_weeks)
            TimePeriodType.BI_MONTHLY.name -> context.getString(R.string.bi_monthly)
            TimePeriodType.QUARTERLY.name -> context.getString(R.string.quarterly)
            TimePeriodType.SEMI_ANNUALY.name -> context.getString(R.string.semi_annually)
            TimePeriodType.ANNUALLY.name -> context.getString(R.string.annually)
            else -> {
                if (value.contains(context.getString(R.string.ir))) {
                    context.getString(R.string.ir) + ibanFormatterForEditText(value.filter { it.isDigit() })
                } else {
                    value
                }

            }
        }
    }


}