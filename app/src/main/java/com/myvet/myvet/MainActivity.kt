package com.myvet.myvet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import java.util.Arrays


class MainActivity : AppCompatActivity() {
    private lateinit var loginBtn: Button
    private lateinit var signUpBtn: Button
    private lateinit var errorMessage: TextView

    private var signInLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        loginBtn = findViewById(R.id.Login_button)
        signUpBtn = findViewById(R.id.sign_up_button)
        errorMessage = findViewById(R.id.error_message)

        signInLauncher = registerForActivityResult(
            FirebaseAuthUIActivityResultContract()
        ) { result: FirebaseAuthUIAuthenticationResult ->
            // Handle the FirebaseAuthUIAuthenticationResult
            handleSignInResult(result)
        }

        //when the user clicks on the sign-up button
        signUpBtn.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            finish()
        }

        loginBtn.setOnClickListener {
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(
                    Arrays.asList(
                        EmailBuilder().build(),
//                        GoogleBuilder().build()
                    )
                )
                .build()
            signInLauncher!!.launch(signInIntent)
        }
    }

    private fun handleSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            // Signed in successfully
            val user = FirebaseAuth.getInstance().currentUser
        } else {
            // Sign-in failed
        }
    }
}