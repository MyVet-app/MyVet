package com.myvet.myvet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast


class UpdatePetDetails : AppCompatActivity() {

    private lateinit var petWeight: EditText
    private lateinit var medicalHistory: EditText
    private lateinit var updateDetails: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_pet_details)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@UpdatePetDetails, PetOwnerWindow::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        })

        petWeight = findViewById(R.id.weight)
        medicalHistory = findViewById(R.id.medicalHistory)
        updateDetails = findViewById(R.id.update)

        updateDetails.isEnabled = false

        //Function to check if the user has typed in all the information needed for the registration
        fun checkInputs() {
            val medicalHistory = medicalHistory.text.toString()
            updateDetails.isEnabled =  medicalHistory.isNotEmpty()
        }

        medicalHistory.addTextChangedListener { checkInputs() }

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        updateDetails.setOnClickListener {
            val petCollection = db.collection("users").document(user!!.uid).collection("petDetails")
            val petDocRef = petCollection.document("Pet")

            petDocRef.get().addOnSuccessListener { document ->

                        val currentMedicalHistory = document.getString("medicalHistory") ?: ""
                        val updatedMedicalHistory = "$currentMedicalHistory\n${medicalHistory.text}"

                        // Prepare updated data (without overwriting the medical history)
                        val updatedData = hashMapOf(
                            "petWeight" to petWeight.text.toString(),
                            "medicalHistory" to updatedMedicalHistory
                        )

                        petDocRef.update(updatedData as Map<String, Any>)
                            .addOnSuccessListener {
                                Log.i("Update pet details", "Pet details updated successfully")
                                Toast.makeText(this, getString(R.string.pet_details_updated_successfully), Toast.LENGTH_SHORT).show()

                                startActivity(Intent(this, PetOwnerWindow::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                Log.e("Update pet details", "Pet details update failed")
                                Toast.makeText(this, getString(R.string.pet_details_update_failed), Toast.LENGTH_SHORT).show()
                            }

            }.addOnFailureListener {
                Log.e("Update pet details", "Failed to check pet existence")
                Toast.makeText(this, getString(R.string.error_loading_pet_details), Toast.LENGTH_SHORT).show()
            }
        }
    }
}