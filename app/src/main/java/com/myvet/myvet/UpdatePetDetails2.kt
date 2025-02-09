package com.myvet.myvet

import android.annotation.SuppressLint
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

class UpdatePetDetails2 : AppCompatActivity() {

    private lateinit var PetWeight: EditText
    private lateinit var PetGender: EditText
    private lateinit var MedicalHistory: EditText
    private lateinit var ErorrMessage: TextView
    private lateinit var UpdateDetails: Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_pet_details)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@UpdatePetDetails2, UpdatePetDetails::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        })

        PetWeight = findViewById(R.id.weight)
        PetGender = findViewById(R.id.gender)
        MedicalHistory = findViewById(R.id.medicalHistory)
        ErorrMessage = findViewById(R.id.errorMessage)
        UpdateDetails = findViewById(R.id.update)

        val db = FirebaseFirestore.getInstance()

        //Set the register button to be disabled
        UpdateDetails.isEnabled = false

        //Function to check if the user has typed in all the information needed for the registration
        fun checkInputs() {
            val medicalHistory = MedicalHistory.text.toString()
            UpdateDetails.isEnabled = medicalHistory.isNotEmpty()
        }

        MedicalHistory.addTextChangedListener { checkInputs() }

        UpdateDetails.setOnClickListener {
            val email = intent.getStringExtra("EMAIL") ?: return@setOnClickListener
            val documentRef = db.collection("pet owner").document(email)
            documentRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val petDetails = hashMapOf(
                            "weight" to PetWeight.text.toString(),
                            "gender" to PetGender.text.toString(),
                            "medical history" to MedicalHistory.text.toString()
                        )
                        db.collection("pet owner").document(email).collection("pet details").add(petDetails)
                            .addOnSuccessListener {
                                Log.i(
                                    "Registration pet",
                                    "The pet details have been added to the database"
                                )
                            }
                            .addOnFailureListener {
                                Log.i(
                                    "Registration pet owner",
                                    "The pet details have not been added to the database"
                                )
                            }
                        ErorrMessage.text = "הפרטים עודכנו בהצלחה"
                        ErorrMessage.visibility = TextView.VISIBLE
                        val handler = android.os.Handler(Looper.getMainLooper())
                        handler.postDelayed({
                            ErorrMessage.visibility = TextView.GONE
                        }, 5000)
                        val intent = Intent(this, PetOwnerWindow::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
        }
    }
}