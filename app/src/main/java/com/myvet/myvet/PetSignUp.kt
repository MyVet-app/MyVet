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
import com.google.firebase.firestore.FirebaseFirestore


class PetSignUp : AppCompatActivity() {
    private lateinit var username: EditText
    private lateinit var petName: EditText
    private lateinit var password: EditText
    private lateinit var email: EditText
    private lateinit var address: EditText
    private lateinit var petAge: EditText
    private lateinit var medicalHistory: EditText
    private lateinit var register: Button
    private lateinit var errorMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@PetSignUp, SignUp::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        })

        enableEdgeToEdge()
        setContentView(R.layout.activity_pet_signup)

        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        email = findViewById(R.id.email)
        petName = findViewById(R.id.petName)
        address = findViewById(R.id.homeAddress)
        petAge = findViewById(R.id.petAge)
        medicalHistory = findViewById(R.id.medicalHistory)
        register = findViewById(R.id.submitButton)
        errorMessage = findViewById(R.id.errorMessage)

        val db = FirebaseFirestore.getInstance()

        //Set the register button to be disabled
        register.isEnabled = false

        //Function to check if the user has typed in all the information needed for the registration
        fun checkInputs() {
            val username = username.text.toString()
            val password = password.text.toString()
            val email = email.text.toString()
            val petName = petName.text.toString()
            val address = address.text.toString()
            val petAge = petAge.text.toString()
            register.isEnabled = password.isNotEmpty() && email.isNotEmpty() &&
                    username.isNotEmpty() && petName.isNotEmpty() && address.isNotEmpty() && petAge.isNotEmpty()
        }

        username.addTextChangedListener { checkInputs() }
        password.addTextChangedListener { checkInputs() }
        email.addTextChangedListener { checkInputs() }
        petName.addTextChangedListener { checkInputs() }
        address.addTextChangedListener { checkInputs() }
        petAge.addTextChangedListener { checkInputs() }

        register.setOnClickListener {
            val email = email.text.toString()

            val documentRef = db.collection("pet owner").document(email)
            documentRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        Log.i("Registration problem - pet owner", "The email already exists")
                        errorMessage.text = "המייל כבר קיים במערכת"
                        errorMessage.visibility = TextView.VISIBLE
                        val handler = android.os.Handler(Looper.getMainLooper())
                        handler.postDelayed({
                            errorMessage.visibility = TextView.GONE
                        }, 5000)
                        username.text.clear()
                        password.text.clear()
                        this.email.text.clear()
                        petName.text.clear()
                        address.text.clear()
                        petAge.text.clear()
                        medicalHistory.text.clear()
                    } else {
                        Log.i("Registration pet owner", "The email does not exist")
                        val user = hashMapOf(
                            "pet owner name" to username.text.toString(),
                            "password" to password.text.toString(),
                            "email" to this.email.text.toString(),
                            "pet name" to petName.text.toString(),
                            "address" to address.text.toString(),
                            "age pet" to petAge.text.toString(),
                            "historical medical" to medicalHistory.text.toString()
                        )
                        db.collection("pet owner").document(this.email.text.toString()).set(user)
                            .addOnSuccessListener {
                                Log.i(
                                    "Registration pet owner",
                                    "The user has been added to the database"
                                )
                            }
                            .addOnFailureListener {
                                Log.i(
                                    "Registration pet owner",
                                    "The user has not been added to the database"
                                )
                            }
                        val intent = Intent(this, MainActivity::class.java)//change to pet page
                        startActivity(intent)
                        finish()
                    }
                }
        }
    }
}