package com.example.hourlytemperature

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("weather")
    fun getHourlyWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("exclude") exclude: String = "current,minutely,daily,alerts",
        @Query("appid") apiKey: String
    ): Call<HourlyWeatherResponse>
}