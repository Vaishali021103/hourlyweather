package com.example.hourlytemperature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
//import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Check location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastKnownLocation()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)
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
            .addOnFailureListener {
                Toast.makeText(this,"Failure", Toast.LENGTH_SHORT).show()
            }
    }
    private fun getHourlyWeatherData(latitude: Double, longitude: Double) {
        val apiKey = "62fa0f3f1d8105534a3605af3bf67cae" // Replace with your actual API key
        val weatherApiClient = WeatherApiClient()
        val weatherService = weatherApiClient.getWeatherService()

        val call = weatherService.getHourlyWeather(latitude, longitude, "current,minutely,daily,alerts" , apiKey)
        call.enqueue(object : Callback<HourlyWeatherResponse> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<HourlyWeatherResponse>,
                response: Response<HourlyWeatherResponse>
            ) {
                if (response.isSuccessful) {
                    val hourlyWeatherResponse = response.body()
                    // Handle the hourly weather data and update your UI
                    hourlyWeatherResponse?.let {
                        val hourlyWeatherList = hourlyWeatherResponse.hourly
                        // Update UI with hourly weather details
                        if(hourlyWeatherList != null && hourlyWeatherList.isNotEmpty()){
                            val zerotemp = findViewById<TextView>(R.id.zerotemp)
                            zerotemp.text = hourlyWeatherList.toString()
                        }else{
                            Log.e("this","error")
                        }

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
                    Toast.makeText(this@MainActivity,"Error",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<HourlyWeatherResponse>, t: Throwable) {
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
                    Log.d("TAG", "Debug log message");
                    Log.e("TAG", "Error log message");
                    Log.i("TAG", "Information log message");
                }
            }
        }
    }
}



