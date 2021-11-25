package com.example.in2000_prosjektoppgave.di

import com.example.in2000_prosjektoppgave.data.local.drifty.DriftyLocalSource
import com.example.in2000_prosjektoppgave.data.local.drifty.SimulationDao
import com.example.in2000_prosjektoppgave.data.local.ocean.OceanDao
import com.example.in2000_prosjektoppgave.data.local.weather.WeatherDao
import com.example.in2000_prosjektoppgave.data.remote.drifty.DriftyRemoteSource
import com.example.in2000_prosjektoppgave.data.remote.drifty.DriftyService
import com.example.in2000_prosjektoppgave.data.remote.met.MetService
import com.example.in2000_prosjektoppgave.data.repository.DriftyRepository
import com.example.in2000_prosjektoppgave.data.repository.WeatherRepository
import com.example.in2000_prosjektoppgave.ui.MainApplication
import com.example.in2000_prosjektoppgave.util.NetCdfParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


/**
 * Hilt Repository module: Used to create repository singletons and associated
 * data source objects, and NetCDF parser.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /**
     * Hilt provide singleton for Weather repository object.
     */
    @Provides
    @Singleton
    fun provideWeatherRepository(
            metService: MetService,
            localWeather: WeatherDao,
            localOcean: OceanDao
    ): WeatherRepository = WeatherRepository(
            metService = metService,
            localWeather = localWeather,
            localOcean = localOcean,
    )


    /**
     * Hilt provide singleton for drifty remote source object.
     */
    @Provides
    @Singleton
    fun provideDriftyRemoteSource(
            webService: DriftyService
    ): DriftyRemoteSource = DriftyRemoteSource(
            webService = webService
    )


    /**
     * Hilt provide singleton for drifty locale source object.
     */
    @Provides
    @Singleton
    fun provideDriftyLocalSource(
            app: MainApplication,
            database: SimulationDao
    ): DriftyLocalSource = DriftyLocalSource(
            app = app,
            database = database
    )


    /**
     * Hilt provide singleton for drifty repository object.
     */
    @Provides
    @Singleton
    fun provideDriftyRepository(
            remoteSource: DriftyRemoteSource,
            localeSource: DriftyLocalSource
    ): DriftyRepository = DriftyRepository(
            remoteSource = remoteSource,
            localeSource = localeSource
    )


    /**
     * Hilt provide singleton for NetCDF parser.
     */
    @Singleton
    @Provides
    fun provideNetCdf(mainApplication: MainApplication): NetCdfParser = NetCdfParser(mainApplication)

}