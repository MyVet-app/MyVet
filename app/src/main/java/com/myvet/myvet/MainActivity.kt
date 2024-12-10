package com.myvet.myvet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.logginapp.R
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var loginBtn: Button
    private lateinit var signUpBtn: Button
    private lateinit var errorMessage: TextView

    private lateinit var verifyButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        usernameInput = findViewById(R.id.Username_input)
        passwordInput = findViewById(R.id.Password_input)
        loginBtn = findViewById(R.id.Login_button)
        signUpBtn = findViewById(R.id.sign_up_button)
        emailInput = findViewById(R.id.email_input)
        errorMessage = findViewById(R.id.error_message)

        signUpBtn.isEnabled = false
        // when the user clicks on the sign up button, it will open the sign up page
        val db = FirebaseFirestore.getInstance()

        // when the user clicks on the sign-up button
        signUpBtn.setOnClickListener {
            val email = emailInput.text.toString()
            val username = usernameInput.text.toString()

            val password = passwordInput.text.toString()

            if (email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) {
                val user = hashMapOf(
                    "username" to username,
                    "password" to password
                )
                db.collection("pet owner").document(email)
                    .set(user)
                    .addOnSuccessListener {
                        Log.i("Sign up try", "User added successfully!")
                        val intent = Intent(this, SignUp::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        Log.i("Sign up try", "Error adding user: ${e.message}")
                    }
            } else {
                Log.i("Sign up try", "Please fill in all fields.")
            }
        }

        // boolean to check if the login button should be enabled
        loginBtn.isEnabled = false

        // when the user types in the username or password, the login button will be able to be clicked
        fun checkInputs() {
            //val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            val email = emailInput.text.toString()
            //loginBtn.isEnabled = password.isNotEmpty() && email.isNotEmpty() && username.isNotEmpty()
            signUpBtn.isEnabled = email.isNotEmpty() && password.isNotEmpty()

        }

        // check if the user has typed in the username or password
        //usernameInput.addTextChangedListener {checkInputs()}
        passwordInput.addTextChangedListener { checkInputs() }
        emailInput.addTextChangedListener { checkInputs() }

        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            // check if the password is at least 6 characters long and contains at least one letter and one number
            if (!password.matches(Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$"))) {
                errorMessage.visibility = TextView.VISIBLE
                passwordInput.text.clear()
            } else {
                errorMessage.visibility = TextView.GONE
                Log.i("Test login", "Username: $username, Password: $password")

                val intent = Intent(this, SecondPage::class.java)
                intent.putExtra("USERNAME", username)
                startActivity(intent)
                finish()
            }
        }
    }
}