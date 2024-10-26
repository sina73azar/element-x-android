package com.drp.card_facilities.presentation.phone_contact

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drp.refah.ui.data.enums.AdapterViewType
import com.drp.refah.ui.data.model.ContactItem
import io.element.android.x.R
import io.element.android.x.databinding.ItemEmptyListBinding
import io.element.android.x.databinding.ItemMobileContactBinding

class PhoneContactAdapter(val adapterOnClick: (ContactItem) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private var contacts: List<ContactItem> = ArrayList()

    class ViewHolderNormal(val binding: ItemMobileContactBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ViewHolderEmpty(val emptyBinding: ItemEmptyListBinding) :
        RecyclerView.ViewHolder(emptyBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (AdapterViewType.valueOf(viewType)) {
            AdapterViewType.VIEW_TYPE_NORMAL -> {
                val binding = ItemMobileContactBinding.bind(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_mobile_contact, parent, false)
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderNormal -> {
                with(holder) {
                    binding.tvMobileNo.text = contacts[position].mobileNo
                    binding.tvName.text = contacts[position].name
                    if (contacts[position].avatar != null)
                        binding.ivImage.setImageURI(Uri.parse(contacts[position].avatar))
                    binding.root.setOnClickListener {
                        adapterOnClick(contacts[position])
                    }
                }
            }
            is ViewHolderEmpty -> {
                with(holder) {
                    emptyBinding.noResultTxt.text = context.getString(R.string.no_item_contact)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (contacts.isNotEmpty()) contacts.size else 1
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(contacts: List<ContactItem>) {
        this.contacts = contacts
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (contacts.isEmpty()) {
            true -> AdapterViewType.VIEW_TYPE_EMPTY.value
            false -> AdapterViewType.VIEW_TYPE_NORMAL.value
        }
    }
}
