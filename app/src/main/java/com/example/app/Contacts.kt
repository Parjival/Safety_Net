package com.example.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.io.FileOutputStream
import android.content.Context

private const val FILE_NAME = "contacts.txt"

class Contacts : Fragment() {

    private lateinit var contactsEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)

        contactsEditText = view.findViewById(R.id.Contact_List)
        val saveButton: Button = view.findViewById(R.id.save)

        saveButton.setOnClickListener {
            saveContactsToFile()
        }

        return view
    }

    private fun saveContactsToFile() {
        val contacts = contactsEditText.text.toString().trim()
        if (contacts.isNotEmpty()) {
            try {
                val fos = requireContext().openFileOutput(FILE_NAME, Context.MODE_PRIVATE)
                fos.write(contacts.toByteArray())
                fos.close()
                Toast.makeText(
                    requireContext(),
                    "Contacts saved successfully",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    "Failed to save contacts",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Please enter contacts to save",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
