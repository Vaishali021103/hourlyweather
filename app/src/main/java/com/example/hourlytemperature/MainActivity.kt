package com.example.hourlytemperature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
//import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLastKnownLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    // Call the weather API with retrieved coordinates
                    getHourlyWeatherData(latitude, longitude)
                }
            }
    }
    private fun getHourlyWeatherData(latitude: Double, longitude: Double) {
        val apiKey = "62fa0f3f1d8105534a3605af3bf67cae" // Replace with your actual API key
        val weatherApiClient = WeatherApiClient()
        val weatherService = weatherApiClient.getWeatherService()

        val call = weatherService.getHourlyWeather(latitude, longitude, apiKey)
        call.enqueue(object : retrofit2.Callback<HourlyWeatherResponse> {
            override fun onResponse(
                call: retrofit2.Call<HourlyWeatherResponse>,
                response: retrofit2.Response<HourlyWeatherResponse>
            ) {
                if (response.isSuccessful) {
                    val hourlyWeatherResponse = response.body()
                    // Handle the hourly weather data and update your UI
                    hourlyWeatherResponse?.let {
                        val hourlyWeatherList = hourlyWeatherResponse.hourly
                        // Update UI with hourly weather details
//                        val zerotym = findViewById<TextView>(R.id.zerotym)
//                        zerotym.text= hourlyWeatherList.toString()
                        val zerotemp = findViewById<TextView>(R.id.zerotemp)
                        zerotemp.text = hourlyWeatherList.toString()

                        val day = findViewById<TextView>(R.id.day)
                        val date = findViewById<TextView>(R.id.date)
                        val defaultTimeZone = TimeZone.getDefault()
                        val defaultCalendar = Calendar.getInstance(defaultTimeZone)
                        val defaultTime = defaultCalendar.time
                        val dateFormat1 = SimpleDateFormat("EEEE", Locale.getDefault())
                        val dateFormat2 = SimpleDateFormat("dd-MMMM-yyyy", Locale.getDefault())
                        val defaultFormattedDay = dateFormat1.format(defaultTime)
                        val defaultFormattedDate = dateFormat2.format(defaultTime)
                        day.text = defaultFormattedDay
                        date.text= defaultFormattedDate
                    }
                } else {
                    // Handle API error
                }
            }

            override fun onFailure(call: retrofit2.Call<HourlyWeatherResponse>, t: Throwable) {
                // Handle network failure or request cancellation
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, perform the operation
                    getLastKnownLocation()
                } else {
                    // Permission denied, handle accordingly (e.g., show a message, disable functionality)
                    // ...
                }
            }
        }
    }
}


