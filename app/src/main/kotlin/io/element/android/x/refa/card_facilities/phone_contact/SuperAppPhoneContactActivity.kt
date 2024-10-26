package com.drp.card_facilities.presentation.phone_contact

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import com.drp.refah.card_facilities.utility.Commons.customSearchView
import com.drp.refah.card_facilities.utility.Commons.mobileNoFormatter
import com.drp.refah.ui.data.model.ContactItem
import com.drp.shared_ui.BaseActivity
import com.drp.utils.RESULT
import io.element.android.x.R
import io.element.android.x.databinding.ActivityPhoneContactSuperAppBinding
import io.element.android.x.refa.Constants.CONTACT
import timber.log.Timber
import java.util.Locale

class SuperAppPhoneContactActivity :
    BaseActivity<ActivityPhoneContactSuperAppBinding>(ActivityPhoneContactSuperAppBinding::inflate),
    LoaderManager.LoaderCallbacks<Cursor> {
    var contacts: List<ContactItem> = arrayListOf()
    lateinit var adapter: PhoneContactAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.toolbar.ivBack.setOnClickListener { finish() }
        binding.toolbar.tvToolbarTitle.text = getString(R.string.contact_title)
        setUpRecycler()
        customSearchView(binding.svContact, this)
        binding.svContact.visibility = View.GONE
        if (PackageManager.PERMISSION_GRANTED !=
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            )
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS), CONTACT
            )
        } else {
            LoaderManager.getInstance(this).restartLoader(0, null, this)
        }
        binding.svContact.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    onSearch(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    onSearch(newText)
                }
                return false
            }
        })
    }

    fun setUpRecycler() {
        adapter = PhoneContactAdapter {
            onSelectItem(it)
        }
        val linearManager = LinearLayoutManager(this)
        binding.rvContact.layoutManager = linearManager
        binding.rvContact.adapter = adapter
    }

    companion object {
        private val PROJECTION = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        )
        private const val ORDER = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " ASC"
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(
            applicationContext,
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            PROJECTION,
            null,
            null,
            ORDER
        )
    }

    //AMB-77
    /**
     * getting phone contacts with name and mobile numbers
     * removing repeated mobile numbers
     */
    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {
        val contacts: MutableList<ContactItem> = ArrayList()
        try {
            cursor?.let {
                val totalCount: Int = it.count
                if (totalCount > 0) {
                    it.moveToFirst()
                    while (!it.isAfterLast) {
                        val name: String =
                            it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                        val mobileNo: String = mobileNoFormatter(
                            it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        )
                        contacts.add(ContactItem(name, mobileNo, null))
                        it.moveToNext()
                    }
                }

            }
            this.contacts = contacts.distinctBy { it.mobileNo }
            if (contacts.isNotEmpty()) {
                binding.svContact.visibility = View.VISIBLE
            }
            if (::adapter.isInitialized)
                adapter.updateItems(this.contacts)
        } catch (e: java.lang.IllegalArgumentException) {
            Timber.i(e)
        } catch (ex: Exception) {
            Timber.i(ex)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        // contact loader reset callback
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CONTACT -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    LoaderManager.getInstance(this).restartLoader(0, null, this)
                } else {
                    finish()
                }
            }
        }
    }

    private fun onSelectItem(item: ContactItem) {
        val intent = Intent()
        intent.putExtra(RESULT, item)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun onSearch(phrase: String) {
        val filteredContacts: List<ContactItem> = contacts.filter { contactItem ->
            contactItem.name.lowercase(
                Locale.getDefault()
            )
                .contains(phrase) || contactItem.mobileNo.contains(phrase)
        }
        if (::adapter.isInitialized)
            adapter.updateItems(filteredContacts)
    }
}
