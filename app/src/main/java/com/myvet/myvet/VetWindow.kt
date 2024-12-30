package com.myvet.myvet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class VetWindow : AppCompatActivity() {
    private lateinit var addAvailability: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vet_window)

        val user = FirebaseAuth.getInstance().currentUser

        val textView: TextView = findViewById(R.id.HelloText)
        textView.text = "Welcome ${user?.displayName}"

        addAvailability = findViewById(R.id.AddAvailability)
        addAvailability.setOnClickListener {
            val intent = Intent(this, AddAvailability::class.java)
            startActivity(intent)
        }
    }
}

// Availability:
// {
//      "vet": vetid,
//      "date": ...,
//      "window": time range (start time - end time),
// }