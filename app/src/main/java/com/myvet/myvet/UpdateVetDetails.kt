package com.myvet.myvet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class UpdateVetDetails : AppCompatActivity() {
    private lateinit var expertise: EditText
    private lateinit var aboutMe: EditText

    private lateinit var erorrMessage: TextView
    private lateinit var updateDetails: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_vet_details)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@UpdateVetDetails, VetWindow::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        })

        expertise = findViewById(R.id.expertise)
        aboutMe = findViewById(R.id.aboutMe)

        erorrMessage = findViewById(R.id.errorMessage)
        updateDetails = findViewById(R.id.submitButton)

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        updateDetails.setOnClickListener {
            val vetDocumentRef = db.collection("users").document(user!!.uid)

            vetDocumentRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val currentAboutMe = document.getString("aboutMe") ?: ""
                    val updatedAboutMe = "$currentAboutMe\n${aboutMe.text}"

                    // Prepare updated data (without overwriting the medical history)
                    val updatedData = hashMapOf(
                        "aboutMe" to updatedAboutMe,
                        "expertise" to expertise.text.toString(),
                    )

                    vetDocumentRef.update(updatedData as Map<String, Any>)
                        .addOnSuccessListener {
                            Log.i("Update vet details", "Vet details updated successfully")
                            Toast.makeText(
                                this,
                                getString(R.string.vet_details_updated_successfully),
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(Intent(this, VetWindow::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Log.e("Update vet details", "Vet details update failed")
                            Toast.makeText(this,
                                getString(R.string.vet_details_update_failed), Toast.LENGTH_SHORT)
                                .show()

                }

                //There is no vet details document for the user - create a new one
                val newVetData = hashMapOf(
                    "aboutMe" to aboutMe.text.toString(),
                    "expertise" to expertise.text.toString(),
                )

                vetDocumentRef.set(newVetData)
                    .addOnSuccessListener {
                        Log.i("Update vet details", "Vet details saved successfully")
                        Toast.makeText(this,
                            getString(R.string.vet_details_saved_successfully), Toast.LENGTH_SHORT)
                            .show()

                        startActivity(Intent(this, VetWindow::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Log.e("Update vet details", "Failed to save vet details")
                        Toast.makeText(this, "Failed to save vet details", Toast.LENGTH_SHORT)
                            .show()
                    }
            }
        }.addOnFailureListener {
            Log.e("Update vet details", "Failed to check vet existence")
            Toast.makeText(this, "Error checking vet details", Toast.LENGTH_SHORT).show()
        }
    }
}}

