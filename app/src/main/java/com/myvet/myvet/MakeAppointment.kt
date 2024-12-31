package com.myvet.myvet

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class MakeAppointment : AppCompatActivity() {
    private lateinit var vetId: String
    private lateinit var vetTitle: TextView
    private lateinit var calendarView: CalendarView
    private lateinit var appointmentList: LinearLayout

    private fun makeAppointment(date: LocalDate, time: LocalTime) {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        val appointmentData = hashMapOf(
            "user" to auth.currentUser!!.uid,
            "vet" to vetId,
            "date" to date.toString(),
            "time" to time.toString(),
            "creationTime" to LocalDateTime.now().toString(),
        )

        db.collection("appointments")
            .document()
            .set(appointmentData)
            .addOnSuccessListener {
                Log.i(
                    "Appointment creation",
                    "Appointment created successfully"
                )
                Toast.makeText(this, "Appointment made successfully", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayAvailabilityWindows(date: LocalDate, windows: QuerySnapshot) {
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

                val appointmentTime = currentTime

                val selectButton = Button(this)
                selectButton.text = "Select"
                selectButton.setOnClickListener {
                    makeAppointment(date, appointmentTime)
                    finish()
                }

                appointmentText.layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.7f
                ) // 70% width
                selectButton.layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.3f
                ) // 30% width

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
                displayAvailabilityWindows(date, result)
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

        val db = FirebaseFirestore.getInstance()
        db
            .collection("users")
            .document(vetId)
            .get()
            .addOnSuccessListener { document ->
                vetTitle = findViewById(R.id.vetTitle)
                vetTitle.text = document.getString("name")
            }

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