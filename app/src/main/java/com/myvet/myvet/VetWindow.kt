package com.myvet.myvet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date

class VetWindow : AppCompatActivity() {
    private lateinit var addAvailability: Button
    private lateinit var availabilityWindowsList: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vet_window)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val user = FirebaseAuth.getInstance().currentUser

        val textView: TextView = findViewById(R.id.HelloText)
        textView.text = "Welcome ${user?.displayName}"

        addAvailability = findViewById(R.id.AddAvailability)
        addAvailability.setOnClickListener {
            val intent = Intent(this, AddAvailability::class.java)
            startActivity(intent)
        }

        availabilityWindowsList = findViewById(R.id.availabilityWindowsList)
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .document(user!!.uid)  // Use the uid as the document ID
            .collection("availability")
            .get()
            .addOnSuccessListener { result ->
                for (window in result) {
                    val date = LocalDate.parse(window.getString("date"))
                    val startTime = LocalTime.parse(window.getString("startTime"))
                    val endTime = LocalTime.parse(window.getString("endTime"))

                    val availabilityText = TextView(this)
                    availabilityText.text =
                        "Date: $date\nStart Time: $startTime\nEnd Time: $endTime"

                    // Optionally, set some layout parameters
                    availabilityText.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                    availabilityWindowsList.addView(availabilityText)
                }
            }
    }
}