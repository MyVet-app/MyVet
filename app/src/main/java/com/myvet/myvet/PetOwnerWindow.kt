package com.myvet.myvet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PetOwnerWindow : AppCompatActivity() {

    private lateinit var updateDetails: Button
    private lateinit var logOut: Button
    private lateinit var deleteAccount: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pet_owner_window)

        val user = FirebaseAuth.getInstance().currentUser

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
                .addOnFailureListener{ e ->
                    Log.i("Delete account", e.toString())
                }
        }
    }
}