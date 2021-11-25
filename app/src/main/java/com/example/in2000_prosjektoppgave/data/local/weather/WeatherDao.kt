package com.example.in2000_prosjektoppgave.data.local.weather

import androidx.room.*
import com.example.in2000_prosjektoppgave.model.Forecast


/**
 * Simulation data access object for weather data objects. That can be use to query, update,
 * insert, and delete data in the database.
 */
@Dao
abstract class WeatherDao {

    /**
     * Function to insert a domain model properly into the database.
     */
    suspend fun insert(forecast: Forecast) {

        // Create the different entities
        val weatherEntity: WeatherEntity = forecast.toWeatherEntity()
        val dataWeatherEntity: List<DataWeatherEntity> = forecast.toDataEntity(weatherEntity.id)

        // Insert weather entity
        insertWeather(weatherEntity)

        // Insert weather data entities
        insertData(dataWeatherEntity)
    }


    /**
     * Function to insert a weather entity.
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertWeather(weatherEntity: WeatherEntity)


    /**
     * Function to insert a list of weather data entities.
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertData(dataWeatherEntity: List<DataWeatherEntity>)


    /**
     * Function to delete all weather data in database.
     */
    @Query("DELETE FROM WeatherEntity")
    abstract suspend fun deleteAll()


    /**
     * Function to fetch weather data for a single point.
     */
    @Transaction
    @Query("SELECT * FROM WeatherEntity WHERE id = :id")
    abstract suspend fun getData(id: String): WeatherWithData?

}