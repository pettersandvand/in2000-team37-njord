package com.example.in2000_prosjektoppgave.data.local.weather

import androidx.room.*
import com.example.in2000_prosjektoppgave.model.DataForecast
import com.example.in2000_prosjektoppgave.model.Forecast


/**
 * Data class used by database for storage of weather data objects.
 *
 * @property id Primary key, coordinates.
 * @property updated Date for when the data was updated server side.
 * @property longitude Longitude coordinate.
 * @property latitude Latitude coordinate.
 */
@Entity
data class WeatherEntity(
        @PrimaryKey
        var id: String,
        var updated: String,
        var longitude: Double,
        var latitude: Double
)


/**
 * Data class used by database to store information about weather conditions at a single point
 * in time, at a specific location.
 *
 * @property dataId Primary key, autogenerated.
 * @property weatherId Foreign key.
 * @property time Time instant.
 * @property wind_dir Wind direction.
 * @property wind_speed Wind speed.
 * @property temperature Air temperature.
 * @property symbol Weather symbol.
 */
@Entity(
        foreignKeys = [ForeignKey(
                entity = WeatherEntity::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("weatherId"),
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )],
        indices = [Index(value = ["weatherId"])]
)
data class DataWeatherEntity(
        var weatherId: String,
        @PrimaryKey(autoGenerate = true)
        var dataId: Int = 0,
        var time: String,
        var wind_dir: Double? = null,
        var wind_speed: Double? = null,
        var temperature: Double? = null,
        var symbol: String? = null
)


/**
 * Data class for relation modeling, one to many between weather entity and data entity.
 */
data class WeatherWithData(
        @Embedded val weatherEntity: WeatherEntity,
        @Relation(
                parentColumn = "id",
                entityColumn = "weatherId"
        )
        val data: List<DataWeatherEntity>
) {

    /**
     * Function used to convert a WeatherWithData object to a domain model.
     */
    fun toDomain(): Forecast = Forecast(
            updated = weatherEntity.updated,
            longitude = weatherEntity.longitude,
            latitude = weatherEntity.latitude,
            timeSeries = data.map {
                DataForecast(
                        time = it.time,
                        windSpeed = it.wind_speed,
                        windDir = it.wind_dir,
                        temperature = it.temperature,
                        symbol = it.symbol
                )
            }
    )

}