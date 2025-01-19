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
import com.google.firebase.firestore.ListenerRegistration
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar


class PetOwnerWindow : AppCompatActivity() {

    private lateinit var appointmentsListener: ListenerRegistration

    private lateinit var updateDetails: Button
    private lateinit var logOut: Button
    private lateinit var deleteAccount: Button
    private lateinit var findVet: Button
    private lateinit var appointmentsList: LinearLayout
    private lateinit var petDetails: LinearLayout
    private lateinit var petName: TextView

    private fun showAppointments(appointments: MutableList<Pair<DocumentSnapshot, String>>) {
        appointmentsList.removeAllViews()

        val db = FirebaseFirestore.getInstance()

        for (pair in appointments) {
            val appointmentContainer = LinearLayout(this)
            appointmentContainer.orientation = LinearLayout.HORIZONTAL

            val date = LocalDate.parse(pair.first.getString("date"))
            val time = LocalTime.ofSecondOfDay(pair.first.getLong("time")!!)
            val vet = pair.second

            val appointmentText = TextView(this)
            appointmentText.text =
                "Dr. $vet\n$date $time - ${time.plusMinutes(15)}"

            val deleteButton = Button(this)
            deleteButton.text = "Delete"
            deleteButton.setOnClickListener {
                db.collection("appointments").document(pair.first.id).delete().addOnSuccessListener {
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
                    .putExtra(Events.TITLE, "Appointment with vet Dr. $vet")
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pet_owner_window)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val user = FirebaseAuth.getInstance().currentUser!!
        val db = FirebaseFirestore.getInstance()

        val textView: TextView = findViewById(R.id.HelloText)
        textView.text = "Welcome ${user.displayName}"

        updateDetails = findViewById(R.id.UpdateDetails)
        updateDetails.setOnClickListener {
            val intent = Intent(this, UpdatePetDetails::class.java)
            startActivity(intent)
        }

        logOut = findViewById(R.id.LogOut)
        logOut.setOnClickListener {
            appointmentsListener.remove()

            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener { // user is now signed out
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
        }

        deleteAccount = findViewById(R.id.DeleteAccount)
        deleteAccount.setOnClickListener {
            val uid = user.uid

            AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
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

        findVet = findViewById(R.id.FindVet)
        findVet.setOnClickListener {
            val intent = Intent(this, FindVet::class.java)
            startActivity(intent)
        }

        appointmentsList = findViewById(R.id.appointments)
        appointmentsListener = db
            .collection("appointments")
            .whereEqualTo("user", user.uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("PetOwnerActivity", "Error fetching appointments", error)
                    return@addSnapshotListener
                }

                val appointments = mutableListOf<Pair<DocumentSnapshot, String>>() // Pair of appointment and vet name
                val vetIds = snapshot?.documents?.map { it.getString("vet") ?: "" }?.distinct() ?: emptyList()

                if (vetIds.isEmpty()) {
                    appointmentsList.removeAllViews()
                    return@addSnapshotListener
                }

                db.collection("users")
                    .whereIn(FieldPath.documentId(), vetIds)
                    .get()
                    .addOnSuccessListener { userSnapshots ->
                        val vetNames = userSnapshots.documents.associateBy({ it.id }, { it.getString("name") ?: "Unknown" })

                        snapshot?.documents?.forEach { appointment ->
                            val vetId = appointment.getString("vet")
                            val vetName = vetNames[vetId] ?: "Unknown"
                            appointments.add(Pair(appointment, vetName))
                        }

                        showAppointments(appointments)
                    }
            }
        petDetails = findViewById(R.id.petDetails)
        petName = findViewById(R.id.petName)

        db.collection("users").document(user.uid).collection("petDetails").document("Pet")
            .addSnapshotListener { document, error ->
                if (error != null) {
                    petName.text = "Error loading pet details"
                    return@addSnapshotListener
                }
                if (document != null && document.exists()) {
                    petDetails.removeAllViews()

                    val name = document.getString("petName") ?: "N/A"
                    petName.text = "$name's Details"
                    val type = document.getString("petType") ?: "N/A"
                    val age = document.getString("petAge") ?: "N/A"
                    val weight = document.getString("petWeight") ?: "N/A"
                    val gender = document.getString("petGender") ?: "N/A"
                    val medicalHistory = document.getString("medicalHistory") ?: "N/A"

                    // Function to add a TextView to the LinearLayout
                    fun addTextView(label: String, value: String) {
                        val textView = TextView(this)
                        textView.text = "$label: $value"
                        textView.textSize = 16f
                        textView.setPadding(10, 10, 10, 10)
                        petDetails.addView(textView)
                    }

                    // Adding the pet details to the LinearLayout
                    addTextView("Pet Name", name)
                    addTextView("Pet Type", type)
                    addTextView("Pet Age", age)
                    addTextView("Pet Weight", weight)
                    addTextView("Pet Gender", gender)
                    addTextView("Medical History","\n$medicalHistory")
                } else {
                    petName.text = "No pet details found"
                }
            }
    }
}