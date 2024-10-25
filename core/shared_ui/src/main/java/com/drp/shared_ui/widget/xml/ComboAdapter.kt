package com.drp.shared_ui.widget.xml

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drp.refah.ui.data.enums.AdapterViewType
import com.drp.shared_ui.R
import com.drp.shared_ui.databinding.BottomSearchableItemBinding
import com.drp.shared_ui.databinding.ItemEmptyListBinding
import com.drp.shared_ui.model.ComboModel


class ComboAdapter constructor(val onItemClick: (ComboModel) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var context: Context
    var list: List<ComboModel> = ArrayList()
    var direction: String = "LTR"

    class ViewHolderNormal(val binding: BottomSearchableItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ViewHolderEmpty(val emptyBinding: ItemEmptyListBinding) :
        RecyclerView.ViewHolder(emptyBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (AdapterViewType.valueOf(viewType)) {
            AdapterViewType.VIEW_TYPE_NORMAL -> {
                val bottomSearchableBinding = BottomSearchableItemBinding.bind(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.bottom_searchable_item, parent, false)
                )
                return ViewHolderNormal(bottomSearchableBinding)
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
                with(holder) {
                    this.binding.apply {
                        tvTitle.text = list[p1].title
                        root.setOnClickListener { onItemClick.invoke(list[p1]) }
                        if (direction == "LTR")
                            tvTitle.gravity = Gravity.RIGHT
                        else
                            tvTitle.gravity = Gravity.LEFT
                    }
                }
            }

            is ViewHolderEmpty -> {
                with(holder) {
                    emptyBinding.noResultTxt.text =
                        context.getString(R.string.no_item_destination_account)
                }

            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<ComboModel>, direction: String) {
        this.list = list
        notifyDataSetChanged()
        this.direction = direction
    }

    override fun getItemCount(): Int {
        return if (!list.isNullOrEmpty()) list.size else 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (list.isNullOrEmpty()) {
            true -> AdapterViewType.VIEW_TYPE_EMPTY.value
            false -> AdapterViewType.VIEW_TYPE_NORMAL.value
        }
    }
}