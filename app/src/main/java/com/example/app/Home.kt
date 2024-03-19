package com.example.app

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class Home : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Find the logout button
        val logoutButton = view.findViewById<Button>(R.id.out)

        // Set click listener for the logout button
        logoutButton.setOnClickListener {
            // Call logout function to redirect to SignInActivity
            logout()
        }

        return view
    }

    private fun logout() {
        // Redirect to SignInActivity
        val intent = Intent(requireContext(), SignInActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // Finish the current activity (Home)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            Home().apply {
                arguments = Bundle().apply {}
            }
    }
}
