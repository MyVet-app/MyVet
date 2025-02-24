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

class AddPet : AppCompatActivity() {

    private lateinit var petName: EditText
    private lateinit var petType: EditText
    private lateinit var petAge: EditText
    private lateinit var petGender: EditText
    private lateinit var next: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_pet)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@AddPet, OwnerSignUp::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        })

        petName = findViewById(R.id.name)
        petType = findViewById(R.id.typeOfPet)
        petAge = findViewById(R.id.age)
        petGender = findViewById(R.id.gender)
        next = findViewById(R.id.next)

        //Set the register button to be disabled
        next.isEnabled = false

        //Function to check if the user has typed in all the information needed for the registration
        fun checkInputs() {
            val petName = petName.text.toString()
            val petType = petType.text.toString()
            val petAge = petAge.text.toString()
            val petGender = petGender.text.toString()
            next.isEnabled = petName.isNotEmpty() && petType.isNotEmpty() &&
                    petAge.isNotEmpty() && petGender.isNotEmpty()
        }

        petName.addTextChangedListener { checkInputs() }
        petType.addTextChangedListener { checkInputs() }
        petAge.addTextChangedListener { checkInputs() }
        petGender.addTextChangedListener { checkInputs() }

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        next.setOnClickListener {
            val petCollection = db.collection("users").document(user!!.uid).collection("petDetails")
            val petDocRef = petCollection.document("Pet")

            petDocRef.get().addOnSuccessListener {
                val enteredPetName = petName.text.toString()
                // There is no pet details document for the user - create a new one
                val newPetData = hashMapOf(
                    "petName" to enteredPetName,
                    "petType" to petType.text.toString(),
                    "petAge" to petAge.text.toString(),
                    "petGender" to petGender.text.toString()
                )

                petDocRef.set(newPetData)
                    .addOnSuccessListener {
                        Log.i("Update pet details", "Pet details saved successfully")
                        Toast.makeText(this, getString(R.string.pet_details_saved_successfully), Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(this, PetOwnerWindow::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Log.e("Update pet details", "Failed to save pet details")
                        Toast.makeText(this, getString(R.string.failed_to_save_pet_details), Toast.LENGTH_SHORT)
                            .show()
                    }
            }.addOnFailureListener {
                Log.e("Update pet details", "Failed to check pet existence")
                Toast.makeText(this, getString(R.string.failed_to_save_pet_details), Toast.LENGTH_SHORT).show()
            }
        }
    }
}