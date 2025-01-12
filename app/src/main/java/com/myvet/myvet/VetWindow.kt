package com.myvet.myvet

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

class VetWindow : AppCompatActivity() {
    private lateinit var logOut: Button
    private lateinit var deleteAccount: Button

    private lateinit var addAvailability: Button
    private lateinit var availabilityWindowsList: LinearLayout
    private lateinit var appointmentsList: LinearLayout

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

        logOut = findViewById(R.id.LogOut)
        logOut.setOnClickListener {
            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener { // user is now signed out
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
        }

        deleteAccount = findViewById(R.id.DeleteAccount)
        deleteAccount.setOnClickListener {
            val uid = user!!.uid

            AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val db = FirebaseFirestore.getInstance()

                        db.collection("users").document(uid).delete()

                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        // Deletion failed
                    }
                }
                .addOnFailureListener { e ->
                    Log.i("Delete account", e.toString())
                }
        }

        val textView: TextView = findViewById(R.id.HelloText)
        textView.text = "Welcome ${user?.displayName}"

        addAvailability = findViewById(R.id.AddAvailability)
        addAvailability.setOnClickListener {
            val intent = Intent(this, AddAvailability::class.java)
            startActivity(intent)
        }

        availabilityWindowsList = findViewById(R.id.availabilityWindowsList)
        appointmentsList = findViewById(R.id.appointmentsList)

        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .document(user!!.uid)
            .collection("availability")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("MyActivity", "Error getting data", exception)
                    return@addSnapshotListener
                }

                // Process data and update the view
                snapshot?.let {
                    updateAvailabilityWindows(it)
                }
            }

        db.collection("appointments")
            .whereEqualTo("vet", user.uid)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("MyActivity", "Error getting data", exception)
                    return@addSnapshotListener
                }

                snapshot?.let {
                    val appointments = mutableListOf<Pair<DocumentSnapshot, String>>() // Pair of appointment and vet name
                    val ownerIds = snapshot.documents.map { it.getString("user") ?: "" }.distinct() ?: emptyList()

                    if (ownerIds.isEmpty()) {
                        return@addSnapshotListener
                    }

                    db.collection("users")
                        .whereIn(FieldPath.documentId(), ownerIds)
                        .get()
                        .addOnSuccessListener { userSnapshots ->
                            val ownerNames = userSnapshots.documents.associateBy({ it.id }, { it.getString("name") ?: "Unknown" })

                            snapshot.documents.forEach { appointment ->
                                val ownerId = appointment.getString("user")
                                val ownerName = ownerNames[ownerId] ?: "Unknown"
                                appointments.add(Pair(appointment, ownerName))
                            }

                            updateAppointments(appointments)
                        }
                }
            }
    }

    private fun updateAppointments(snapshot: MutableList<Pair<DocumentSnapshot, String>>) {
        appointmentsList.removeAllViews()

        val title = TextView(this)
        title.text = "Appointments:"

        appointmentsList.addView(title)

        val db = FirebaseFirestore.getInstance()

        for (pair in snapshot) {
            val appointmentContainer = LinearLayout(this)
            appointmentContainer.orientation = LinearLayout.HORIZONTAL

            val date = LocalDate.parse(pair.first.getString("date"))
            val time = LocalTime.ofSecondOfDay(pair.first.getLong("time")!!)
            val owner = pair.second

            val appointmentText = TextView(this)
            appointmentText.text =
                "$owner\n$date $time - ${time.plusMinutes(15)}"

            val deleteButton = Button(this)
            deleteButton.text = "Delete"
            deleteButton.setOnClickListener {
                db.collection("appointments").document(pair.first.id).delete().addOnSuccessListener {
                    Log.i("Appointment Deletion", "Appointment deleted successfully")
                    Toast.makeText(this, "Appointment deleted successfully", Toast.LENGTH_SHORT).show()
                }
            }

            val calendarButton = Button(this)
            calendarButton.text = "Add to Calendar"
            calendarButton.setOnClickListener {
                val beginTime: Calendar = Calendar.getInstance()
                beginTime.set(date.year, date.monthValue, date.dayOfMonth, time.hour, time.minute)

                val endTime: Calendar = Calendar.getInstance()
                endTime.set(date.year, date.monthValue, date.dayOfMonth, time.plusMinutes(15).hour, time.plusMinutes(15).minute)
                val intent: Intent = Intent(Intent.ACTION_INSERT)
                    .setData(Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.timeInMillis)
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.timeInMillis)
                    .putExtra(Events.TITLE, "Appointment with vet Dr. $owner")
//                    .putExtra(Events.DESCRIPTION, "Group class")
                    .putExtra(Events.EVENT_LOCATION, "Virtual Meeting")
                    .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
//                    .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com")
                startActivity(intent)
            }

            appointmentText.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.3f) // 70% width
            deleteButton.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.3f) // 30% width
            calendarButton.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f) // 30% width

            appointmentContainer.addView(appointmentText)
            appointmentContainer.addView(deleteButton)
            appointmentContainer.addView(calendarButton)

            appointmentsList.addView(appointmentContainer)
        }
    }

    private fun updateAvailabilityWindows(snapshot: QuerySnapshot) {
        // Clear the existing UI
        availabilityWindowsList.removeAllViews()

        val title = TextView(this)
        title.text = "Availability Windows:"

        availabilityWindowsList.addView(title)

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser!!

        // Build the UI elements dynamically based on the updated data
        for (window in snapshot) {
            val date = LocalDate.parse(window.getString("date"))
            val startTime = LocalTime.ofSecondOfDay(window.getLong("startTime")!!)
            val endTime = LocalTime.ofSecondOfDay(window.getLong("endTime")!!)

            val availabilityContainer = LinearLayout(this)
            availabilityContainer.orientation = LinearLayout.HORIZONTAL

            val availabilityText = TextView(this)
            availabilityText.text =
                "Date: $date\n$startTime - $endTime"

            val deleteButton = Button(this)
            deleteButton.text = "Delete"
            deleteButton.setOnClickListener {
                db.collection("users")
                    .document(user.uid)
                    .collection("availability")
                    .document(window.id)
                    .delete()
                    .addOnSuccessListener {

                        db.collection("appointments")
                            .whereEqualTo("vet", user.uid)
                            .whereEqualTo("date", window.getString("date"))
                            .whereGreaterThanOrEqualTo("time", window.getLong("startTime")!!)
                            .whereLessThan("time", window.getLong("endTime")!!)
                            .get()
                            .addOnCompleteListener { task ->
                                Log.i(
                                    "Availability deletion",
                                    "Task status: ${task.isSuccessful} ${task.exception}"
                                )
                            }
                            .addOnSuccessListener { appointments ->
                                for (appointment in appointments) {
                                    db.collection("appointments")
                                        .document(appointment.id)
                                        .delete()
                                        .addOnCompleteListener { task ->
                                            if (!task.isSuccessful) {
                                                Log.e(
                                                    "Availability deletion",
                                                    "Appointment ${appointment.id} failed to delete"
                                                )
                                            } else {
                                                Log.i(
                                                    "Availability deletion",
                                                    "Appointment ${appointment.id} deleted successfully"
                                                )
                                            }
                                        }
                                }
                            }

                        Toast.makeText(
                            this,
                            "Availability window deleted successfully",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
            }

            // Optionally, set some layout parameters
            availabilityText.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            availabilityText.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.7f
            ) // 70% width
            deleteButton.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.3f
            ) // 30% width

            availabilityContainer.addView(availabilityText)
            availabilityContainer.addView(deleteButton)

            availabilityWindowsList.addView(availabilityContainer)
        }
    }
}