package com.example.ticket_printer.fragments.print.printHandler

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Filter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.example.ticket_printer.R
import com.example.ticket_printer.data.Client
import androidx.appcompat.widget.AppCompatEditText
import com.example.ticket_printer.data.UserViewModel
import java.nio.file.DirectoryStream

class HandleInput(private val view: View, private val context: Context) {

    // In your HandleInput class
    private val lastNameEditText = view.findViewById<AppCompatAutoCompleteTextView>(R.id.lastNameEditText)
    private val nameEditText = view.findViewById<AppCompatAutoCompleteTextView>(R.id.nameEditText)
    private val phoneEditText = view.findViewById<AppCompatAutoCompleteTextView>(R.id.phoneEditText)
//    private val firstNameEditText = view.findViewById<EditText>(R.id.nameEditText)

    fun setupAutoComplete(lifecycleOwner: LifecycleOwner, viewModel: UserViewModel) {
        setupSearchField(R.id.phoneEditText, viewModel, lifecycleOwner) { query ->
            viewModel.searchByPhone(query)
        }

        setupSearchField(R.id.nameEditText, viewModel, lifecycleOwner) { query ->
            viewModel.searchByName(query)
        }

        setupSearchField(R.id.lastNameEditText, viewModel, lifecycleOwner) { query ->
            viewModel.searchByLastName(query)
        }

    }


    @SuppressLint("FragmentLiveDataObserve")
    private fun setupSearchField(
        fieldId: Int,
        viewModel: UserViewModel,
        lifecycleOwner: LifecycleOwner,
        queryFunction: (String) -> Unit
    ) {
        val autoCompleteView = view.findViewById<AutoCompleteTextView>(fieldId)
        val adapter = ClientArrayAdapter(context, R.layout.dropdown_client_item)
        autoCompleteView.setAdapter(adapter)

        autoCompleteView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.takeIf { it.length >= 2 }?.let {
                    queryFunction.invoke(it.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        viewModel.clientsLiveData.observe(lifecycleOwner, Observer { clients ->
            Log.d("DROPDOWN_DEBUG", "Observed clients: $clients")
            adapter.clear()
            adapter.addAll(clients)
            adapter.notifyDataSetChanged()
            if (autoCompleteView.isFocused) {
                autoCompleteView.showDropDown()
            }


        })

        autoCompleteView.setOnItemClickListener { _, _, position, _ ->
            val selectedClient = adapter.getItem(position)
            selectedClient?.let {
                phoneEditText.setText(it.phone)
                nameEditText.setText(it.name)
                lastNameEditText.setText(it.lastName)
            }
            autoCompleteView.clearFocus()
        }
    }

    fun formatPhoneNumber(phone: String): String = when {
        phone.length == 10 -> phone.chunked(2).joinToString(" ")
        else -> phone
    }

    fun validateInput(): Boolean = !TextUtils.isEmpty(nameEditText.text) &&
            !TextUtils.isEmpty(lastNameEditText.text) &&
            phoneEditText.text.length >= 10

    fun focusNameField() {
        lastNameEditText.requestFocus().also {
            if (it) showSoftKeyboard(nameEditText)
        }
    }

    private fun showSoftKeyboard(view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private inner class ClientArrayAdapter(
        context: Context,
        resource: Int
    ) : ArrayAdapter<Client>(context, resource) {

        override fun getFilter(): Filter = object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                // Since you are doing filtering externally, simply return the full list
                results.values = (0 until count).map { getItem(it) }
                results.count = count
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                // Do nothing here because the list is already updated from LiveData.
            }
        }


        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.dropdown_client_item, parent, false)

            getItem(position)?.let { client ->
                view.findViewById<TextView>(R.id.tvName).text = client.name
                view.findViewById<TextView>(R.id.tvLastName).text = client.lastName
                view.findViewById<TextView>(R.id.tvPhone).text = formatPhoneNumber(client.phone)
            }
            return view
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            return getView(position, convertView, parent)
        }
    }
}