package com.myvet.myvet

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class FindVet : AppCompatActivity() {
    private lateinit var vetsList: LinearLayout

    private lateinit var fusedLocationProvider: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    private fun getVets() {
        // Now check background location
        fusedLocationProvider.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val db = FirebaseFirestore.getInstance()

                val userLatitude = location.latitude
                val userLongitude = location.longitude

                Log.d("Geospatial", "User coords: $userLatitude $userLongitude")

                val center = GeoLocation(userLatitude, userLongitude)
                val radiusInM = 50.0 * 1000.0 // 50 km radius

                // Get query bounds
                val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)
                val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()

                for (b in bounds) {
                    val q: Query = db.collection("users")
                        .whereEqualTo("type", "vet")
                        .orderBy("clinicGeohash")
                        .startAt(b.startHash)
                        .endAt(b.endHash)
                    tasks.add(q.get())
                }

                // Collect all the query results and filter them
                Tasks.whenAllComplete(tasks).addOnCompleteListener {
                    val matchingDocs: MutableList<DocumentSnapshot> = ArrayList()
                    for (task in tasks) {
                        val snap = task.result
                        for (doc in snap!!.documents) {
                            val lat = doc.getString("clinicLat")!!.toDouble()
                            val lon = doc.getString("clinicLon")!!.toDouble()

                            // Calculate the distance between the vet's location and the user
                            val vetLocation = GeoLocation(lat, lon)
                            val distanceInM = GeoFireUtils.getDistanceBetween(vetLocation, center)

                            // If the distance is within the radius, add to results
                            if (distanceInM <= radiusInM) {
                                matchingDocs.add(doc)
                            }
                        }
                    }

                    Log.d("Vet Geospatial Lookup", "Got matching docs: ${matchingDocs.size}")
                    updateUIWithData(matchingDocs)
                }
            }
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

    private fun checkLocationPermission() {
        Log.d(
            "Vet Geospatial", "checkLocationPermission: ${
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            }"
        )

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission()
            }
        } else {
            getVets()
        }
    }

    private fun updateUIWithData(snapshot: MutableList<DocumentSnapshot>) {
        // Clear the existing UI
        vetsList.removeAllViews()

        // Build the UI elements dynamically based on the updated data
        for (vet in snapshot) {
            val vetContainer = LinearLayout(this)
            vetContainer.orientation = LinearLayout.HORIZONTAL

            val name = vet.getString("name")
            val address = vet.getString("clinicAddress")

            val vetText = TextView(this)
            vetText.text =
                "${getString(R.string.name_vet)}: $name\n${getString(R.string.address)}: $address"

            val selectButton = Button(this)
            selectButton.text = getString(R.string.select_button)

            selectButton.setOnClickListener {
                val i = Intent(this, MakeAppointment::class.java)
                i.putExtra("vetId", vet.id)
                startActivity(i)
                finish()
            }

            vetText.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.7f
            ) // 70% width
            selectButton.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.3f
            ) // 30% width

            vetContainer.addView(vetText)
            vetContainer.addView(selectButton)

            vetsList.addView(vetContainer)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                        // Now check background location
                        getVets()
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_LONG).show()

                    // Check if we are in a state where the user has denied the permission and
                    // selected Don't ask again
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", this.packageName, null),
                            ),
                        )
                    }
                }
                return
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_find_vet)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        vetsList = findViewById(R.id.vetsList)

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }
}