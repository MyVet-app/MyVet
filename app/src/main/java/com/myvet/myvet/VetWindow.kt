package com.myvet.myvet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentContainerView
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot

class VetWindow : AppCompatActivity() {
    private lateinit var appointmentsListener: ListenerRegistration
    private lateinit var availabilityWindowsListener: ListenerRegistration

    private lateinit var clinicNameTitle: TextView
    private lateinit var clinicAddressTitle: TextView

    private lateinit var logOut: Button
    private lateinit var deleteAccount: Button

    private lateinit var addAvailability: Button
    private lateinit var availabilityWindowsList: FragmentContainerView
    private lateinit var appointmentsList: FragmentContainerView

    private fun showAppointments(appointments: MutableList<Pair<DocumentSnapshot, String>>) {
        val transaction = supportFragmentManager.beginTransaction()

        for (pair in appointments) {
            val appointmentFragment = Appointment.newInstance(
                pair.first.id,
                pair.first.getString("date")!!,
                pair.first.getLong("time")!!,
                pair.second
            )
            transaction.add(appointmentsList.id, appointmentFragment)
        }

        transaction.commit()
    }

    private fun showAvailabilityWindows(snapshot: QuerySnapshot) {
        val transaction = supportFragmentManager.beginTransaction()

        for (window in snapshot) {
            val windowFragment = AvailabilityWindow.newInstance(
                window.id,
                window.getString("date")!!,
                window.getLong("startTime")!!,
                window.getLong("endTime")!!,
            )
            transaction.add(availabilityWindowsList.id, windowFragment)
        }

        transaction.commit()
    }

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
        textView.text = "Welcome ${user.displayName}"

        addAvailability = findViewById(R.id.AddAvailability)
        addAvailability.setOnClickListener {
            val intent = Intent(this, AddAvailability::class.java)
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
                    showAvailabilityWindows(it)
                }
            }

        appointmentsListener = db.collection("appointments")
            .whereEqualTo("vet", user.uid)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("MyActivity", "Error getting data", exception)
                    return@addSnapshotListener
                }

                snapshot?.let {
                    val appointments = mutableListOf<Pair<DocumentSnapshot, String>>() // Pair of appointment and vet name
                    val ownerIds = snapshot.documents.map { it.getString("user") ?: "" }.distinct()

                    if (ownerIds.isEmpty()) {
                        appointmentsList.removeAllViews()
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

                            showAppointments(appointments)
                        }
                }
            }
    }
}