package com.example.ticket_printer.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.ticket_printer.data.UserViewModel
import com.example.ticket_printer.databinding.FragmentClientProfileBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ClientProfileFragment : Fragment() {

    private val args: ClientProfileFragmentArgs by navArgs()

    private lateinit var userViewModel: UserViewModel
    private var _binding: FragmentClientProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentClientProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Set client details from navigation arguments
        binding.clientNameProfile.text = args.clientName
        binding.textViewPhoneNumber.text = args.clientPhone
        binding.textViewEmailAddress.text = args.clientEmail
        binding.clientLastNameProfile.text = args.clientLastName

        // Retrieve client ID from navigation arguments
        val clientId = args.clientId

        // Fetch the last 3 visits for the client (without LiveData)
        fetchLastThreeVisits(clientId)

        // 1. Call the client
        binding.iconCall.setOnClickListener {
            val phoneNumber = args.clientPhone
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            startActivity(callIntent)
        }

        // 2. Send an SMS
        binding.iconSms.setOnClickListener { view ->
            val phoneNumber = args.clientPhone
            val smsIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phoneNumber"))
            startActivity(smsIntent)
        }

        // 3. Send an Email
        binding.iconEmail.setOnClickListener {
            val emailAddress = args.clientEmail
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$emailAddress")
                // Optionally, you can add extras like subject or text
                // putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
                // putExtra(Intent.EXTRA_TEXT, "Email body here")
            }
            startActivity(emailIntent)
        }

        binding.buttonVeloPret.setOnClickListener {
            val message = "Bonjour,\n\nVotre vélo est prêt ! Vous pouvez venir le récupérer aujourd'hui avant 20h00.\n\nNos horaires d'ouverture sont :\nde Lundi à Samedi\n- Matin : 9h00 à 13h00\n- Après-midi : 14h30 à 20h00\n\nCordialement"
            val phoneNumber = args.clientPhone
            val smsIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phoneNumber")).apply {
                putExtra("sms_body", message)
            }
            startActivity(smsIntent)
        }

        binding.buttonVeloRecupere.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                userViewModel.updateHasBike(clientId, false)
            }
            Toast.makeText(requireContext(), "Le velo a été récupéré", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchLastThreeVisits(clientId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val visits = userViewModel.getLastThreeVisitsSync(clientId)

            withContext(Dispatchers.Main) {
                if (visits.isNotEmpty()) {
                    visits.forEachIndexed { index, visit ->
                        // Convert bikeFixDetails into bullet points
                        val bikeFixDetails = visit.bikeFixDetails
                        val detailsList = bikeFixDetails.split("\n")
                        val bulletedDetails = detailsList.joinToString("\n") { "• $it" }

                        // Split date and time
                        val dateTimeParts = visit.dateTime.split(" ")
                        val date = dateTimeParts.getOrElse(0) { "" }
                        val time = dateTimeParts.getOrElse(1) { "" }

                        when (index) {
                            0 -> {
                                binding.visitOneTextView.text = bulletedDetails
                                binding.visitOneDate.text = date
                                binding.visitOneTime.text = time
                            }
                            1 -> {
                                binding.visitTwoTextView.text = bulletedDetails
                                binding.visitTwoDate.text = date
                                binding.visitTwoTime.text = time
                            }
                            2 -> {
                                binding.visitThreeTextView.text = bulletedDetails
                                binding.visitThreeDate.text = date  // Fixed typo: visitThree instead of visitTwo
                                binding.visitThreeTime.text = time
                            }
                        }
                    }
                } else {
                    binding.visitOneTextView.text = "No visit history found."
                    binding.visitTwoTextView.text = ""
                    binding.visitThreeTextView.text = ""
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}