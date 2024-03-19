package com.example.app

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Alert : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationbtn: Button
    private lateinit var messageEditText: EditText
    private lateinit var contactEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alert, container, false)

        messageEditText = view.findViewById(R.id.messageloader)
        contactEditText = view.findViewById(R.id.contactsid)
        val saveButton1: Button = view.findViewById(R.id.btn1)
        val loadButton1: Button = view.findViewById(R.id.btn2)
        val saveButton2: Button = view.findViewById(R.id.btn3)
        val loadButton2: Button = view.findViewById(R.id.btn4)
        locationbtn = view.findViewById(R.id.btn5)
        val panic: Button = view.findViewById(R.id.panic)

        saveButton1.setOnClickListener {
            saveMessageToFile()
        }

        loadButton1.setOnClickListener {
            readMessageFromFile()
        }

        saveButton2.setOnClickListener {
            saveContactToFile()
        }

        loadButton2.setOnClickListener {
            readContactFromFile()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Set click listener for location button
        locationbtn.setOnClickListener {
            getLastLocation()
        }

        // Set click listener for panic button
        panic.setOnClickListener {
            val message = messageEditText.text.toString().trim()
            val contacts = contactEditText.text.toString().trim()

            if (message.isNotEmpty() && contacts.isNotEmpty()) {
                sendSMS(contacts, message)
            } else {
                Toast.makeText(requireContext(), "Message or contacts field is empty", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun saveMessageToFile() {
        val message = messageEditText.text.toString().trim()
        if (message.isNotEmpty()) {
            saveToFile("message.txt", message)
        } else {
            Toast.makeText(requireContext(), "Message field is empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readMessageFromFile() {
        val message = readFromFile("message.txt")
        if (message != null) {
            messageEditText.setText(message)
        } else {
            Toast.makeText(requireContext(), "No saved message found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveContactToFile() {
        val contacts = contactEditText.text.toString().trim()
        if (contacts.isNotEmpty()) {
            saveToFile("contact.txt", contacts)
        } else {
            Toast.makeText(requireContext(), "Contacts field is empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readContactFromFile() {
        val contacts = readFromFile("contact.txt")
        if (contacts != null) {
            contactEditText.setText(contacts)
        } else {
            Toast.makeText(requireContext(), "No saved contacts found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToFile(filename: String, data: String) {
        val file = File(requireContext().filesDir, filename)
        file.writeText(data)
        Toast.makeText(requireContext(), "Save Successful", Toast.LENGTH_SHORT).show()
    }

    private fun readFromFile(filename: String): String? {
        val file = File(requireContext().filesDir, filename)
        return if (file.exists()) {
            file.readText()
        } else {
            null
        }
    }

    private fun getLastLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener(requireActivity(), OnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val lastLocation = "$latitude,$longitude"
                    copyToClipboard(lastLocation)
                    Toast.makeText(
                        requireContext(),
                        "Location Copied!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Couldn't retrieve location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
            .addOnFailureListener(requireActivity(), OnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Failed to get location: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            })
    }

    private fun copyToClipboard(text: String) {
        val clipboardManager =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Location", text)
        clipboardManager.setPrimaryClip(clipData)
    }

    private fun sendSMS(contacts: String, message: String) {
        val context = context
        val activity = activity
        if (context != null && activity != null) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.SEND_SMS
            )

            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                val smsManager = SmsManager.getDefault()
                val contactList = contacts.split(",").map { it.trim() }

                GlobalScope.launch(Dispatchers.IO) {
                    for (contact in contactList) {
                        try {
                            smsManager.sendTextMessage(contact, null, message, null, null)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Message sent to $contact", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Failed to send message to $contact", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.SEND_SMS),
                    SEND_SMS_PERMISSION_REQUEST_CODE
                )
            }
        }
    }


    private fun showToastOnUI(message: String) {
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val SEND_SMS_PERMISSION_REQUEST_CODE = 123

    }

}
