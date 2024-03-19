package com.example.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.app.databinding.ActivitySignInBinding
import com.google.firebase.database.*

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Check if the user is already logged in
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            navigateToMain()
            return
        }

        binding.signinbutton.setOnClickListener {
            val signinemail = binding.signinemail.text.toString()
            val signinpassword = binding.signinpassword.text.toString()

            if (signinemail.isNotEmpty() && signinpassword.isNotEmpty()) {
                signinUser(signinemail, signinpassword)
            } else {
                Toast.makeText(this@SignInActivity, "All fields are mandatory!", Toast.LENGTH_SHORT).show()
            }
        }
        binding.signupbutton.setOnClickListener {
            startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
            finish()
        }
    }

    private fun signinUser(email: String, password: String) {
        databaseReference.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (userSnapshot in dataSnapshot.children) {
                            val userData = userSnapshot.getValue(UserData::class.java)

                            if (userData != null && userData.password == password) {
                                Toast.makeText(this@SignInActivity, "Sign In Successful!!", Toast.LENGTH_SHORT)
                                    .show()

                                // Save user session
                                sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()

                                navigateToMain()
                                return
                            }
                        }
                    }
                    Toast.makeText(this@SignInActivity, "Sign In Failed!!", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        this@SignInActivity,
                        "Database Error: ${databaseError.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun navigateToMain() {
        startActivity(Intent(this@SignInActivity, NewMainActivity::class.java))
        finish()
    }
}
