package com.myvet.myvet

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar

class AddAvailability : AppCompatActivity() {

    private lateinit var calendar: CalendarView
    private lateinit var startTimeButton: Button
    private lateinit var startTimeText: TextView
    private lateinit var endTimeButton: Button
    private lateinit var endTimeText: TextView
    private lateinit var save: Button

    private var startTime: LocalTime? = null
    private var endTime: LocalTime? = null

    private fun selectTime(isStartTime: Boolean) {
        // Get current time
        val calendar = Calendar.getInstance()
        var hour = calendar.get(Calendar.HOUR_OF_DAY)
        var minute = calendar.get(Calendar.MINUTE)

        if (isStartTime) {
            startTime?.let {
                hour = it.hour
                minute = it.minute
            }
        } else {
            endTime?.let {
                hour = it.hour
                minute = it.minute
            } ?: {
                hour = startTime!!.hour
                minute = startTime!!.minute
            }
        }

        // Initialize TimePickerDialog
        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                // Adjust minute to nearest 15 minute interval
                val adjustedMinute = (selectedMinute / 15) * 15

                val selectedCalendar = LocalTime.of(selectedHour, adjustedMinute)

                if (isStartTime) {
                    if (endTime != null && endTime!!.isBefore(selectedCalendar)) {
                        Toast.makeText(
                            this,
                            "End time must be after start time",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        startTime = selectedCalendar
                        startTimeText.text =
                            String.format("Start time: %02d:%02d", selectedHour, adjustedMinute)

                        endTimeButton.isEnabled = true
                    }
                } else {
                    startTime?.let {
                        if (selectedCalendar.isBefore(it)) {
                            Toast.makeText(
                                this,
                                "End time must be after start time",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else {
                            endTime = selectedCalendar
                            endTimeText.text =
                                String.format("End time: %02d:%02d", selectedHour, adjustedMinute)

                            save.isEnabled = true
                        }
                    } ?: Toast.makeText(this, "Select start time first", Toast.LENGTH_SHORT).show()
                }
            },
            hour, minute, true // true for 24-hour time format
        )

        // Show the dialog
        timePickerDialog.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_availability)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        calendar = findViewById(R.id.calendar)
        startTimeButton = findViewById(R.id.startTimeButton)
        startTimeText = findViewById(R.id.startTimeText)
        endTimeButton = findViewById(R.id.endTimeButton)
        endTimeText = findViewById(R.id.endTimeText)
        save = findViewById(R.id.save)

        startTimeButton.setOnClickListener {
            selectTime(true)
        }

        endTimeButton.setOnClickListener {
            selectTime(false)
        }

        save.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser

            val availabilityData = hashMapOf(
                "date" to Instant.ofEpochMilli(calendar.date).atZone(ZoneId.systemDefault()).toLocalDate().toString(),
                "startTime" to startTime.toString(),
                "endTime" to endTime.toString(),
            )
            db.collection("users")
                .document(user!!.uid)  // Use the uid as the document ID
                .collection("availability")
                .add(availabilityData)
                .addOnSuccessListener {
                    Log.i(
                        "Availability creation",
                        "Availability window created successfully"
                    )

                    Toast.makeText(this, "Availability window created successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Log.i(
                        "Availability creation",
                        "Availability window creation failed"
                    )

                    Toast.makeText(this, "Availability window creation failed", Toast.LENGTH_SHORT).show()
                }
        }
    }
}