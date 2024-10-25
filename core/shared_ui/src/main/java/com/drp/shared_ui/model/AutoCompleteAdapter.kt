package com.drp.shared_ui.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.drp.shared_ui.R
import java.util.Locale

@Suppress("UNCHECKED_CAST")
class AutoCompleteAdapter(context: Context, @LayoutRes private val layoutResource: Int, private val items: List<AutoCompleteItem>):
    ArrayAdapter<AutoCompleteItem>(context, layoutResource, items), Filterable {

    private var autoCompleteItems: List<AutoCompleteItem> = items

    override fun getCount(): Int {
        return autoCompleteItems.size
    }

    override fun getItem(position: Int): AutoCompleteItem {
        return autoCompleteItems[position]
    }

    override fun getItemId(position: Int): Long {
        return autoCompleteItems[position].id
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view: View
        val viewHolder: ViewHolder
        if (convertView == null){
            val inflater = parent.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(layoutResource, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }else{
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        val autoCompleteItem = autoCompleteItems[position]
        viewHolder.tvTitle?.text = autoCompleteItem.title
        viewHolder.tvValue?.text = autoCompleteItem.value

        return view
    }

    private class ViewHolder(row: View?){

        var tvTitle: TextView? = null
        var tvValue: TextView? = null

        init {
            this.tvTitle = row?.findViewById(R.id.tvTitle)
            this.tvValue = row?.findViewById(R.id.tvValue)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(
                charSequence: CharSequence?,
                filterResults: FilterResults
            ) {
                autoCompleteItems = filterResults.values as List<AutoCompleteItem>
                notifyDataSetChanged()
            }

            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val queryString = charSequence?.toString()?.lowercase(Locale.getDefault())

                val filterResults = FilterResults()
                filterResults.values = if (queryString == null || queryString.isEmpty())
                    items
                else
                    items.filter {
                        it.title.lowercase(Locale.getDefault()).contains(queryString) ||
                                it.value.lowercase(Locale.getDefault()).contains(queryString)
                    }

                return filterResults
            }

        }
    }
}