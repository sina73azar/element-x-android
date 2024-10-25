package com.drp.shared_ui

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.drp.shared_ui.databinding.FragmentAutoCompleteBinding
import com.drp.shared_ui.model.AutoCompleteItem
import com.drp.shared_ui.widget.xml.AutoCompleteAdapter
import com.drp.utils.CONTACTLIST
import com.drp.utils.REQUEST
import com.drp.utils.serializable


class AutoCompleteFragment(
    private val showEditContactBtn: Boolean = true,
    val item: (AutoCompleteItem) -> Unit,
) :
    BaseBottomSheet<FragmentAutoCompleteBinding>(FragmentAutoCompleteBinding::inflate) {
    lateinit var itemList: List<AutoCompleteItem>
    lateinit var adapter: AutoCompleteAdapter
    lateinit var selectedContact: String

    @Suppress("UNCHECKED_CAST")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editContact.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        try {
            arguments?.serializable<ArrayList<AutoCompleteItem>>(CONTACTLIST)?.let {
                itemList = it
            }
            arguments?.getString(REQUEST)?.let {
                selectedContact = it
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        setRecycler()
        if (::itemList.isInitialized)
            itemList.forEach { it.selected = false }
        if (::selectedContact.isInitialized && ::itemList.isInitialized) {
            itemList.find { item ->
                item.value.filter { it.isDigit() } == selectedContact.filter { it.isDigit() }.trim()
            }
                .let {
                    it?.selected = true
                }
            adapter.updateList(itemList)
        }
        binding.svItems.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (::itemList.isInitialized) {
                    adapter.updateList(itemList.filter { item ->
                        item.title.contains(newText.toString())
                                || item.value.contains(newText.toString())
                    })
                    return true
                }
                return false
            }

        })
        binding.btnClosePopUp.setOnClickListener { dismiss() }
        if (showEditContactBtn) {
            binding.editContact.visibility = View.VISIBLE
            binding.editContact.setOnClickListener {
                // TODO: we should do it in near future
//                requireActivity().navigationToDestinationActivity(NavigationActivity.ContactActivity.path)
            }
        }

    }


    private fun setRecycler() {
        val layoutManager = LinearLayoutManager(context)
        adapter = AutoCompleteAdapter { selectedItem ->
            dismiss()
            item(selectedItem)
        }
        binding.accountRv.adapter = adapter
        binding.accountRv.layoutManager = layoutManager
    }

    companion object {
        const val TAG = "AutoCompleteFragment"
    }
}