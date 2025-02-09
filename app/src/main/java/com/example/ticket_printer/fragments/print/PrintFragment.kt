package com.example.ticket_printer.fragments.print

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope // Import lifecycleScope
import com.example.ticket_printer.R
import com.example.ticket_printer.data.UserViewModel
import com.example.ticket_printer.databinding.FragmentPrintBinding
import com.example.ticket_printer.fragments.print.Database.DBhandler
import com.example.ticket_printer.fragments.print.printHandler.HandleInput
import com.example.ticket_printer.fragments.print.printHandler.HandlePrint
import kotlinx.coroutines.CoroutineScope // Import CoroutineScope

class PrintFragment : Fragment() {

    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var detailEditText: EditText
    private lateinit var mClientViewModel: UserViewModel

    // View Binding
    private var _binding: FragmentPrintBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val REQUEST_CODE_BLUETOOTH_PERMISSION = 1001
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrintBinding.inflate(inflater, container, false)
        val view = binding.root

        mClientViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        val inputHandler = HandleInput(view, requireContext())

        // Initialize AutoComplete BEFORE other components
        inputHandler.setupAutoComplete(viewLifecycleOwner, mClientViewModel)

        // Move these AFTER AutoComplete setup
        val dbHandler = DBhandler(
            requireContext(),
            view,
            inputHandler,
            mClientViewModel,
            lifecycleScope
        )

        val printHandler = HandlePrint(requireContext(), view, inputHandler, dbHandler, activity)


        checkBluetoothPermissions()
//        inputHandler.focusNameField()
        inputHandler.setupAutoComplete(viewLifecycleOwner, mClientViewModel)
        printHandler.printOrder()

        nameEditText = view.findViewById(R.id.lastNameEditText)
        phoneEditText = view.findViewById(R.id.phoneEditText)
        detailEditText = view.findViewById(R.id.descriptionText)

        // Set up real-time phone number validation
        setupPhoneNumberValidation()

        return view
    }

    private fun setupPhoneNumberValidation() {
        phoneEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val phoneNumber = s?.toString() ?: ""

                // Check if the phone number starts with "00"
                if (phoneNumber.startsWith("00")) {
                    // No digit limit
                    return
                }

                // Limit to 10 digits
                if (phoneNumber.length > 10) {
                    // Remove extra characters
                    s?.delete(10, s.length)
                    phoneEditText.error = "Le nombre de caractères doit étre 10"
                } else {
                    phoneEditText.error = null
                }
            }
        })
    }

    private fun checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
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

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear the binding object to avoid memory leaks
        _binding = null
    }
}