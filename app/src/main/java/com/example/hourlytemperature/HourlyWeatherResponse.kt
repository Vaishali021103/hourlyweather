package com.example.hourlytemperature

data class HourlyWeatherResponse(
    val hourly: List<HourlyWeather>
)

data class HourlyWeather(
    val time: Long,
    val temperature: Double,
    val weather: List<Weather>
)

data class Weather(
    val main: String,
    val description: String
)
