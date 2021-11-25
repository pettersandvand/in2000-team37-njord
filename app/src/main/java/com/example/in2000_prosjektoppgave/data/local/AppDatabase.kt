package com.example.in2000_prosjektoppgave.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.in2000_prosjektoppgave.data.local.drifty.SimulationDao
import com.example.in2000_prosjektoppgave.data.local.drifty.SimulationEntity
import com.example.in2000_prosjektoppgave.data.local.ocean.DataOceanEntity
import com.example.in2000_prosjektoppgave.data.local.ocean.OceanDao
import com.example.in2000_prosjektoppgave.data.local.ocean.OceanEntity
import com.example.in2000_prosjektoppgave.data.local.weather.DataWeatherEntity
import com.example.in2000_prosjektoppgave.data.local.weather.WeatherDao
import com.example.in2000_prosjektoppgave.data.local.weather.WeatherEntity


/**
 *  Room database abstract class used to persist and store data for the application.
 *
 *  Name of database: app_db
 */
@Database(entities = [
    WeatherEntity::class,
    DataWeatherEntity::class,
    OceanEntity::class,
    DataOceanEntity::class,
    SimulationEntity::class],
        version = 2,
        exportSchema = false)
@TypeConverters(ListConverter::class)
abstract class AppDatabase : RoomDatabase(){

    // Weather data access object
    abstract fun weatherDao(): WeatherDao

    // Ocean data access object
    abstract fun oceanDao() : OceanDao

    // Simulation data access object
    abstract fun simulationDao() : SimulationDao

    // Constants
    companion object{
        const val DATABASE_NAME = "app_db"
    }

}