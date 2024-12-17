package com.myvet.myvet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class SignUp : AppCompatActivity() {

    private lateinit var Back_Btn: Button
    private lateinit var Pet_Btn: ImageView
    private lateinit var Vet_Btn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        Back_Btn = findViewById(R.id.back_home)
        Pet_Btn = findViewById(R.id.pet_button)
        //Vet_Btn = findViewById(R.id.vet_button)

        Back_Btn.setOnClickListener {
            Log.i("Test back", "The user will go back to home page")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        Pet_Btn.setOnClickListener {
            Log.i("Test pet", "The user will go to pet sign up page")
            val intent = Intent(this, Pet_signUp::class.java)
            startActivity(intent)
            finish()
        }
    }

}