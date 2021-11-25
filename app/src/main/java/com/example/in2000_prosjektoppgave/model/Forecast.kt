package com.example.in2000_prosjektoppgave.model

import com.example.in2000_prosjektoppgave.data.local.weather.DataWeatherEntity
import com.example.in2000_prosjektoppgave.data.local.weather.WeatherEntity


/**
 * Data class for weather objects, contains detailed information about the weather at a
 * specific location.
 *
 * @property updated Last time the data was updated.
 * @property timeSeries List of Data Ocean objects.
 * @property longitude Longitude coordinate.
 * @property latitude Latitude coordinate.
 */
data class Forecast(
    val updated: String,
    val longitude: Double,
    val latitude: Double,
    val timeSeries: List<DataForecast>
) {

    /**
     * Extracts needed information for database insertion of Weather Entity.
     */
    fun toWeatherEntity() = WeatherEntity(
        id = longitude.toString() + latitude.toString(),
        updated = this.updated,
        longitude = this.longitude,
        latitude = this.latitude
    )


    /**
     * Extracts needed information for database insertion of Data Weather Entity.
     */
    fun toDataEntity(id: String) = this.timeSeries.map {
        DataWeatherEntity(
            weatherId = id,
            time = it.time,
            wind_dir = it.windDir,
            wind_speed = it.windSpeed,
            temperature = it.temperature,
            symbol = it.symbol
        )
    }

}


/**
 * Data class containing information about weather conditions at a single point in time, at
 * a specific location.
 *
 * @property time Time instant.
 * @property windDir Wind direction.
 * @property windSpeed Wind speed.
 * @property temperature Air temperature.
 * @property symbol Weather symbol for image files.
 */
data class DataForecast(
    val time: String,
    val windDir: Double? = null,
    val windSpeed: Double? = null,
    val temperature: Double? = null,
    val symbol: String? = null
)