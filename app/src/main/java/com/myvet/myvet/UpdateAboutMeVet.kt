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

class UpdateAboutMeVet : AppCompatActivity() {
    private lateinit var YearsOfExperience: EditText
    private lateinit var AboutMe: EditText
    private lateinit var ErrorMessage: TextView
    private lateinit var UpdateDetails: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_about_me_vet)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@UpdateAboutMeVet, VetWindow::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        })

        YearsOfExperience = findViewById(R.id.YearsOfExperience)
        AboutMe = findViewById(R.id.AboutMe)
        ErrorMessage = findViewById(R.id.ErrorMessage)
        UpdateDetails = findViewById(R.id.Update)

        val db = FirebaseFirestore.getInstance()

        // Set the update button to be disabled initially
        UpdateDetails.isEnabled = false

        // Function to check if the required inputs are provided
        fun checkInputs() {
            val yearsOfExperience = YearsOfExperience.text.toString()
            val aboutMeText = AboutMe.text.toString()
            UpdateDetails.isEnabled = aboutMeText.isNotEmpty() && yearsOfExperience.isNotEmpty()
        }

        YearsOfExperience.addTextChangedListener { checkInputs() }
        AboutMe.addTextChangedListener { checkInputs() }

        UpdateDetails.setOnClickListener {
            val email = intent.getStringExtra("EMAIL") ?: return@setOnClickListener
            val documentRef = db.collection("veterinarian").document(email)

            documentRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val aboutMeUpdate = mapOf(
                            "aboutMe" to AboutMe.text.toString()
                        )

                        documentRef.collection("aboutMe").get()
                            .addOnSuccessListener { subCollection ->
                                if (!subCollection.isEmpty) {
                                    // Update the first document in the sub-collection
                                    val subDocRef = subCollection.documents[0].reference
                                    subDocRef.update(aboutMeUpdate)
                                        .addOnSuccessListener {
                                            Log.i(
                                                "UpdateAboutMeVet",
                                                "The 'about me' details have been updated successfully"
                                            )
                                            ErrorMessage.text = "הפרטים עודכנו בהצלחה"
                                            ErrorMessage.visibility = TextView.VISIBLE

                                            val handler = android.os.Handler(Looper.getMainLooper())
                                            handler.postDelayed({
                                                ErrorMessage.visibility = TextView.GONE
                                            }, 5000)

                                            val intent = Intent(this, VetWindow::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                        .addOnFailureListener {
                                            Log.e(
                                                "UpdateAboutMeVet",
                                                "Failed to update 'about me' details"
                                            )
                                            ErrorMessage.text = "עדכון הפרטים נכשל"
                                            ErrorMessage.visibility = TextView.VISIBLE
                                        }
                                } else {
                                    // No document found in the sub-collection
                                    ErrorMessage.text = "לא נמצאו פרטים לעדכון"
                                    ErrorMessage.visibility = TextView.VISIBLE
                                }
                            }
                            .addOnFailureListener {
                                Log.e("UpdateAboutMeVet", "Failed to fetch 'about me' sub-collection")
                                ErrorMessage.text = "שגיאה בגישה לפרטים"
                                ErrorMessage.visibility = TextView.VISIBLE
                            }
                    } else {
                        // Main document not found
                        ErrorMessage.text = "המשתמש לא נמצא במערכת"
                        ErrorMessage.visibility = TextView.VISIBLE
                    }
                }
                .addOnFailureListener {
                    Log.e("UpdateAboutMeVet", "Failed to fetch veterinarian document")
                    ErrorMessage.text = "שגיאה בגישה לפרטי המשתמש"
                    ErrorMessage.visibility = TextView.VISIBLE
                }
        }
    }
}
