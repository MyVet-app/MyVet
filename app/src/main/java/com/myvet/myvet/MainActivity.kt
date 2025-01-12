package com.myvet.myvet

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.AuthUI.IdpConfig.FacebookBuilder
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {
    private var signInLauncher: ActivityResultLauncher<Intent>? = null

    private fun authFlow() {
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setLogo(R.drawable.icon_logo)
            .setTosAndPrivacyPolicyUrls(
                "https://www.freeprivacypolicy.com/live/67168b52-bccb-4544-b878-711f6943de60",
                "https://www.freeprivacypolicy.com/live/67168b52-bccb-4544-b878-711f6943de60"
            )
            .setAvailableProviders(
                listOf(
                    EmailBuilder()
                        .setRequireName(true)
                        .build(),
                    GoogleBuilder().build(),
                    FacebookBuilder().build(),
                )
            )
            .setTheme(R.style.Theme_LogginApp)
            .build()
        signInLauncher!!.launch(signInIntent)
    }

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
                val intent = Intent(this, SignUp::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting user document: $uid", exception)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        signInLauncher = registerForActivityResult(
            FirebaseAuthUIActivityResultContract()
        ) { result: FirebaseAuthUIAuthenticationResult ->
            // Handle the FirebaseAuthUIAuthenticationResult
            handleSignInResult(result)
        }

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            goHome(auth)
        } else {
            authFlow()
        }
    }

    private fun handleSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            // Signed in successfully
            val auth = FirebaseAuth.getInstance()

            Log.i("Login", "Login successful - Username: ${auth.currentUser?.displayName}")

            if (result.idpResponse!!.isNewUser) {
                val intent = Intent(this, SignUp::class.java)
                startActivity(intent)
                finish()
            } else {
                goHome(auth)
            }
        } else {
            val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)

            // set title
            alertDialogBuilder.setTitle("Sign in failed")

            // set dialog message
            alertDialogBuilder
                .setMessage(result.idpResponse!!.error.toString())
                .setCancelable(false)
                .setNegativeButton(
                    "Ok"
                ) { dialog, _ ->
                    dialog.cancel()
                }

            // create alert dialog
            val alertDialog: AlertDialog = alertDialogBuilder.create()

            // show it
            alertDialog.show()
        }
    }
}