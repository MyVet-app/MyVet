//package com.myvet.myvet
//
//import android.content.Intent
//import android.os.Bundle
//import android.os.Looper
//import android.util.Log
//import android.widget.Button
//import android.widget.EditText
//import android.widget.TextView
//import androidx.activity.OnBackPressedCallback
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.widget.addTextChangedListener
//import com.google.firebase.firestore.FirebaseFirestore
//
//class UpdatePetDetails : AppCompatActivity() {
//
//    private lateinit var petName: EditText
//    private lateinit var petAge: EditText
//    private lateinit var petType: EditText
//    private lateinit var errorMessage: TextView
//    private lateinit var updateDetails: Button
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_update_pet_details)
//
//        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                val intent = Intent(this@UpdatePetDetails, PetOwnerWindow::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//                startActivity(intent)
//                finish()
//            }
//        })
//
//        petName = findViewById(R.id.PetName)
//        petType = findViewById(R.id.typeOfPet)
//        petAge = findViewById(R.id.PetAge)
//        errorMessage = findViewById(R.id.errorMessage)
//        updateDetails = findViewById(R.id.update)
//
//        val db = FirebaseFirestore.getInstance()
//
//        //Set the register button to be disabled
//        updateDetails.isEnabled = false
//
//        //Function to check if the user has typed in all the information needed for the registration
//        fun checkInputs() {
//            val petName = petName.text.toString()
//            val petType = petType.text.toString()
//            val petAge = petAge.text.toString()
//            updateDetails.isEnabled = petName.isNotEmpty() && petType.isNotEmpty() &&
//                    petAge.isNotEmpty()
//        }
//
//        petName.addTextChangedListener { checkInputs() }
//        petType.addTextChangedListener { checkInputs() }
//        petAge.addTextChangedListener { checkInputs() }
//
//
//        updateDetails.setOnClickListener {
//            val email = intent.getStringExtra("EMAIL") ?: return@setOnClickListener
//            val documentRef = db.collection("pet owner").document(email)
//            documentRef.get()
//                .addOnSuccessListener { document ->
//                    if (document.exists()) {
//                        val petDetails = hashMapOf(
//                            "pet name" to petName.text.toString(),
//                            "type" to petType.text.toString(),
//                            "age" to petAge.text.toString(),
//                        )
//                        db.collection("pet owner").document(email).collection("pet details").add(petDetails)
//                            .addOnSuccessListener {
//                                Log.i(
//                                    "Registration pet",
//                                    "The pet details have been added to the database"
//                                )
//                            }
//                            .addOnFailureListener {
//                                Log.i(
//                                    "Registration pet owner",
//                                    "The pet details have not been added to the database"
//                                )
//                            }
//                        errorMessage.text = "הפרטים עודכנו בהצלחה"
//                        errorMessage.visibility = TextView.VISIBLE
//                        val handler = android.os.Handler(Looper.getMainLooper())
//                        handler.postDelayed({
//                            errorMessage.visibility = TextView.GONE
//                        }, 5000)
//                        val intent = Intent(this, UpdatePetDetails2::class.java)
//                        startActivity(intent)
//                        finish()
//                    }
//                }
//        }
//    }
//}
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

class UpdatePetDetails : AppCompatActivity() {

    private lateinit var PetName: EditText
    private lateinit var PetType: EditText
    private lateinit var PetWeight: EditText
    private lateinit var PetAge: EditText
    private lateinit var PetGender: EditText
    private lateinit var MedicalHistory: EditText
    private lateinit var ErorrMessage: TextView
    private lateinit var UpdateDetails: Button
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

        PetName = findViewById(R.id.PetName)
        PetType = findViewById(R.id.typeOfPet)
        PetWeight = findViewById(R.id.weight)
        PetAge = findViewById(R.id.PetAge)
        PetGender = findViewById(R.id.gender)
        MedicalHistory = findViewById(R.id.medicalHistory)
        ErorrMessage = findViewById(R.id.errorMessage)
        UpdateDetails = findViewById(R.id.update)

        val db = FirebaseFirestore.getInstance()

        //Set the register button to be disabled
        UpdateDetails.isEnabled = false

        //Function to check if the user has typed in all the information needed for the registration
        fun checkInputs() {
            val petName = PetName.text.toString()
            val petType = PetType.text.toString()
            val petAge = PetAge.text.toString()
            val medicalHistory = MedicalHistory.text.toString()
            UpdateDetails.isEnabled = petName.isNotEmpty() && petType.isNotEmpty() &&
                    petAge.isNotEmpty() && medicalHistory.isNotEmpty()
        }

        PetName.addTextChangedListener { checkInputs() }
        PetType.addTextChangedListener { checkInputs() }
        PetAge.addTextChangedListener { checkInputs() }
        MedicalHistory.addTextChangedListener { checkInputs() }

        UpdateDetails.setOnClickListener {
            val email = intent.getStringExtra("EMAIL") ?: return@setOnClickListener
            val documentRef = db.collection("pet owner").document(email)
            documentRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val petDetails = hashMapOf(
                            "pet name" to PetName.text.toString(),
                            "type" to PetType.text.toString(),
                            "age" to PetAge.text.toString(),
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