package com.myvet.myvet

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class VetWindow : AppCompatActivity() {
    private lateinit var appointmentsListener: ListenerRegistration
    private lateinit var availabilityWindowsListener: ListenerRegistration

    private lateinit var clinicNameTitle: TextView
    private lateinit var clinicAddressTitle: TextView

    private lateinit var logOut: Button
    private lateinit var deleteAccount: Button

    private lateinit var addAvailability: Button
    private lateinit var availabilityWindowsList: LinearLayout
    private lateinit var appointmentsList: LinearLayout

    private lateinit var updateVetDetails: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vet_window)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser!!

        clinicNameTitle = findViewById(R.id.clinicNameTitle)
        clinicAddressTitle = findViewById(R.id.clinicAddressTitle)

        db.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { result ->
                clinicNameTitle.text = result.getString("clinicName")
                clinicAddressTitle.text = result.getString("clinicAddress")
            }

        logOut = findViewById(R.id.LogOut)
        logOut.setOnClickListener {
            availabilityWindowsListener.remove()
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
            availabilityWindowsListener.remove()
            appointmentsListener.remove()

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

        val textView: TextView = findViewById(R.id.HelloText)
        textView.text = getString(R.string.welcome_vet_or_pet) + " ${user.displayName}"


        addAvailability = findViewById(R.id.AddAvailability)
        addAvailability.setOnClickListener {
            val intent = Intent(this, AddAvailability::class.java)
            startActivity(intent)
        }

        updateVetDetails = findViewById(R.id.UpdateVetDetails)
        updateVetDetails.setOnClickListener {
            val intent = Intent(this, UpdateVetDetails::class.java)
            startActivity(intent)
        }

        availabilityWindowsList = findViewById(R.id.availabilityWindowsList)
        appointmentsList = findViewById(R.id.appointmentsList)

        availabilityWindowsListener = db.collection("users")
            .document(user.uid)
            .collection("availability")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("VetWindow", "Error getting availability windows", exception)
                    return@addSnapshotListener
                }

                // Process data and update the view
                snapshot?.let {
                    updateAvailabilityWindows(it)
                }
            }

        appointmentsListener = db.collection("appointments")
            .whereEqualTo("vet", user.uid)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("MyActivity", "Error getting data", exception)
                    return@addSnapshotListener
                }

                for (change in snapshot!!.documentChanges.filter { it.type.name == "REMOVED" }) {
                    val title = getString(R.string.appointment_cancelled)

                    db.collection("users")
                        .document(change.document.getString("user")!!)
                        .get()
                        .addOnSuccessListener { userRef ->
                            val body = getString(
                                R.string.appointment_text, userRef.getString("name")!!, getString(
                                    R.string.time_range,
                                    LocalTime.ofSecondOfDay(change.document.getLong("time")!!),
                                    LocalDate.parse(change.document.getString("date"))
                                )
                            )

                            val CHANNEL_ID = "MyVetChannel"
                            val channel = NotificationChannel(
                                CHANNEL_ID,
                                "MyNotification",
                                NotificationManager.IMPORTANCE_HIGH,
                            )

                            getSystemService(NotificationManager::class.java).createNotificationChannel(
                                channel
                            )
                            val notification = Notification.Builder(this, CHANNEL_ID)
                                .setContentTitle(title)
                                .setContentText(body)
                                .setSmallIcon(R.drawable.icon_logo)
                                .setAutoCancel(true)

                            if (ActivityCompat.checkSelfPermission(
                                    this@VetWindow,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                NotificationManagerCompat.from(this).notify(1, notification.build())
                            }
                        }

//                    val builder = NotificationCompat.Builder(this, "MYVET_CHANNEL_ID")
//                        .setSmallIcon(R.drawable.icon_logo)
//                        .setContentTitle("Appointment Cancelled")
//                        .setContentText(
//                            "Client ... cancelled an appointment at " +
//                                    "${LocalTime.ofSecondOfDay(change.document.getLong("time")!!)} on " +
//                                    "${LocalDate.parse(change.document.getString("date"))}"
//                        )
//                        .setPriority(NotificationCompat.PRIORITY_HIGH)
//
//                    with(NotificationManagerCompat.from(this)) {
//                        if (ActivityCompat.checkSelfPermission(
//                                this@VetWindow,
//                                Manifest.permission.POST_NOTIFICATIONS
//                            ) != PackageManager.PERMISSION_GRANTED
//                        ) {
//                            // TODO: Consider calling
//                            // ActivityCompat#requestPermissions
//                            // here to request the missing permissions, and then overriding
//                            // public fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
//                            //                                        grantResults: IntArray)
//                            // to handle the case where the user grants the permission. See the documentation
//                            // for ActivityCompat#requestPermissions for more details.
//
//                            return@with
//                        }
//                        // notificationId is a unique int for each notification that you must define.
//                        notify(1, builder.build())
//                    }

                }

                snapshot.let {
                    val appointments =
                        mutableListOf<Pair<DocumentSnapshot, String>>() // Pair of appointment and vet name
                    val ownerIds = snapshot.documents.map { it.getString("user") ?: "" }.distinct()

                    if (ownerIds.isEmpty()) {
                        appointmentsList.removeAllViews()
                        return@addSnapshotListener
                    }

                    db.collection("users")
                        .whereIn(FieldPath.documentId(), ownerIds)
                        .get()
                        .addOnSuccessListener { userSnapshots ->
                            val ownerNames = userSnapshots.documents.associateBy({ it.id },
                                { it.getString("name") ?: "Unknown" })

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
        title.text = getString(R.string.appointments)

        appointmentsList.addView(title)

        val db = FirebaseFirestore.getInstance()

        for (pair in snapshot) {
            val appointmentContainer = LinearLayout(this)
            appointmentContainer.orientation = LinearLayout.HORIZONTAL

            val date = LocalDate.parse(pair.first.getString("date"))
            val time = LocalTime.ofSecondOfDay(pair.first.getLong("time")!!)
            val owner = pair.second

            val appointmentText = TextView(this)
            val formatter = DateTimeFormatter.ofPattern("HH:mm") // לדוגמה: 14:30
            val timeFormatted = time.format(formatter)
            val timeEndFormatted = time.plusMinutes(15).format(formatter)

            val timeRange = getString(R.string.time_range, timeFormatted, timeEndFormatted)
            appointmentText.text = getString(R.string.appointment_text, owner, timeRange)

            val deleteButton = Button(this)
            deleteButton.text = getString(R.string.delete_button)
            deleteButton.setOnClickListener {
                db.collection("appointments").document(pair.first.id).delete()
                    .addOnSuccessListener {
                        Log.i("Appointment Deletion", "Appointment deleted successfully")
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(
                                this,
                                getString(R.string.appointment_deleted_successfully),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                        val messageData = hashMapOf(
                            "title" to getString(R.string.appointment_cancelled),
                            "body" to getString(R.string.cancelled_appointment_on_at, date, time),
                            "recipient" to owner,
                        )
                        db.collection("messages")
                            .document()
                            .set(messageData)
                            .addOnSuccessListener {
                                Log.i(
                                    "Appointment deletion",
                                    "Sent push notification"
                                )
                            }
                    }
            }

            val calendarButton = Button(this)
            calendarButton.text = getString(R.string.add_to_calendar)
            calendarButton.setOnClickListener {
                val beginTime: Calendar = Calendar.getInstance()
                beginTime.set(
                    date.year,
                    date.monthValue,
                    date.dayOfMonth,
                    time.hour,
                    time.minute
                )

                val endTime: Calendar = Calendar.getInstance()
                endTime.set(
                    date.year,
                    date.monthValue,
                    date.dayOfMonth,
                    time.plusMinutes(15).hour,
                    time.plusMinutes(15).minute
                )
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

            appointmentText.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.3f
            ) // 70% width
            deleteButton.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.3f
            ) // 30% width
            calendarButton.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.4f
            ) // 30% width

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
        title.text = getString(R.string.avilable_windows)

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

//            val availabilityText = TextView(this)
//            availabilityText.text =
//                "${getString(R.string.date)}$date\n$startTime - $endTime"
            val availabilityText = TextView(this)

            val formatter = DateTimeFormatter.ofPattern("HH:mm") // תבנית שעה:דקה
            val startFormatted = startTime.format(formatter)
            val endFormatted = endTime.format(formatter)

// מחרוזת עם סדר מתאים לשפה
            val availability =
                getString(R.string.availability, date, startFormatted, endFormatted)
            availabilityText.text = availability


            val deleteButton = Button(this)
            deleteButton.text = getString(R.string.delete_button)
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
                            getString(R.string.availability_window_deleted_successfully),
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