package com.myvet.myvet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.firestore.FirebaseFirestore


class Pet_signUp  : AppCompatActivity() {

   private lateinit var Back_Btn: Button
   private lateinit var pet_owner_name: EditText
   private lateinit var petName: EditText
   private lateinit var password: EditText
   private lateinit var email: EditText
   private lateinit var address: EditText
   private lateinit var agePet: EditText
   private lateinit var Historical_medical: EditText
   private lateinit var register: Button
   private lateinit var errorMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pet_signup)

        Back_Btn = findViewById(R.id.back_home)
        pet_owner_name = findViewById(R.id.user_name_pet)
        password = findViewById(R.id.password_pet)
        email = findViewById(R.id.email_pet)
        petName = findViewById(R.id.pet_name)
        address = findViewById(R.id.home_address)
        agePet = findViewById(R.id.age_pet)
        Historical_medical = findViewById(R.id.historical_medical)
        register = findViewById(R.id.sign_up_pet)
        errorMessage = findViewById(R.id.error_message_register)

        val db = FirebaseFirestore.getInstance()

        Back_Btn.setOnClickListener {
            Log.i("Test back", "The user will go back to home page")
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            finish()
        }

        //Set the register button to be disabled
        register.isEnabled = false
        //Function to check if the user has typed in all the information needed for the registration
        fun checkInputs() {
            val pet_owner_name = pet_owner_name.text.toString()
            val password = password.text.toString()
            val email = email.text.toString()
            val petName = petName.text.toString()
            val address = address.text.toString()
            val agePet = agePet.text.toString()
            register.isEnabled = password.isNotEmpty() && email.isNotEmpty() &&
                    pet_owner_name.isNotEmpty() && petName.isNotEmpty() && address.isNotEmpty() && agePet.isNotEmpty()
        }

        pet_owner_name.addTextChangedListener { checkInputs() }
        password.addTextChangedListener { checkInputs() }
        email.addTextChangedListener { checkInputs() }
        petName.addTextChangedListener { checkInputs() }
        address.addTextChangedListener { checkInputs() }
        agePet.addTextChangedListener { checkInputs() }

        register.setOnClickListener {
            val emailOfClient = email.text.toString()

            val documentRef = db.collection("pet owner").document(emailOfClient)
            documentRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        Log.i("Registration problem - pet owner", "The email already exists")
                        errorMessage.text = "המייל כבר קיים במערכת"
                        errorMessage.visibility = TextView.VISIBLE
                        val handler = android.os.Handler()
                        handler.postDelayed({
                            errorMessage.visibility = TextView.GONE
                        }, 5000)
                        pet_owner_name.text.clear()
                        password.text.clear()
                        email.text.clear()
                        petName.text.clear()
                        address.text.clear()
                        agePet.text.clear()
                        Historical_medical.text.clear()
                    } else {
                        Log.i("Registration pet owner", "The email does not exist")
                        val user = hashMapOf(
                            "pet owner name" to pet_owner_name.text.toString(),
                            "password" to password.text.toString(),
                            "email" to email.text.toString(),
                            "pet name" to petName.text.toString(),
                            "address" to address.text.toString(),
                            "age pet" to agePet.text.toString(),
                            "historical medical" to Historical_medical.text.toString()
                        )
                        db.collection("pet owner").document(email.text.toString()).set(user)
                            .addOnSuccessListener {
                                Log.i("Registration pet owner", "The user has been added to the database")
                            }
                            .addOnFailureListener {
                                Log.i("Registration pet owner", "The user has not been added to the database")
                            }
                        val intent = Intent(this, MainActivity::class.java)//change to pet page
                        startActivity(intent)
                        finish()
                    }
                }

        }



    }
}