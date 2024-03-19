package com.example.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony.TextBasedSmsColumns
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.app.databinding.ActivitySignUpBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.w3c.dom.Text

class SignUpActivity : AppCompatActivity() {


    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase=FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")
        val backButton: Button = findViewById(R.id.back)

        // Set OnClickListener to the back button
        backButton.setOnClickListener {
            // Create an Intent to navigate back to SignInActivity
            val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
            startActivity(intent)
            finish() // Finish current activity (SignUpActivity)
        }
        binding.signupbtn.setOnClickListener{
            val signupusername=binding.signupusername.text.toString()
            val signupemail=binding.signupemail.text.toString()
            val signuppassword=binding.signuppassword.text.toString()

            if(signupusername.isNotEmpty()&&signupemail.isNotEmpty()&&signuppassword.isNotEmpty()){
                signupUser(signupemail,signupemail,signuppassword)
            }
            else {
                Toast.makeText(this@SignUpActivity,"All fields are mandatory!",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun signupUser(username:String,email:String,password:String){
        databaseReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(
            object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(!dataSnapshot.exists()){
                    val id=databaseReference.push().key
                    val userData = UserData(id, username, email, password)
                    databaseReference.child(id!!).setValue(userData)
                    Toast.makeText(this@SignUpActivity,"SignUp Successful",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SignUpActivity,SignInActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this@SignUpActivity,"Sign Up Failed User Exists",Toast.LENGTH_SHORT).show()
                }
            }

                override fun onCancelled(databaseError: DatabaseError){
                    val errorMessage = "Database Error: ${databaseError.message}"
                    Toast.makeText(this@SignUpActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    Log.e("SignUpActivity", errorMessage) // Log the error message for further analysis
                }

            })
    }
}