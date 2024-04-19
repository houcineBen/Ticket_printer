package com.example.ticket_printer.fragments.print.Database

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.ticket_printer.R
import com.example.ticket_printer.data.Client
import com.example.ticket_printer.data.UserViewModel
import com.example.ticket_printer.fragments.print.printHandler.HandleInput

class DBhandler(val context: Context, val view: View, val handleInput: HandleInput, val mClientViewModel: UserViewModel) {


    fun insertDataToDatabase() {
        val clientName: String = view?.findViewById<EditText>(R.id.nameEditText)?.text.toString()
        val phoneNumber: String = view?.findViewById<EditText>(R.id.phoneEditText)?.text.toString()

        if(handleInput.inputCheck(clientName, phoneNumber)) {
            val client = Client(0, clientName, phoneNumber)
            mClientViewModel.addClient(client)
            Toast.makeText(context, "Données ajoutée", Toast.LENGTH_LONG).show()
        }
    }
}