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
import androidx.core.widget.addTextChangedListener
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.moshi.Json
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


class VeterinarianSignUp2 : AppCompatActivity() {
    private lateinit var expertise: EditText
    private lateinit var aboutMe: EditText

    private lateinit var register: Button
    private lateinit var errorMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_veterinarian_sign_up2)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@VeterinarianSignUp2, SignUp::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        })

        expertise = findViewById(R.id.expertise)
        aboutMe = findViewById(R.id.aboutMe)
        register = findViewById(R.id.submitButton)
        errorMessage = findViewById(R.id.errorMessage)

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        //Set the register button to be disabled
//        register.isEnabled = false


        register.setOnClickListener {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            val type = Types.newParameterizedType(List::class.java, NominatimResult::class.java)
            val adapter = moshi.adapter<List<NominatimResult>>(type)

            val url = "https://nominatim.openstreetmap.org/search?q=${
                URLEncoder.encode(

                    "UTF-8"
                )
            }&format=json&limit=1"

            val request = Request.Builder()
                .header("User-Agent", "MyVet Android Application")
                .url(url)
                .build()

            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")

                        val json = response.body!!.string()
                        val results = adapter.fromJson(json)

                        if (results != null) {
                            Log.d(
                                "Autocomplete Predictions",
                                "Prediction results: ${results.size}"
                            )

                            if (results.isEmpty()) {
                                Handler(Looper.getMainLooper()).post {
                                    Toast.makeText(
                                        this@VeterinarianSignUp2,
                                        "Address not found",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                return
                            }

                            val result = results[0]

                            val userData = hashMapOf(
                                "type" to "vet",
                                "name" to user!!.displayName,
                                "clinicAddress" to result.displayName,
                                "clinicAddressId" to result.placeId,
                                "clinicLat" to result.lat,
                                "clinicLon" to result.lon,
                                "clinicGeohash" to GeoFireUtils.getGeoHashForLocation(GeoLocation(result.lat.toDouble(), result.lon.toDouble())),
                                "expertise" to expertise.text.toString(),

                                "aboutMe" to aboutMe.text.toString(),
                            )

                            db.collection("users")
                                .document(user.uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    Log.i(
                                        "Sign up vet",
                                        "Vet registered successfully"
                                    )

                                    val intent = Intent(this@VeterinarianSignUp2, VetWindow::class.java)
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
            })
        }
    }
}