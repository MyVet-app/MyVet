package com.myvet.myvet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SecondPage : AppCompatActivity() {

    private lateinit var BackBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second_page)

        val username = intent.getStringExtra("USERNAME")
        val textView: TextView = findViewById(R.id.textView)
        textView.text = "Welcome $username"

        BackBtn = findViewById(R.id.BackBtn)

        BackBtn.setOnClickListener {
            Log.i("Test back", "The user will go back to home page")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}