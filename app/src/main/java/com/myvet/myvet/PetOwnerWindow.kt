package com.myvet.myvet

import android.content.Intent
import android.os.Bundle
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
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import java.time.LocalTime

class PetOwnerWindow : AppCompatActivity() {

    private lateinit var updateDetails: Button
    private lateinit var logOut: Button
    private lateinit var deleteAccount: Button
    private lateinit var findVet: Button
    private lateinit var appointmentsList: LinearLayout

    private fun showAppointments(appointments: MutableList<Pair<DocumentSnapshot, String>>) {
        appointmentsList.removeAllViews()

        val db = FirebaseFirestore.getInstance()

        for (pair in appointments) {
            val appointmentContainer = LinearLayout(this)
            appointmentContainer.orientation = LinearLayout.HORIZONTAL

            val date = pair.first.getString("date")
            val time = pair.first.getString("time")
            val vet = pair.second

            val appointmentText = TextView(this)
            appointmentText.text =
                "Dr. $vet\n$date $time - ${LocalTime.parse(time).plusMinutes(15)}"

            val deleteButton = Button(this)
            deleteButton.text = "Delete"
            deleteButton.setOnClickListener {
                db.collection("appointments").document(pair.first.id).delete().addOnSuccessListener {
                    Toast.makeText(this, "Appointment deleted successfully", Toast.LENGTH_SHORT).show()
                }
            }

            appointmentText.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.7f) // 70% width
            deleteButton.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.3f) // 30% width

            appointmentContainer.addView(appointmentText)
            appointmentContainer.addView(deleteButton)

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
        textView.text = "Welcome ${user?.displayName}"

        updateDetails = findViewById(R.id.UpdateDetails)
        updateDetails.setOnClickListener {
            val intent = Intent(this, UpdatePetDetails::class.java)
            startActivity(intent)
        }

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
        db
            .collection("appointments")
            .whereEqualTo("user", user.uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firestore", "Error fetching appointments", error)
                    return@addSnapshotListener
                }

                val appointments = mutableListOf<Pair<DocumentSnapshot, String>>() // Pair of appointment and vet name
                val vetIds = snapshot?.documents?.map { it.getString("vet") ?: "" }?.distinct() ?: emptyList()

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

//                showAppointments(snapshot!!)
            }
    }
}