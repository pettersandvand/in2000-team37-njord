package com.example.in2000_prosjektoppgave.data.remote.met

import com.example.in2000_prosjektoppgave.data.remote.met.model.weather.ForecastDto
import com.example.in2000_prosjektoppgave.data.remote.met.model.ocean.OceanDto
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Retrofit service interface with functions for MET API.
 */
interface MetService {

    // Constants
    companion object {
        const val BASE_URL_MET = "https://in2000-apiproxy.ifi.uio.no/weatherapi/"
        const val LOCATION_FORECAST = "locationforecast/2.0/compact.json"
        const val OCEAN_FORECAST = "oceanforecast/2.0/complete.json"
        const val LAT = "lat"
        const val LON = "lon"
        const val USER_AGENT = "user-agent"
        const val TEAM_NAME = "Team-37"
    }

    /**
     * Get request that returns a weather forecast for a specific location.
     *
     * @param lat Latitude coordinate.
     * @param lon Longitude coordinate
     */
    @GET(LOCATION_FORECAST)
    suspend fun getWeather(
            @Query(LAT) lat: Double,
            @Query(LON) lon: Double
    ): ApiResponse<ForecastDto>


    /**
     * Get request that returns a ocean forecast for a specific location.
     *
     * @param lat Latitude coordinate.
     * @param lon Longitude coordinate
     */
    @GET(OCEAN_FORECAST)
    suspend fun getOcean(
            @Query(LAT) lat: Double,
            @Query(LON) lon: Double
    ): ApiResponse<OceanDto>

}