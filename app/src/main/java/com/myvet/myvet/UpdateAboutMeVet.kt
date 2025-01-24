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
    private lateinit var ClinicName: EditText
    private lateinit var ClinicLocation: EditText
    private lateinit var YearsOfExperience: EditText
    private lateinit var Expertise: EditText
    private lateinit var AboutMe: EditText
    private lateinit var ErorrMessage: TextView
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

        ClinicName = findViewById(R.id.ClinicName)
        ClinicLocation = findViewById(R.id.ClinicLocation)
        YearsOfExperience = findViewById(R.id.YearsOfExperience)
        Expertise = findViewById(R.id.Expertise)
        AboutMe = findViewById(R.id.AboutMe)
        ErorrMessage = findViewById(R.id.ErrorMessage)
        UpdateDetails = findViewById(R.id.Update)

        val db = FirebaseFirestore.getInstance()

        //Set the register button to be disabled
        UpdateDetails.isEnabled = false

        //Function to check if the user has typed in all the information needed for the registration
        fun checkInputs() {
            val ClinicName = ClinicName.text.toString()
            val ClinicLocation = ClinicLocation.text.toString()
            val Expertise = YearsOfExperience.text.toString()
            UpdateDetails.isEnabled =
                ClinicName.isNotEmpty() && ClinicLocation.isNotEmpty() && Expertise.isNotEmpty()
        }

        ClinicName.addTextChangedListener { checkInputs() }
        ClinicLocation.addTextChangedListener { checkInputs() }
        YearsOfExperience.addTextChangedListener { checkInputs() }

        UpdateDetails.setOnClickListener {
            val email = intent.getStringExtra("EMAIL") ?: return@setOnClickListener
            val documentRef = db.collection("veterinarian").document(email)
            documentRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val aboutMeVet = hashMapOf(
                            "clinicName" to ClinicName.text.toString(),
                            "clinicLocation" to ClinicLocation.text.toString(),
                            "yearsOfExperience" to YearsOfExperience.text.toString(),
                            "expertise" to Expertise.text.toString(),
                            "aboutMe" to AboutMe.text.toString()
                        )
                        db.collection("veterinarian").document(email).collection("aboutMe").add(aboutMeVet)
                            .addOnSuccessListener {
                            Log.i(
                                "UpdateAboutMeVet",
                                "The about me details have been updated successfully"
                            )
                        }
                            .addOnFailureListener {
                                Log.i(
                                    "UpdateAboutMeVet",
                                    "The about me details have not been updated successfully"
                                )
                            }
                        ErorrMessage.text = "הפרטים עודכנו בהצלחה"
                        ErorrMessage.visibility = TextView.VISIBLE
                        val handler = android.os.Handler(Looper.getMainLooper())
                        handler.postDelayed({
                            ErorrMessage.visibility = TextView.GONE
                        },5000)
                        val intent = Intent(this, VetWindow::class.java)
                        startActivity(intent)
                        finish()
                    }
                }

        }
    }

}