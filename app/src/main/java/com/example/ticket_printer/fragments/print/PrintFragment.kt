package com.example.ticket_printer.fragments.print


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ticket_printer.R
import com.example.ticket_printer.data.UserViewModel
import com.example.ticket_printer.fragments.print.Database.DBhandler
import com.example.ticket_printer.fragments.print.printHandler.HandleInput
import com.example.ticket_printer.fragments.print.printHandler.HandlePrint

class PrintFragment: Fragment() {

    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var printButton: Button
    private lateinit var detailEditText: EditText
    private lateinit var mClientViewModel: UserViewModel

    companion object {
        const val REQUEST_CODE_BLUETOOTH_PERMISSION = 1001
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_print, container, false)
        mClientViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        val inputHandler = HandleInput(view, requireContext())
        val dbHandler = DBhandler(requireContext(), view, inputHandler, mClientViewModel)
        val printHandler = HandlePrint(requireContext(), view, inputHandler, dbHandler, activity)

        checkBluetoothPermissions()
        inputHandler.nameFocus()
        inputHandler.handleDropDownMenu(viewLifecycleOwner, mClientViewModel)
        printHandler.printOrder()

        nameEditText = view.findViewById(R.id.nameEditText)
        phoneEditText = view.findViewById(R.id.phoneEditText)
        detailEditText = view.findViewById(R.id.descriptionText)
        printButton = view.findViewById(R.id.printButton)

        return view
    }

    private fun checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                REQUEST_CODE_BLUETOOTH_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_BLUETOOTH_PERMISSION) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Bluetooth permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}