package com.myvet.myvet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UpdatePetDetails : AppCompatActivity() {

    private lateinit var petName: EditText
    private lateinit var petType: EditText
    private lateinit var petWeight: EditText
    private lateinit var petAge: EditText
    private lateinit var petGender: EditText
    private lateinit var medicalHistory: EditText
    private lateinit var updateDetails: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_pet_details)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@UpdatePetDetails, PetOwnerWindow::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
        })

        petName = findViewById(R.id.PetName)
        petType = findViewById(R.id.typeOfPet)
        petWeight = findViewById(R.id.weight)
        petAge = findViewById(R.id.PetAge)
        petGender = findViewById(R.id.gender)
        medicalHistory = findViewById(R.id.medicalHistory)
        updateDetails = findViewById(R.id.update)

        //Set the register button to be disabled
        updateDetails.isEnabled = false

        //Function to check if the user has typed in all the information needed for the registration
        fun checkInputs() {
            val petName = petName.text.toString()
            val petType = petType.text.toString()
            val petAge = petAge.text.toString()
            val medicalHistory = medicalHistory.text.toString()
            updateDetails.isEnabled = petName.isNotEmpty() && petType.isNotEmpty() &&
                    petAge.isNotEmpty() && medicalHistory.isNotEmpty()
        }

        petName.addTextChangedListener { checkInputs() }
        petType.addTextChangedListener { checkInputs() }
        petAge.addTextChangedListener { checkInputs() }
        medicalHistory.addTextChangedListener { checkInputs() }

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        updateDetails.setOnClickListener {
            val petCollection = db.collection("users").document(user!!.uid).collection("petDetails")
            val petDocRef = petCollection.document("Pet")

            petDocRef.get().addOnSuccessListener { document ->
                val enteredPetName = petName.text.toString()

                if (document.exists()) {
                    val existingPetName = document.getString("petName")

                    if (existingPetName == enteredPetName) {
                        // The user did not try to enter a new pet (the pet name is the same)
                        val updatedData = hashMapOf(
                            "petWeight" to petWeight.text.toString(),
                            "petAge" to petAge.text.toString(),
                            "medicalHistory" to medicalHistory.text.toString()
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
                    } else {
                        // The user tried to change the pet's name
                        Toast.makeText(this, "You cannot add a new pet!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // There is no pet details document for the user - create a new one
                    val newPetData = hashMapOf(
                        "petName" to enteredPetName,
                        "petType" to petType.text.toString(),
                        "petWeight" to petWeight.text.toString(),
                        "petAge" to petAge.text.toString(),
                        "petGender" to petGender.text.toString(),
                        "medicalHistory" to medicalHistory.text.toString()
                    )

                    petDocRef.set(newPetData)
                        .addOnSuccessListener {
                            Log.i("Update pet details", "Pet details saved successfully")
                            Toast.makeText(this, "Pet details saved successfully", Toast.LENGTH_SHORT).show()

                            startActivity(Intent(this, PetOwnerWindow::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Log.e("Update pet details", "Failed to save pet details")
                            Toast.makeText(this, "Failed to save pet details", Toast.LENGTH_SHORT).show()
                        }
                }
            }.addOnFailureListener {
                Log.e("Update pet details", "Failed to check pet existence")
                Toast.makeText(this, "Error checking pet details", Toast.LENGTH_SHORT).show()
            }
        }
    }
}