package com.myvet.myvet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI


class SignUp : AppCompatActivity() {

    private lateinit var PetBtn: ImageView
    private lateinit var VetBtn: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AuthUI.getInstance()
                    .delete(this@SignUp)
                    .addOnSuccessListener {
                        val intent = Intent(this@SignUp, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        Log.e(
                            "DeleteUserFromSignUp",
                            "handleOnBackPressed: Couldn't delete user",
                            exception
                        )
                    }
            }
        })

        PetBtn = findViewById(R.id.pet_button)
        VetBtn = findViewById(R.id.vet_button)

        PetBtn.setOnClickListener {
            Log.i("Test pet", "The user will go to pet sign up page")
            val intent = Intent(this, OwnerSignUp::class.java)
            startActivity(intent)
            finish()
        }

        VetBtn.setOnClickListener {
            Log.i("Test pet", "The user will go to vet sign up page")
            val intent = Intent(this, VeterinarianSignUp::class.java)
            startActivity(intent)
            finish()
        }
    }
}