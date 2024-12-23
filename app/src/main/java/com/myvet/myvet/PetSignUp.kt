package com.myvet.myvet

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class PetSignUp : AppCompatActivity() {
    private lateinit var address: EditText
    private lateinit var register: Button
    private lateinit var errorMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pet_signup)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@PetSignUp, SignUp::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        })

        address = findViewById(R.id.homeAddress)
        register = findViewById(R.id.submitButton)
        errorMessage = findViewById(R.id.errorMessage)

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        //Set the register button to be disabled
        register.isEnabled = false

        //Function to check if the user has typed in all the information needed for the registration
        fun checkInputs() {
            val address = address.text.toString()
            register.isEnabled = address.isNotEmpty()
        }

        address.addTextChangedListener { checkInputs() }

        register.setOnClickListener {
            val userData = hashMapOf(
                "type" to "owner",
                "address" to address
            )
            db.collection("users")
                .document(user!!.uid)  // Use the uid as the document ID
                .set(userData)  // Set the data in the document
                .addOnSuccessListener {
                    Log.i(
                        "Sign up pet owner",
                        "Pet owner registered successfully"
                    )
                }
                .addOnFailureListener {
                    Log.i(
                        "Sign up pet owner",
                        "Pet owner registration failed"
                    )
                }

            val intent = Intent(this, PetOwnerWindow::class.java)
            startActivity(intent)
            finish()
        }
    }
}