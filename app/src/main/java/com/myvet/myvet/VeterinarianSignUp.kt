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


data class NominatimResult(
    @Json(name = "place_id") val placeId: Long,
    val lat: String,
    val lon: String,
    @Json(name = "display_name") val displayName: String,
    val type: String
)

class VeterinarianSignUp : AppCompatActivity() {
    private lateinit var clinicName: EditText
    private lateinit var clinicLocation: EditText
    private lateinit var yearsOfExperience: EditText
    private lateinit var expertise: EditText
    private lateinit var aboutMe: EditText
    private lateinit var register: Button
    private lateinit var errorMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_veterinarian_sign_up)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@VeterinarianSignUp, SignUp::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        })

        clinicName = findViewById(R.id.clinicName)
        clinicLocation = findViewById(R.id.clinicLocation)
        yearsOfExperience = findViewById(R.id.yearsOfExperience)
//        expertise = findViewById(R.id.expertise)
//        aboutMe = findViewById(R.id.aboutMe)
        register = findViewById(R.id.submitButton)
        errorMessage = findViewById(R.id.errorMessage)

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        //Set the register button to be disabled
        register.isEnabled = false

        //Function to check if the user has typed in all the information needed for the registration
        fun checkInputs() {
            val clinicName = clinicName.text.toString()
            val clinicLocation = clinicLocation.text.toString()
            val expertise = yearsOfExperience.text.toString()
            register.isEnabled =
                clinicName.isNotEmpty() && clinicLocation.isNotEmpty() && expertise.isNotEmpty()
        }

        clinicName.addTextChangedListener { checkInputs() }
        clinicLocation.addTextChangedListener { checkInputs() }
        yearsOfExperience.addTextChangedListener { checkInputs() }

        register.setOnClickListener {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            val type = Types.newParameterizedType(List::class.java, NominatimResult::class.java)
            val adapter = moshi.adapter<List<NominatimResult>>(type)

            val url = "https://nominatim.openstreetmap.org/search?q=${
                URLEncoder.encode(
                    clinicLocation.text.toString(),
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
                                        this@VeterinarianSignUp,
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
                                "clinicName" to clinicName.text.toString(),
                                "clinicAddress" to result.displayName,
                                "clinicAddressId" to result.placeId,
                                "clinicLat" to result.lat,
                                "clinicLon" to result.lon,
                                "clinicGeohash" to GeoFireUtils.getGeoHashForLocation(GeoLocation(result.lat.toDouble(), result.lon.toDouble())),
                                "expertise" to expertise.text.toString(),
                                "yearsOfExperience" to yearsOfExperience.text.toString(),
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

                                    val intent = Intent(this@VeterinarianSignUp, VetWindow::class.java)
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


//
//
//
//
//
//
//
//
//class VeterinarianSignUp : AppCompatActivity() {
//    private lateinit var clinicName: EditText
//    private lateinit var clinicLocation: EditText
//    private lateinit var yearsOfExperience: EditText
////    private lateinit var expertise: EditText
////    private lateinit var aboutMe: EditText
//    private lateinit var register: Button
//    private lateinit var errorMessage: TextView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_veterinarian_sign_up)
//
//        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                val intent = Intent(this@VeterinarianSignUp, SignUp::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//                startActivity(intent)
//                finish()
//            }
//        })
//
//        clinicName = findViewById(R.id.clinicName)
//        clinicLocation = findViewById(R.id.clinicLocation)
//        yearsOfExperience = findViewById(R.id.yearsOfExperience)
////        expertise = findViewById(R.id.expertise)
////        aboutMe = findViewById(R.id.aboutMe)
//        register = findViewById(R.id.submitButton)
//        errorMessage = findViewById(R.id.errorMessage)
//
//        val db = FirebaseFirestore.getInstance()
//        val user = FirebaseAuth.getInstance().currentUser
//
//        //Set the register button to be disabled
//        register.isEnabled = false
//
//        //Function to check if the user has typed in all the information needed for the registration
//        fun checkInputs() {
//            val clinicName = clinicName.text.toString()
//            val clinicLocation = clinicLocation.text.toString()
//            val expertise = yearsOfExperience.text.toString()
//            register.isEnabled =
//                clinicName.isNotEmpty() && clinicLocation.isNotEmpty() && expertise.isNotEmpty()
//        }
//
//        clinicName.addTextChangedListener { checkInputs() }
//        clinicLocation.addTextChangedListener { checkInputs() }
//        yearsOfExperience.addTextChangedListener { checkInputs() }
//
//        register.setOnClickListener {
//            val moshi = Moshi.Builder()
//                .add(KotlinJsonAdapterFactory())
//                .build()
//            val type = Types.newParameterizedType(List::class.java, NominatimResult::class.java)
//            val adapter = moshi.adapter<List<NominatimResult>>(type)
//
//            val url = "https://nominatim.openstreetmap.org/search?q=${
//                URLEncoder.encode(
//                    clinicLocation.text.toString(),
//                    "UTF-8"
//                )
//            }&format=json&limit=1"
//
//            val request = Request.Builder()
//                .header("User-Agent", "MyVet Android Application")
//                .url(url)
//                .build()
//
//            OkHttpClient().newCall(request).enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    e.printStackTrace()
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    response.use {
//                        if (!response.isSuccessful) throw IOException("Unexpected code $response")
//
//                        val json = response.body!!.string()
//                        val results = adapter.fromJson(json)
//
//                        if (results != null) {
//                            Log.d(
//                                "Autocomplete Predictions",
//                                "Prediction results: ${results.size}"
//                            )
//
//                            if (results.isEmpty()) {
//                                Handler(Looper.getMainLooper()).post {
//                                    Toast.makeText(
//                                        this@VeterinarianSignUp,
//                                        "Address not found",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
//                                return
//                            }
//
//                            val result = results[0]
//
//                            val userData = hashMapOf(
//                                "type" to "vet",
//                                "name" to user!!.displayName,
//                                "clinicName" to clinicName.text.toString(),
//                                "clinicAddress" to result.displayName,
//                                "clinicAddressId" to result.placeId,
//                                "clinicLat" to result.lat,
//                                "clinicLon" to result.lon,
//                                "clinicGeohash" to GeoFireUtils.getGeoHashForLocation(GeoLocation(result.lat.toDouble(), result.lon.toDouble())),
////                                "expertise" to expertise.text.toString(),
//                                "yearsOfExperience" to yearsOfExperience.text.toString(),
////                                "aboutMe" to aboutMe.text.toString(),
//                            )
//
//                            db.collection("users")
//                                .document(user.uid)
//                                .set(userData)
//                                .addOnSuccessListener {
//                                    Log.i(
//                                        "Sign up vet",
//                                        "Vet registered successfully"
//                                    )
//
//                                    val intent = Intent(this@VeterinarianSignUp, VeterinarianSignUp2::class.java)
//                                    startActivity(intent)
//                                    finish()
//                                }
//                                .addOnFailureListener {
//                                    Log.i(
//                                        "Sign up vet",
//                                        "Vet registration failed"
//                                    )
//                                }
//                        }
//                    }
//                }
//            })
//        }
//    }
//}
//
//
//
//
//
//
