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

class VeterinarianSignUp : AppCompatActivity() {
    private lateinit var clinicName: EditText
    private lateinit var clinicLocation: EditText
    private lateinit var yearsOfExperience: EditText
    private lateinit var expertise: EditText
    private lateinit var aboutMe: EditText
    private lateinit var register: Button
    private lateinit var errorMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_veterinarian_sign_up)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@VeterinarianSignUp, SignUp::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        })

        clinicName = findViewById(R.id.clinicName)
        clinicLocation = findViewById(R.id.clinicLocation)
        yearsOfExperience = findViewById(R.id.yearsOfExperience)
        expertise = findViewById(R.id.expertise)
        aboutMe = findViewById(R.id.aboutMe)
        register = findViewById(R.id.submitButton)
        errorMessage = findViewById(R.id.errorMessage)

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        //Set the register button to be disabled
        register.isEnabled = false

        //Function to check if the user has typed in all the information needed for the registration
        fun checkInputs() {
            val clinicName = clinicName.text.toString()
            val clinicLocation = clinicLocation.text.toString()
            val expertise = yearsOfExperience.text.toString()
            register.isEnabled = clinicName.isNotEmpty() && clinicLocation.isNotEmpty() && expertise.isNotEmpty()
        }

        clinicName.addTextChangedListener { checkInputs() }
        clinicLocation.addTextChangedListener { checkInputs() }
        yearsOfExperience.addTextChangedListener { checkInputs() }

        register.setOnClickListener {
            db.collection("users").add(
                hashMapOf(
                    "uid" to user?.uid,
                    "type" to "vet",
                    "clinicName" to clinicName.text.toString(),
                    "address" to clinicLocation.text.toString(),
                    "expertise" to expertise.text.toString(),
                    "yearsOfExperience" to yearsOfExperience.text.toString(),
                    "aboutMe" to aboutMe.text.toString(),
                )
            )
                .addOnSuccessListener {
                    Log.i(
                        "Sign up vet",
                        "Vet registered successfully"
                    )
                }
                .addOnFailureListener {
                    Log.i(
                        "Sign up vet",
                        "Vet registration failed"
                    )
                }

            val intent = Intent(this, VetWindow::class.java)
            startActivity(intent)
            finish()
        }
    }
}