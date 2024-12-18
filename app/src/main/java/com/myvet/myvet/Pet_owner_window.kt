package com.myvet.myvet

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Pet_owner_window : AppCompatActivity() {

    private lateinit var HelloTxt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_owner_window)

        val username = intent.getStringExtra("USERNAME")
        val textView: TextView = findViewById(R.id.HelloText)
        textView.text = "Welcome $username"

    }


}