package com.example.in2000_prosjektoppgave.di

import android.content.Context
import androidx.room.Room
import com.example.in2000_prosjektoppgave.data.local.AppDatabase
import com.example.in2000_prosjektoppgave.data.local.drifty.SimulationDao
import com.example.in2000_prosjektoppgave.data.local.ocean.OceanDao
import com.example.in2000_prosjektoppgave.data.local.weather.WeatherDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


/**
 * Hilt Room Database module: Used to create room database and data access objects.
 */
@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    /**
     * Hilt provide singleton for main Room database object.
     */
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room
                .databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        AppDatabase.DATABASE_NAME
                )
                .fallbackToDestructiveMigration()
                .build()
    }


    /**
     * Hilt provide singleton for ocean data access object.
     */
    @Singleton
    @Provides
    fun provideOceanDao(appDatabase: AppDatabase): OceanDao {
        return appDatabase.oceanDao()
    }


    /**
     * Hilt provide singleton weather data access object.
     */
    @Singleton
    @Provides
    fun provideWeatherDAO(appDatabase: AppDatabase): WeatherDao {
        return appDatabase.weatherDao()
    }


    /**
     * Hilt provide singleton simulation data access object.
     */
    @Singleton
    @Provides
    fun provideSimulationDAO(appDatabase: AppDatabase): SimulationDao {
        return appDatabase.simulationDao()
    }

}