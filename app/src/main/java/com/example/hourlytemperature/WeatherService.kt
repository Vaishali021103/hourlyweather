package com.example.hourlytemperature

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("onecall")
    fun getHourlyWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("exclude") exclude: String = "current,minutely,daily,alerts",
        @Query("appid") apiKey: String
    ): Call<HourlyWeatherResponse>

    fun getHourlyWeather(
        latitude: Double,
        longitude: Double,
        exclude: String
    ): Call<HourlyWeatherResponse>
}