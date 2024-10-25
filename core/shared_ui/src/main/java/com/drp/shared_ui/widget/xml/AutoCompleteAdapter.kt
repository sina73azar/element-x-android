package com.drp.shared_ui.widget.xml

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drp.refah.ui.data.enums.AdapterViewType
import com.drp.shared_ui.R
import com.drp.shared_ui.databinding.AutoCompleteItemBinding
import com.drp.shared_ui.databinding.ItemEmptyListBinding
import com.drp.shared_ui.model.AutoCompleteItem


class AutoCompleteAdapter constructor(val onItemClick: (AutoCompleteItem) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var context: Context
    var sources: List<AutoCompleteItem> = ArrayList()
    class ViewHolderNormal(val binding: AutoCompleteItemBinding) :
        RecyclerView.ViewHolder(binding.root)
    class ViewHolderEmpty(val emptyBinding: ItemEmptyListBinding) :
        RecyclerView.ViewHolder(emptyBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (AdapterViewType.valueOf(viewType)) {
            AdapterViewType.VIEW_TYPE_NORMAL -> {
                val binding = AutoCompleteItemBinding.bind(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.auto_complete_item,
                        parent,
                        false
                    )
                )
                ViewHolderNormal(binding)
            }
            else -> {
                val emptyBinding = ItemEmptyListBinding.bind(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_empty_list, parent, false)
                )
                ViewHolderEmpty(emptyBinding)
            }

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, p1: Int) {
        when (holder) {
            is ViewHolderNormal -> {
                with(holder){
                    binding.root.setOnClickListener {
                        onItemClick(sources[p1])
                    }
                    binding.btnRadio.setOnClickListener {
                        onItemClick(sources[p1])
                    }
                    if (sources[p1].selected) {
                        binding.btnRadio.isChecked = true
                        binding.viewStroke.visibility = View.VISIBLE
                    } else {
                        binding.viewStroke.visibility = View.GONE
                    }
                    binding.txtTitle.text = sources[p1].title
                    binding.txtValue.text =sources[p1].value
                    if(sources[p1].bank!=null){
                        if (sources[p1].bank?.imageURL != null) {
                            val decodedString: ByteArray = Base64.decode(
                                sources[p1].bank?.imageURL?.split(",")?.get(1),
                                Base64.DEFAULT
                            )
                            val decodedBitmap =
                                BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                            binding.bankImg.visibility = View.VISIBLE
                            binding.bankImg.setImageBitmap(decodedBitmap)
                        } else
                            binding.bankImg.visibility = View.GONE
                    }
                }
            }
            is ViewHolderEmpty -> {
                with(holder){
                    emptyBinding.noResultTxt.text =
                        context.getString(R.string.no_item_contact)
                }

            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(accounts: List<AutoCompleteItem>) {
        this.sources = accounts
        notifyDataSetChanged()

    }

    override fun getItemCount(): Int {
        return if (!sources.isNullOrEmpty()) sources.size else 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (sources.isNullOrEmpty()) {
            true -> AdapterViewType.VIEW_TYPE_EMPTY.value
            false -> AdapterViewType.VIEW_TYPE_NORMAL.value
        }
    }
}