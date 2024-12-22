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

class VeterinarianSignUp : AppCompatActivity() {

    private lateinit var VetName: EditText
    private lateinit var Password: EditText
    private lateinit var Email: EditText
    private lateinit var ClinicName: EditText
    private lateinit var ClinicLocation: EditText
    private lateinit var yearsOfExperience: EditText
    private lateinit var TypeExperience: EditText
    private lateinit var AboutMe: EditText
    private lateinit var register: Button
    private lateinit var errorMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_veterinarian_sign_up)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@VeterinarianSignUp, SignUp::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        })
        VetName = findViewById(R.id.vetName)
        Password = findViewById(R.id.password)
        Email = findViewById(R.id.emailVet)
        ClinicName = findViewById(R.id.ClinicName)
        ClinicLocation = findViewById(R.id.ClinicLocation)
        yearsOfExperience = findViewById(R.id.YearsOfExperience)
        TypeExperience = findViewById(R.id.typeExperience)
        AboutMe = findViewById(R.id.AboutMe)
        register = findViewById(R.id.submitButton)
        errorMessage = findViewById(R.id.errorMessage)

        val db = FirebaseFirestore.getInstance()

        //Set the register button to be disabled
        register.isEnabled = false

        //Function to check if the user has typed in all the information needed for the registration
        fun checkInputs() {
            val VetName = VetName.text.toString()
            val password = Password.text.toString()
            val email = Email.text.toString()
            val ClinicName = ClinicName.text.toString()
            val ClinicLocation = ClinicLocation.text.toString()
            val yearsOfExperience = yearsOfExperience.text.toString()
            register.isEnabled = password.isNotEmpty() && email.isNotEmpty() &&
                    VetName.isNotEmpty() && ClinicName.isNotEmpty() && ClinicLocation.isNotEmpty() && yearsOfExperience.isNotEmpty()
        }

        VetName.addTextChangedListener { checkInputs() }
        Password.addTextChangedListener { checkInputs() }
        Email.addTextChangedListener { checkInputs() }
        ClinicName.addTextChangedListener { checkInputs() }
        ClinicLocation.addTextChangedListener { checkInputs() }
        yearsOfExperience.addTextChangedListener { checkInputs() }

        register.setOnClickListener {
            val email = Email.text.toString()
                val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
                if (email.matches(emailPattern)) {
                    Log.i("Registration vet", "The email is valid")
                } else {
                    errorMessage.text = "המייל שהוזן אינו תקני"
                    errorMessage.visibility = TextView.VISIBLE
                    val handler = android.os.Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        errorMessage.visibility = TextView.GONE
                    }, 5000)
                    Email.text.clear()
                    return@setOnClickListener
                }
            val documentRef = db.collection("Veterinarians").document(email)
            documentRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        Log.i("Registration problem - vet", "The email already exists")
                        errorMessage.text = "המייל כבר קיים במערכת"
                        errorMessage.visibility = TextView.VISIBLE
                        val handler = android.os.Handler(Looper.getMainLooper())
                        handler.postDelayed({
                            errorMessage.visibility = TextView.GONE
                        }, 5000)
                        VetName.text.clear()
                        Password.text.clear()
                        this.Email.text.clear()
                        ClinicName.text.clear()
                        ClinicLocation.text.clear()
                        yearsOfExperience.text.clear()
                    } else {
                        Log.i("Registration vet", "The email does not exist")
                        val user = hashMapOf(
                            "VetName" to VetName.text.toString(),
                            "Password" to Password.text.toString(),
                            "Email" to Email.text.toString(),
                            "ClinicName" to ClinicName.text.toString(),
                            "ClinicLocation" to ClinicLocation.text.toString(),
                            "yearsOfExperience" to yearsOfExperience.text.toString(),
                            "TypeExperience" to TypeExperience.text.toString(),
                            "AboutMe" to AboutMe.text.toString()
                        )
                        db.collection("Veterinarians").document(this.Email.text.toString()).set(user)
                            .addOnSuccessListener {
                                Log.i(
                                    "Registration Vet",
                                    "The user has been added to the database"
                                )
                            }
                            .addOnFailureListener {
                                Log.i(
                                    "Registration Vet",
                                    "The user has not been added to the database"
                                )
                            }
                        val intent = Intent(this, VetWindow::class.java)//change to vet page
                        intent.putExtra("USERNAME", VetName.text.toString())
                        startActivity(intent)
                        finish()
                    }
                }
        }
    }
}