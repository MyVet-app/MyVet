package com.myvet.myvet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.logginapp.R

class SignUp : AppCompatActivity() {

    private lateinit var Back_Btn: Button
    private lateinit var Pet_Btn: Button
    private lateinit var Vet_Btn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        Back_Btn = findViewById(R.id.back_home)
        //Pet_Btn = findViewById(R.id.pet_button)
        //Vet_Btn = findViewById(R.id.vet_button)

        Back_Btn.setOnClickListener {
            Log.i("Test back", "The user will go back to home page")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val vet_icon: ImageView = findViewById(R.id.vet_button)
        vet_icon.setOnClickListener {
            Log.i("Test vet", "The user will go to the vet sign up page")
            //val intent = Intent(this, VetSignUp::class.java)
            startActivity(intent)
            finish()
        }

        val pet_icon: ImageView = findViewById(R.id.pet_button)
        pet_icon.setOnClickListener {
            Log.i("Test vet", "The user will go to the vet sign up page")
            //val intent = Intent(this, PetSignUp::class.java)
            startActivity(intent)
            finish()
        }
    }

}