package com.example.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SMS : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

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
        val view = inflater.inflate(R.layout.fragment_s_m_s, container, false)

        val messageEditText: EditText = view.findViewById(R.id.Messagesender)
        val saveButton: Button = view.findViewById(R.id.button4)
        val loadButton: Button = view.findViewById(R.id.button9)

        saveButton.setOnClickListener {
            val message = messageEditText.text.toString().trim()
            if (message.isNotEmpty()) {
                saveMessageToFile(message)
            }
        }

        loadButton.setOnClickListener {
            val message = readMessageFromFile()
            if (message != null) {
                messageEditText.setText(message)
            } else {
                Toast.makeText(requireContext(), "No saved message found", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun saveMessageToFile(message: String) {
        val filename = "message.txt"
        val fileContents = message

        val file = File(requireContext().filesDir, filename)
        file.writeText(fileContents)

        // Display a toast indicating successful saving
        Toast.makeText(requireContext(), "Save Successful", Toast.LENGTH_SHORT).show()
    }

    private fun readMessageFromFile(): String? {
        val filename = "message.txt"
        val file = File(requireContext().filesDir, filename)

        if (!file.exists()) {
            return null
        }

        val fileInputStream = FileInputStream(file)
        val inputStreamReader = InputStreamReader(fileInputStream)
        val bufferedReader = BufferedReader(inputStreamReader)
        val stringBuilder = StringBuilder()
        var line: String? = null

        try {
            while ({ line = bufferedReader.readLine(); line }() != null) {
                stringBuilder.append(line)
            }
            bufferedReader.close()
            return stringBuilder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SMS().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
