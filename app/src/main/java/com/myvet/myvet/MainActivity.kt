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
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var loginBtn: Button
    private lateinit var signUpBtn: Button
    private lateinit var errorMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //usernameInput = findViewById(R.id.Username_input)
        passwordInput = findViewById(R.id.Password_input)
        loginBtn = findViewById(R.id.Login_button)
        signUpBtn = findViewById(R.id.sign_up_button)
        emailInput = findViewById(R.id.email_input)
        errorMessage = findViewById(R.id.error_message)

        signUpBtn.isEnabled = false
        // when the user clicks on the sign up button, it will open the sign up page
        val db = FirebaseFirestore.getInstance()

        // when the user clicks on the sign-up button
//        signUpBtn.setOnClickListener {
//            val email = emailInput.text.toString()
//            val username = usernameInput.text.toString()
//
//            val password = passwordInput.text.toString()
//
//            if (email.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) {
//                val user = hashMapOf(
//                    "username" to username,
//                    "password" to password
//                )
//                db.collection("pet owner").document(email)
//                    .set(user)
//                    .addOnSuccessListener {
//                        Log.i("Sign up try", "User added successfully!")
//                        val intent = Intent(this, SignUp::class.java)
//                        startActivity(intent)
//                    }
//                    .addOnFailureListener { e ->
//                        Log.i("Sign up try", "Error adding user: ${e.message}")
//                    }
//            } else {
//                Log.i("Sign up try", "Please fill in all fields.")
//            }
//        }

        // boolean to check if the login button should be enabled
        loginBtn.isEnabled = false

        // when the user types in the username or password, the login button will be able to be clicked
        fun checkInputs() {
            //val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            val email = emailInput.text.toString()
            loginBtn.isEnabled = password.isNotEmpty() && email.isNotEmpty()
            //signUpBtn.isEnabled = email.isNotEmpty() && password.isNotEmpty()
        }

        // check if the user has typed in the username or password
        //usernameInput.addTextChangedListener {checkInputs()}
        passwordInput.addTextChangedListener { checkInputs() }
        emailInput.addTextChangedListener { checkInputs() }

        var username: String? = null

        loginBtn.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isNotEmpty()) {
                val documentRef = db.collection("pet owner").document(email)

                documentRef.get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            Log.i("Login", "Email exists in Firestore")
                            username = document.getString("username")
                            val storedPassword = document.getString("password")

                            if (password == storedPassword) {
                                errorMessage.visibility = TextView.GONE
                                Log.i("Login", "Login successful - Username: $username")

                                val intent = Intent(this, SecondPage::class.java)
                                intent.putExtra("USERNAME", username)
                                startActivity(intent)
                                finish()
                            } else {
                                Log.i("Login", "Incorrect password")
                                errorMessage.text = "סיסמא שגויה"
                                errorMessage.visibility = TextView.VISIBLE
                                passwordInput.text.clear()
                                val handler = android.os.Handler()
                                handler.postDelayed({
                                    errorMessage.visibility = TextView.GONE
                                }, 5000)
                            }
                        } else {
                            Log.i("Login", "Email does not exist in Firestore")
                            errorMessage.text = "המייל לא קיים במערכת"
                            errorMessage.visibility = TextView.VISIBLE
                            val handler = android.os.Handler()
                            handler.postDelayed({
                                errorMessage.visibility = TextView.GONE
                            }, 5000)
                            passwordInput.text.clear()
                            emailInput.text.clear()
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.i("Login", "Error checking email: ${e.message}")
                    }
            } else {
                errorMessage.text = "Email is required"
                errorMessage.visibility = TextView.VISIBLE
            }
        }

    }
}