package com.myvet.myvet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class SignUp : AppCompatActivity() {
    private lateinit var Pet_Btn: ImageView
    private lateinit var Vet_Btn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        Pet_Btn = findViewById(R.id.pet_button)
        //Vet_Btn = findViewById(R.id.vet_button)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@SignUp, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        })

        Pet_Btn.setOnClickListener {
            Log.i("Test pet", "The user will go to pet sign up page")
            val intent = Intent(this, Pet_signUp::class.java)
            startActivity(intent)
            finish()
        }
    }

}