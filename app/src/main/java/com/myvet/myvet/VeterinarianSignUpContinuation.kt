package com.myvet.myvet

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.URLEncoder


class VeterinarianSignUpContinuation : AppCompatActivity() {
    private lateinit var expertise: EditText
    private lateinit var aboutMe: EditText

    private lateinit var register: Button
    private lateinit var errorMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_veterinarian_sign_up_continuation)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@VeterinarianSignUpContinuation, SignUp::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        })

        expertise = findViewById(R.id.expertise)
        aboutMe = findViewById(R.id.aboutMe)
        register = findViewById(R.id.nextButton)
        errorMessage = findViewById(R.id.errorMessage)


        register.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser!!


            val userData = hashMapOf(
                "expertise" to expertise.text.toString(),
                "aboutMe" to aboutMe.text.toString(),
            )

            db.collection("users")
                .document(user.uid)
                .set(userData, SetOptions.merge())
                .addOnSuccessListener {
                    Log.i(
                        "Sign up vet",
                        "Vet registered successfully"
                    )

                    val intent = Intent(
                        this@VeterinarianSignUpContinuation,
                        VetWindow::class.java
                    )
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    Log.i(
                        "Sign up vet",
                        "Vet registration failed"
                    )
                }
        }
    }
}