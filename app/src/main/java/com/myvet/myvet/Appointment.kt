package com.myvet.myvet

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

private const val ARG_APPOINTMENT_ID = "appointmentId"
private const val ARG_DATE = "date"
private const val ARG_TIME = "time"
private const val ARG_VET_NAME = "vetName"

class Appointment : Fragment() {
    private lateinit var appointmentId: String
    private lateinit var date: LocalDate
    private lateinit var time: LocalTime
    private lateinit var memberName: String

    private lateinit var appointmentText: TextView
    private lateinit var deleteButton: Button
    private lateinit var addToCalendarButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            appointmentId = it.getString(ARG_APPOINTMENT_ID)!!
            date = LocalDate.parse(it.getString(ARG_DATE))
            time = LocalTime.ofSecondOfDay(it.getLong(ARG_TIME))
            memberName = it.getString(ARG_VET_NAME)!!
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

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_appointment, container, false)

        appointmentText = view.findViewById(R.id.appointmentText)
        appointmentText.text = "$memberName\n$date\n$time - ${time.plusMinutes(15)}"

        deleteButton = view.findViewById(R.id.deleteButton)
        deleteButton.setOnClickListener {
            db.collection("appointments").document(appointmentId).delete().addOnSuccessListener {
                Toast.makeText(container!!.context, "Appointment deleted successfully", Toast.LENGTH_SHORT).show()
            }
        }

        addToCalendarButton = view.findViewById(R.id.addToCalendarButton)
        addToCalendarButton.setOnClickListener {
            val beginTime: Calendar = Calendar.getInstance()
            beginTime.set(date.year, date.monthValue, date.dayOfMonth, time.hour, time.minute)

            val endTime: Calendar = Calendar.getInstance()
            endTime.set(date.year, date.monthValue, date.dayOfMonth, time.plusMinutes(15).hour, time.plusMinutes(15).minute)
            val intent: Intent = Intent(Intent.ACTION_INSERT)
                .setData(Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.timeInMillis)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.timeInMillis)
                .putExtra(Events.TITLE, "Appointment with vet Dr. $memberName")
//                    .putExtra(Events.DESCRIPTION, "Group class")
                .putExtra(Events.EVENT_LOCATION, "Virtual Meeting")
                .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
//                    .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com")
            startActivity(intent)
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(appointmentId: String, date: String, time: Long, vetName: String) =
            Appointment().apply {
                arguments = Bundle().apply {
                    putString(ARG_APPOINTMENT_ID, appointmentId)
                    putString(ARG_DATE, date)
                    putLong(ARG_TIME, time)
                    putString(ARG_VET_NAME, vetName)
                }
            }
    }
}