package com.example.ticket_printer.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ticket_printer.data.UserViewModel
import com.example.ticket_printer.databinding.FragmentQrCodeScannerBinding
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QRCodeScannerFragment : Fragment() {

    private var _binding: FragmentQrCodeScannerBinding? = null
    private val binding get() = _binding!!
    private lateinit var barcodeView: DecoratedBarcodeView

    // This flag prevents multiple scans from triggering repeated navigation.
    private var hasScanned = false

    // Request permission using the Activity Result API.
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startScanning()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Camera permission is required for scanning",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentQrCodeScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        barcodeView = binding.barcodeScanner

        // Check for camera permission and request it if necessary.
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startScanning()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startScanning() {
        // Use continuous decoding; you can also use decodeSingle if you prefer.
        barcodeView.decodeContinuous { result ->
            if (!hasScanned) {
                hasScanned = true
                val scannedText = result.text.substringAfter(":").replace(" ", "").trim()

                // Get an instance of UserViewModel from the hosting activity.
                val userViewModel =
                    ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

                // Check if the scanned text (assumed to be a phone number) exists in the database.
                lifecycleScope.launch(Dispatchers.IO) {
                    val clientId = userViewModel.getClientIdByPhone(scannedText)
                    val clientName = userViewModel.getClientNameByPhone(scannedText)
                    val clientLastName = userViewModel.getClientLastNameByPhone(scannedText)
                    val clientEmail = userViewModel.getClientEmailByPhone(scannedText)
                    withContext(Dispatchers.Main) {
                        if (clientId != null) {
                            // If found, navigate to the client's profile.
                            // Adjust the navigation action and arguments as needed.
                            val action =
                                QRCodeScannerFragmentDirections
                                    .actionQrCodeScannerFragmentToClientProfileFragment(
                                        clientId = clientId,
                                        clientName = clientName ?: "",      // Populate if available
                                        clientPhone = scannedText,
                                        clientEmail = clientEmail ?: "Pas d'adresse mail",     // Populate if available
                                        clientLastName = clientLastName ?: ""   // Populate if available
                                    )
                            findNavController().navigate(action)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                scannedText,
                                Toast.LENGTH_SHORT
                            ).show()
                            // Allow scanning again.
                            hasScanned = false
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
