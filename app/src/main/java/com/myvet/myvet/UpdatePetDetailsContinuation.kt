package com.myvet.myvet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast


class UpdatePetDetailsContinuation : AppCompatActivity() {

    private lateinit var PetWeight: EditText
    private lateinit var PetGender: EditText
    private lateinit var MedicalHistory: EditText
    private lateinit var ErorrMessage: TextView
    private lateinit var UpdateDetails: Button
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_pet_details_continuation)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@UpdatePetDetailsContinuation, UpdatePetDetails::class.java)
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

        //Set the register button to be disabled
        UpdateDetails.isEnabled = false

        //Function to check if the user has typed in all the information needed for the registration

        //Function to check if the user has typed in all the information needed for the registration
        fun checkInputs() {
            val medicalHistory = MedicalHistory.text.toString()
            UpdateDetails.isEnabled =  medicalHistory.isNotEmpty()
        }

        MedicalHistory.addTextChangedListener { checkInputs() }

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        UpdateDetails.setOnClickListener {
            val petCollection = db.collection("users").document(user!!.uid).collection("petDetails")
            val petDocRef = petCollection.document("Pet")

            petDocRef.get().addOnSuccessListener { document ->

                        val currentMedicalHistory = document.getString("medicalHistory") ?: ""
                        val updatedMedicalHistory = "$currentMedicalHistory\n${MedicalHistory.text}"

                        // Prepare updated data (without overwriting the medical history)
                        val updatedData = hashMapOf(
                            "petWeight" to PetWeight.text.toString(),
                            "medicalHistory" to updatedMedicalHistory
                        )

                        petDocRef.update(updatedData as Map<String, Any>)
                            .addOnSuccessListener {
                                Log.i("Update pet details", "Pet details updated successfully")
                                Toast.makeText(this, "Pet details updated successfully", Toast.LENGTH_SHORT).show()

                                startActivity(Intent(this, PetOwnerWindow::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                Log.e("Update pet details", "Pet details update failed")
                                Toast.makeText(this, "Pet details update failed", Toast.LENGTH_SHORT).show()
                            }

            }.addOnFailureListener {
                Log.e("Update pet details", "Failed to check pet existence")
                Toast.makeText(this, "Error checking pet details", Toast.LENGTH_SHORT).show()
            }
        }
    }
}