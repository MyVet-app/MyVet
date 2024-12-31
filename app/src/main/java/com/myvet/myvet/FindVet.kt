package com.myvet.myvet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.time.LocalDate
import java.time.LocalTime

class FindVet : AppCompatActivity() {
    private lateinit var vetsList: LinearLayout

    private fun updateUIWithData(snapshot: QuerySnapshot) {
        // Clear the existing UI
        vetsList.removeAllViews()

        // Build the UI elements dynamically based on the updated data
        for (vet in snapshot) {
            val vetContainer = LinearLayout(this)
            vetContainer.orientation = LinearLayout.HORIZONTAL

            val name = vet.getString("name")
            val address = vet.getString("address")

            val availabilityText = TextView(this)
            availabilityText.text =
                "Name: $name\nAddress: $address"

            val selectButton = Button(this)
            selectButton.text = "Select"
            selectButton.setOnClickListener {
                val i = Intent(this, MakeAppointment::class.java)
                i.putExtra("vetId", vet.id)
                startActivity(i)
                finish()
            }

            availabilityText.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.7f) // 70% width
            selectButton.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.3f) // 30% width

            vetContainer.addView(availabilityText)
            vetContainer.addView(selectButton)

            vetsList.addView(vetContainer)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_find_vet)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        vetsList = findViewById(R.id.vetsList)

        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .whereEqualTo("type", "vet")
            .addSnapshotListener { snapshot, _ -> updateUIWithData(snapshot!!) }
//            .addOnSuccessListener { documents ->
//
//            }
//            .addOnFailureListener { exception ->
//                Log.e("FirestoreError", "Error getting documents: ", exception)
//            }
    }
}