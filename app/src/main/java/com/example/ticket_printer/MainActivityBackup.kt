package com.example.ticket_printer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Import Bluetooth classes
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.view.inputmethod.InputMethodManager
import java.nio.charset.Charset


class MainActivityBackup : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var printButton: Button
    private lateinit var detailEditText: EditText

    companion object {
        internal const val REQUEST_CODE_BLUETOOTH_PERMISSION = 1001
    }



// ...

    private fun getCurrentDateTime(): String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm", Locale.getDefault())
        return LocalDateTime.now().format(formatter)
    }

    private fun formatPhoneNumber(phone: String): String {
        return phone.chunked(2).joinToString(" ")
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkBluetoothPermissions()

        nameEditText = findViewById(R.id.nameEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        detailEditText = findViewById(R.id.descriptionText)
        printButton = findViewById(R.id.printButton)

        nameEditText.requestFocus()

        if (nameEditText.requestFocus()) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(nameEditText, InputMethodManager.SHOW_IMPLICIT)
        }

        printButton.setOnClickListener {
            val name = convertFrenchToASCII(nameEditText.text.toString())
            val rawPhone = phoneEditText.text.toString()
            val details = convertFrenchToASCII(detailEditText.text.toString())

//             Code to send this data to the printer
            if (rawPhone.length == 10) {
                val formattedPhone = formatPhoneNumber(rawPhone)
                var dateTime = "\n" + "depose le " + getCurrentDateTime() + "\n"
                dateTime = "\n" + "depose le " + getCurrentDateTime() + "\n"
                //Toast.makeText(this, dateTime, Toast.LENGTH_SHORT).show()

                printToPrinter(name, formattedPhone, details, dateTime)
            } else {
                Toast.makeText(this, "Le Numéro doit avoir 10 chiffres", Toast.LENGTH_SHORT).show()
            }
//            var dateTime = "\n" + "depose le " + getCurrentDateTime() + "\n"
//            printToPrinter(name, rawPhone, details, dateTime)
        }
    }

    private fun checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_CONNECT), REQUEST_CODE_BLUETOOTH_PERMISSION)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_BLUETOOTH_PERMISSION) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Bluetooth permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun printToPrinter(name: String, phone: String, details: String, dateTime: String) {
        Thread(Runnable {
            val printerAddress = "10:22:33:AB:47:E3" // Replace with your printer's Bluetooth address
            val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
                val device: BluetoothDevice? = bluetoothAdapter.getRemoteDevice(printerAddress)
                val socket: BluetoothSocket? = device?.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
                val outputStream = socket?.outputStream
                try {
                    socket?.connect()

                    // Use outputStream to send data to the printer

                    // Your printing logic here
                    val boldOn = byteArrayOf(0x1B, 0x45, 0x01)
                    val boldOff = byteArrayOf(0x1B, 0x45, 0x00)
                    val centerAlign = byteArrayOf(0x1B, 0x61, 0x01)
                    val centerAlignOff = byteArrayOf(0x1B, 0x61, 0x00)
                    val doubleSizeCommand = byteArrayOf(0x1B, 0x21, 0x30)
                    val normalSizeCommand = byteArrayOf(0x1B, 0x21, 0x00)
                    val cutCommand = byteArrayOf(0x1B, 0x56, 0x00)
                    val codeTableByte: Byte = 0x1C // Example: 0x04 or 0x06, depending on the printer's manual

// ESC/POS command to select the character code table
                    val selectCodeTableCommand = byteArrayOf(0x1B, 0x74, codeTableByte)


                    // Example ESC/POS commands
                    val printData = "\n$name\n\n$phone\n\n Details:\n $details\n\n"



                    val qrCode = "smsto:" + phone + "\n"
                    outputStream?.write(selectCodeTableCommand)
                    outputStream?.write(boldOn)
                    outputStream?.write(doubleSizeCommand)
                    outputStream?.write(centerAlign)
                    outputStream?.write(printData.toByteArray(Charset.defaultCharset()))
                    outputStream?.write(normalSizeCommand)
                    outputStream?.write(dateTime.toByteArray(Charset.defaultCharset()))




                    val modelCommand = byteArrayOf(0x1D, 0x28, 0x6B, 0x04, 0x00, 0x31, 0x41, 0x32, 0x00) // Model
                    val sizeCommand = byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x43, 0x06) // Size
                    val errorCommand = byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x45, 0x30) // Error correction
                    val storeCommand = byteArrayOf(0x1D, 0x28, 0x6B, (3 + qrCode.length).toByte(), 0x00, 0x31, 0x50, 0x30) // Store data
                    val printCommand = byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x51, 0x30) // Print QR code

                    // Sending QR code commands to the printer
                    outputStream?.write(normalSizeCommand)
                    outputStream?.write(modelCommand)
                    outputStream?.write(sizeCommand)
                    outputStream?.write(errorCommand)
                    outputStream?.write(storeCommand)
                    outputStream?.write(qrCode.toByteArray(Charsets.UTF_8))
                    outputStream?.write(printCommand)
                    outputStream?.write("\n\n\n".toByteArray(Charsets.UTF_8))













                    // Additional commands can be sent here

                    outputStream?.flush()

                    // Make sure to run UI updates on the main thread
                    runOnUiThread {
                        nameEditText.text.clear()
                        phoneEditText.text.clear()
                        detailEditText.text.clear()
                        // Any other UI updates
                    }
                } catch (e: IOException) {
                    // Handle exceptions
                    runOnUiThread {
                        Toast.makeText(this@MainActivityBackup, e.message, Toast.LENGTH_SHORT).show()
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
                runOnUiThread {
                    Toast.makeText(this@MainActivityBackup, "Bluetooth is not enabled or not available", Toast.LENGTH_SHORT).show()
                }
            }
        }).start()
    }

}
