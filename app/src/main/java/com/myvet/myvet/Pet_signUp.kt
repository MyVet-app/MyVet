package com.myvet.myvet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
        errorMessage = findViewById(R.id.error_message)

        val db = FirebaseFirestore.getInstance()

        Back_Btn.setOnClickListener {
            Log.i("Test back", "The user will go back to home page")
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            finish()
        }

        register.isEnabled = false

        if(pet_owner_name.text.isNotEmpty() && petName.text.isNotEmpty() && password.text.isNotEmpty() &&
            email.text.isNotEmpty() && address.text.isNotEmpty()){
            register.isEnabled = true
        }

    register.setOnClickListener {
        if (register.isEnabled == false){Log.i("Test rigister", "Not all the information are filled")}
        //Log.i("Test rigister", "The user want to register")

        // if the email exist in the database, the user will not be able to register
        // if the email does not exist in the database, the user will be able to register
        // all the filled information will be stored in the database
        db.collection("pet owner").document(email.text.toString()).get().addOnSuccessListener {
            if (it.exists()) {
                Log.i("Registration problem - pet owner", "The email already exists")
                errorMessage.text = "The email already exists"
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
            }
        }
        val intent = Intent(this, MainActivity::class.java)//change to pet page
        startActivity(intent)
        finish()
    }



    }
}