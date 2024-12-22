package com.myvet.myvet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class PetOwnerWindow : AppCompatActivity() {

    private lateinit var UpdateDetails: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pet_owner_window)

        val username = intent.getStringExtra("USERNAME")
        val textView: TextView = findViewById(R.id.HelloText)
        textView.text = "Welcome $username"

        val Email = intent.getStringExtra("EMAIL")

        UpdateDetails = findViewById(R.id.UpdateDetails)

        UpdateDetails.setOnClickListener {
            val intent = Intent(this, UpdatePetDetails::class.java)
            intent.putExtra("EMAIL", Email)
            startActivity(intent)
        }
    }


}