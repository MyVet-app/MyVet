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
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class UpdatePetDetails : AppCompatActivity() {

    private lateinit var PetName: EditText
    private lateinit var PetAge: EditText
    private lateinit var PetType: EditText
    private lateinit var Next: Button
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

        PetName = findViewById(R.id.name)
        PetType = findViewById(R.id.typeOfPet)
        PetAge = findViewById(R.id.age)
        Next = findViewById(R.id.next)

        //Set the register button to be disabled
        Next.isEnabled = false

        //Function to check if the user has typed in all the information needed for the registration
        fun checkInputs() {
            val name = PetName.text.toString()
            val typeOfPet = PetType.text.toString()
            val age = PetAge.text.toString()
            Next.isEnabled = name.isNotEmpty() && typeOfPet.isNotEmpty() &&
                    age.isNotEmpty()
        }

        PetName.addTextChangedListener { checkInputs() }
        PetType.addTextChangedListener { checkInputs() }
        PetAge.addTextChangedListener { checkInputs() }

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        Next.setOnClickListener {
            val petCollection = db.collection("users").document(user!!.uid).collection("petDetails")
            val petDocRef = petCollection.document("Pet")

            petDocRef.get().addOnSuccessListener { document ->
                val enteredPetName = PetName.text.toString()

                if (document.exists()) {
                    val existingPetName = document.getString("petName")

                    if (existingPetName == enteredPetName) {
                        // The user did not try to enter a new pet (the pet name is the same)

                        // Prepare updated data (without overwriting the medical history)
                        val updatedData = hashMapOf(
                            "age" to PetAge.text.toString(),
                            "typeOfPet" to PetType.text.toString()
                        )

                        petDocRef.update(updatedData as Map<String, Any>)
                            .addOnSuccessListener {
                                Log.i("Update pet details", "Pet details updated successfully")
                                Toast.makeText(this, "Pet details updated successfully", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, UpdatePetDetailsContinuation::class.java)
                                Log.d("Navigation", "Attempting to open UpdatePetDetails2") // for check
                                startActivity(intent)

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
                        "petType" to PetType.text.toString(),
                        "petAge" to PetAge.text.toString()
                    )

                    petDocRef.set(newPetData)
                        .addOnSuccessListener {
                            Log.i("Update pet details", "Pet details saved successfully")
                            Toast.makeText(this, "Pet details saved successfully", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, UpdatePetDetailsContinuation::class.java))
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