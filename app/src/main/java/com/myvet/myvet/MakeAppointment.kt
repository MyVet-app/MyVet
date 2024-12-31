package com.myvet.myvet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.time.LocalDate
import java.time.LocalTime

class MakeAppointment : AppCompatActivity() {
    private lateinit var vetId: String
    private lateinit var calendarView: CalendarView
    private lateinit var appointmentList: LinearLayout

    private fun displayAvailabilityWindows(windows: QuerySnapshot) {
        appointmentList.removeAllViews()

        for (window in windows) {
            val startTime = LocalTime.parse(window.getString("startTime"))
            val endTime = LocalTime.parse(window.getString("endTime"))

            var currentTime = startTime
            while (currentTime.isBefore(endTime)) {
                val nextTime = currentTime.plusMinutes(15)

                val appointmentContainer = LinearLayout(this)
                appointmentContainer.orientation = LinearLayout.HORIZONTAL

                val appointmentText = TextView(this)
                appointmentText.text = "$currentTime - $nextTime"

                val selectButton = Button(this)
                selectButton.text = "Select"
                selectButton.setOnClickListener {
                    Toast.makeText(this, "Appointment made successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }

                appointmentText.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.7f) // 70% width
                selectButton.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.3f) // 30% width

                appointmentContainer.addView(appointmentText)
                appointmentContainer.addView(selectButton)

                currentTime = nextTime

                appointmentList.addView(appointmentContainer)
            }
        }
    }

    private fun queryAvailabilityWindows(date: LocalDate) {
        val db = FirebaseFirestore.getInstance()
        db
            .collection("users")
            .document(vetId)
            .collection("availability")
            .whereEqualTo("date", date.toString())
            .get()
            .addOnSuccessListener { result ->
                displayAvailabilityWindows(result)
            }
            .addOnFailureListener { exception ->

            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_make_appointment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        vetId = intent.getStringExtra("vetId")!!

        calendarView = findViewById(R.id.calendarView)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            queryAvailabilityWindows(LocalDate.of(year, month + 1, dayOfMonth))
        }

        appointmentList = findViewById(R.id.appointmentList)

        queryAvailabilityWindows(LocalDate.now())

//        db.collection("users")
//            .document(vetId)
//            .collection("availability")
//            .get()
//            .addOnSuccessListener { result ->
//                val dates = result.documents
//                    .mapNotNull { it.getString("date") }
//                    .distinct() // Get unique dates
//                processDates(dates)
//            }
    }
}