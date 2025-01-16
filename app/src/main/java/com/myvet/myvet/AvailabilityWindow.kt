package com.myvet.myvet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalTime

private const val ARG_WINDOW_ID = "appointmentId"
private const val ARG_DATE = "date"
private const val ARG_START_TIME = "startTime"
private const val ARG_END_TIME = "endTime"

class AvailabilityWindow : Fragment() {
    private lateinit var windowId: String
    private lateinit var date: LocalDate
    private lateinit var startTime: LocalTime
    private lateinit var endTime: LocalTime

    private lateinit var availabilityText: TextView
    private lateinit var deleteButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            windowId = it.getString(ARG_WINDOW_ID)!!
            date = LocalDate.parse(it.getString(ARG_DATE))
            startTime = LocalTime.ofSecondOfDay(it.getLong(ARG_START_TIME))
            endTime = LocalTime.ofSecondOfDay(it.getLong(ARG_END_TIME))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (arguments == null) {
            return null
        }

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser!!

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_availablity_window, container, false)

        availabilityText = view.findViewById(R.id.windowText)
        availabilityText.text = "Date: $date\n$startTime - $endTime"

        deleteButton = view.findViewById(R.id.deleteButton)
        deleteButton.setOnClickListener {
            db.collection("users")
                .document(user.uid)
                .collection("availability")
                .document(windowId)
                .delete()
                .addOnSuccessListener {

                    db.collection("appointments")
                        .whereEqualTo("vet", user.uid)
                        .whereEqualTo("date", date)
                        .whereGreaterThanOrEqualTo("time", startTime)
                        .whereLessThan("time", endTime)
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
                        container!!.context,
                        "Availability window deleted successfully",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(windowId: String, date: String, startTime: Long, endTime: Long) =
            AvailabilityWindow().apply {
                arguments = Bundle().apply {
                    putString(ARG_WINDOW_ID, windowId)
                    putString(ARG_DATE, date)
                    putLong(ARG_START_TIME, startTime)
                    putLong(ARG_END_TIME, endTime)
                }
            }
    }
}