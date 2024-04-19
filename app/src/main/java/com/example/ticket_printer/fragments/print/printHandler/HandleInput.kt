package com.example.ticket_printer.fragments.print.printHandler

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.example.ticket_printer.R
import com.example.ticket_printer.data.UserViewModel

class HandleInput(view: View, context: Context) {

    val context = context
    val view = view
    val nameEditText = view.findViewById<AutoCompleteTextView>(R.id.nameEditText)

    fun handleDropDownMenu(lifecycleOwner: LifecycleOwner, mClientViewModel: UserViewModel) {

        val phoneAutoCompleteTextView: AutoCompleteTextView = view.findViewById(R.id.phoneEditText)
        val nameAutoCompleteTextView: AutoCompleteTextView = view.findViewById(R.id.nameEditText)
        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, ArrayList())
        phoneAutoCompleteTextView.setAdapter(adapter)
        nameAutoCompleteTextView.setAdapter(adapter)

        // Update adapter data in response to text input
        phoneAutoCompleteTextView.addTextChangedListener(object : TextWatcher {
            @SuppressLint("FragmentLiveDataObserve")
            override fun afterTextChanged(s: Editable?) {
                val phonePrefix = s.toString()
                if (phonePrefix.length >= 2) { // Only query if sufficient characters are typed
                    mClientViewModel.getClientByPhonePrefix(phonePrefix).observe(lifecycleOwner, Observer { clients ->
                        // Transform clients to a list of strings (e.g., phone numbers)
                        val nameAndNumber = clients.map { it.phone + "    " + it.name }
//                        Toast.makeText(context, nameAndNumber.toString(), Toast.LENGTH_LONG).show()
                        adapter.clear()
                        adapter.addAll(nameAndNumber)
                        adapter.notifyDataSetChanged()
                    })
                }
            }


            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}


        })
//        phoneAutoCompleteTextView.setOnFocusChangeListener { v, hasFocus ->
//            if (hasFocus) {
//                val hey: List<String> = arrayListOf("kjhkhmkjmlkj", "hjhg", "kjhkjh", "jgkjhk")
//                Toast.makeText(context, hey.toString(), Toast.LENGTH_LONG).show()
////                adapter.clear()
//                adapter.addAll("hjgjh")
////                adapter.notifyDataSetChanged()
//                phoneAutoCompleteTextView.showDropDown()
//
//
//            } else {
//
//            }
//        }



        nameAutoCompleteTextView.addTextChangedListener(object : TextWatcher {
            @SuppressLint("FragmentLiveDataObserve")
            override fun afterTextChanged(s: Editable?) {
                val name = s.toString()

                mClientViewModel.getClientByName(name).observe(lifecycleOwner, Observer { clients ->
                    // Transform clients to a list of strings (e.g., phone numbers)
                    val nameAndNumber = clients.map { it.phone + "    " + it.name }

                    adapter.clear()
                    adapter.addAll(nameAndNumber)
                    adapter.notifyDataSetChanged()
                })

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}


        })

        phoneAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->


            // Get the selected phone number
            val selectedNameAndPhone = adapter.getItem(position)

            // Assuming you have an EditText for the name
            val nameEditText = view.findViewById<EditText>(R.id.nameEditText)
            val phoneEditText = view.findViewById<EditText>(R.id.phoneEditText)

            // Set the name field with the selected client's name
//            selectedClient?.let {
//                Toast.makeText(requireContext(), selectedClient?.name, Toast.LENGTH_LONG).show()
            phoneEditText.setText(selectedNameAndPhone!!.split("   ")[0].trim())
            nameEditText.setText(selectedNameAndPhone!!.split("   ")[1].trim())
//


        }

        nameAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->


            // Get the selected phone number
            val selectedNameAndPhone = adapter.getItem(position)

            // Assuming you have an EditText for the name
            val phoneEditText = view.findViewById<EditText>(R.id.phoneEditText)
            val nameEditText = view.findViewById<EditText>(R.id.nameEditText)

            // Set the name field with the selected client's name
//            selectedClient?.let {
//                Toast.makeText(requireContext(), selectedClient?.name, Toast.LENGTH_LONG).show()
            phoneEditText.setText(selectedNameAndPhone!!.split("   ")[0].trim())
            nameEditText.setText(selectedNameAndPhone!!.split("   ")[1].trim())
//


        }
    }

    fun formatPhoneNumber(phone: String): String {
        return phone.chunked(2).joinToString(" ")
    }

    fun inputCheck(clientName: String, phoneNumber: String): Boolean {
        return !TextUtils.isEmpty(clientName) && phoneNumber.length == 10
    }

    fun nameFocus() {
        nameEditText.requestFocus()

        if (nameEditText.requestFocus()) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(nameEditText, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}