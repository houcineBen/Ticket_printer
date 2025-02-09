package com.example.ticket_printer.fragments.print.printHandler

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.Navigation.findNavController
import com.example.ticket_printer.R
import com.example.ticket_printer.fragments.print.Database.DBhandler
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.IOException
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID
import androidx.fragment.app.FragmentActivity
import java.io.OutputStream

class HandlePrint(val context: Context, val view: View, val handleInput: HandleInput, val dbHandler: DBhandler, val activity: FragmentActivity?) {


    val nameEditText = view.findViewById<AutoCompleteTextView>(R.id.lastNameEditText)
    val phoneEditText = view.findViewById<AutoCompleteTextView>(R.id.phoneEditText)
    val detailEditText = view.findViewById<EditText>(R.id.descriptionText)
    val printButton = view.findViewById<Button>(R.id.printButton)


    fun printOrder() {



        printButton.setOnClickListener {
//            val name = nameEditText.text.toString()
            val name = convertFrenchToASCII(nameEditText.text.toString())
            val rawPhone = phoneEditText.text.toString()
            val details = detailEditText.text.toString().split("\n").joinToString(separator = "\n") { "- $it" }


//            val details = convertFrenchToASCII(detailEditText.text.toString())

//             Code to send this data to the printer
            if (rawPhone.length >= 10) {
                val formattedPhone = handleInput.formatPhoneNumber(rawPhone)
                var dateTime = "\n" + "depose le " + getCurrentDateTime() + "\n"
                dateTime = "\n" + "deposéle " + getCurrentDateTime() + "\n"
                //Toast.makeText(this, dateTime, Toast.LENGTH_SHORT).show()

                printToPrinter(name, formattedPhone, details, dateTime, activity)
            } else {
                Toast.makeText(context, "Le Numéro doit contenir 10 chiffres minimum", Toast.LENGTH_SHORT).show()
            }

//            insert data in the dataBase
            dbHandler.insertDataToDatabase()
//            var dateTime = "\n" + "depose le " + getCurrentDateTime() + "\n"
//            printToPrinter(name, rawPhone, details, dateTime)
        }

    }

    private fun getCurrentDateTime(): String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm", Locale.getDefault())
        return LocalDateTime.now().format(formatter)
    }



    private fun printToPrinter(name: String, phone: String, details: String, dateTime: String, activity: FragmentActivity?) {
        Thread {
            val printerAddress = "10:22:33:AB:47:E3" // Replace with your printer's Bluetooth address
            val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
                val device: BluetoothDevice? = bluetoothAdapter.getRemoteDevice(printerAddress)
                var socket: BluetoothSocket? = null
                var outputStream: OutputStream? = null

                try {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                        socket = device?.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
                        socket?.connect()
                        outputStream = socket?.outputStream
                    } else {
                        requestBluetoothPermission()
                        return@Thread
                    }

                    if (outputStream == null) {
                        throw IOException("Output stream is null. Failed to connect to the printer.")
                    }

                    val printData = "$name\n$phone\nDétails:\n$details"
                    val qrCode = "smsto:$phone\n"

                    val centerAlign = byteArrayOf(0x1B, 0x61, 0x01)
                    val leftAlign = byteArrayOf(0x1B, 0x61, 0x00)
                    val normalSizeCommand = byteArrayOf(0x1B, 0x21, 0x21)
                    val smallSizeCommand = byteArrayOf(0x1B, 0x21, 0x00)

                    outputStream.apply {
                        write(leftAlign)
                        write(normalSizeCommand)
                        write(printData.toByteArray(Charset.forName("GB2312")))
                        write(smallSizeCommand)
                        write(dateTime.toByteArray(Charset.forName("GB2312")))
                        write(centerAlign)

                        val modelCommand = byteArrayOf(0x1D, 0x28, 0x6B, 0x04, 0x00, 0x31, 0x41, 0x32, 0x00) // Model
                        val sizeCommand = byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x43, 0x04) // Size
                        val errorCommand = byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x45, 0x30) // Error correction
                        val storeCommand = byteArrayOf(0x1D, 0x28, 0x6B, (3 + qrCode.length).toByte(), 0x00, 0x31, 0x50, 0x30) // Store data
                        val printCommand = byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x51, 0x30) // Print QR code

                        write(normalSizeCommand)
                        write(modelCommand)
                        write(sizeCommand)
                        write(errorCommand)
                        write(storeCommand)
                        write(qrCode.toByteArray(Charsets.UTF_8))
                        write(printCommand)
                        write("\n\n".toByteArray(Charsets.UTF_8))

                        flush()
                    }

                    // Clear input fields
                    activity?.runOnUiThread {
                        nameEditText.text.clear()
                        phoneEditText.text.clear()
                        detailEditText.text.clear()
                    }

                } catch (e: IOException) {
                    activity?.runOnUiThread {
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    try {
                        Thread.sleep(5000)
                        outputStream?.close()
                        socket?.close()
                    } catch (e: IOException) {
                        // Handle exceptions
                    }
                }
            } else {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Bluetooth is not enabled or not available", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun requestBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 1)
        }
    }



        fun convertFrenchToASCII(text: String): String {
        val mapping = mapOf(
            'é' to 'e', 'è' to 'e', 'ê' to 'e', 'ë' to 'e',
            'à' to 'a', 'â' to 'a', 'ä' to 'a',
            'î' to 'i', 'ï' to 'i',
            'ô' to 'o', 'ö' to 'o',
            'ù' to 'u', 'û' to 'u', 'ü' to 'u',
            'ç' to 'c',
            'É' to 'E', 'È' to 'E', 'Ê' to 'E', 'Ë' to 'E',
            'À' to 'A', 'Â' to 'A', 'Ä' to 'A',
            'Î' to 'I', 'Ï' to 'I',
            'Ô' to 'O', 'Ö' to 'O',
            'Ù' to 'U', 'Û' to 'U', 'Ü' to 'U',
            'Ç' to 'C'
        )

        return text.map { char -> mapping[char] ?: char }.joinToString("")
    }

}


