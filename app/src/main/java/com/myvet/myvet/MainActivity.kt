package com.myvet.myvet

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {
    private lateinit var loginBtn: Button
    private lateinit var errorMessage: TextView

    private var signInLauncher: ActivityResultLauncher<Intent>? = null

    private fun goHome(auth: FirebaseAuth) {
        val db = FirebaseFirestore.getInstance()

        val uid = auth.currentUser!!.uid
        val userRef = db.collection("users").document(uid)

        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val type = document.getString("type")
                Log.d("Firestore", "User found: Type = $type")

                val intent = when (type) {
                    "owner" -> Intent(this, PetOwnerWindow::class.java)
                    "vet" -> Intent(this, VetWindow::class.java)
                    else -> throw IllegalArgumentException("Unknown type: $type")
                }

                startActivity(intent)
                finish()
            } else {
                Log.d("Firestore", "No such user with UID $uid")
            }
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting user document: $uid", exception)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            goHome(auth)
        }

        loginBtn = findViewById(R.id.Login_button)
        errorMessage = findViewById(R.id.error_message)

        signInLauncher = registerForActivityResult(
            FirebaseAuthUIActivityResultContract()
        ) { result: FirebaseAuthUIAuthenticationResult ->
            // Handle the FirebaseAuthUIAuthenticationResult
            handleSignInResult(result)
        }

        loginBtn.setOnClickListener {
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(
                    listOf(
                        EmailBuilder()
                            .setRequireName(true)
                            .build(),
                        GoogleBuilder().build()
                    )
                )
                .setTheme(R.style.Theme_LogginApp)
                .build()
            signInLauncher!!.launch(signInIntent)
        }
    }

    private fun handleSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            // Signed in successfully
            val auth = FirebaseAuth.getInstance()

            errorMessage.visibility = TextView.GONE
            Log.i("Login", "Login successful - Username: ${auth.currentUser?.displayName}")

            if (result.idpResponse!!.isNewUser) {
                val intent = Intent(this, SignUp::class.java)
                startActivity(intent)
                finish()
            } else {
                goHome(auth)
            }
        } else {
            errorMessage.text = "Sign in failed"
            Log.i("Login", "Sign in failed")
            errorMessage.visibility = TextView.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                errorMessage.visibility = TextView.GONE
            }, 3000)
        }
    }
}