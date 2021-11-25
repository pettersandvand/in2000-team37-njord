package com.example.in2000_prosjektoppgave.data.repository

import com.example.in2000_prosjektoppgave.data.local.ocean.OceanDao
import com.example.in2000_prosjektoppgave.data.local.weather.WeatherDao
import com.example.in2000_prosjektoppgave.data.remote.met.MetService
import com.example.in2000_prosjektoppgave.model.Forecast
import com.example.in2000_prosjektoppgave.model.Ocean
import com.example.in2000_prosjektoppgave.util.Resource
import com.skydoves.sandwich.message
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*


/**
 * Class for Met repository. Collects functions needed for persistence, and communication
 * with Met API
 *
 * @property metService A retrofit service object.
 * @property localWeather A weather data access object.
 * @property localOcean A ocean data access object.
 */
class WeatherRepository(
    private val metService: MetService,
    private val localWeather: WeatherDao,
    private val localOcean: OceanDao
) {

    // Constants
    companion object {
        const val UNKNOWN_ERROR = "Ukjent feil!"
    }

    /**
     * Function that returns weather as a state. Success, error and loading states are
     * represented. A delay is added to show loading animation in UI, for testing.
     *
     * @param lat Latitude coordinate.
     * @param lon Longitude coordinate.
     */
    fun getWeather(lat: Double, lon: Double) = flow<Resource<Forecast>> {

        // Emit loading and (Delay included to show functionality in UI)
        emit(Resource.Loading())
        delay(1000)

        // Fetch any cached data in database and display until api call finishes
        val dbQuery = localWeather.getData(lat.toString() + lon.toString())
        if (dbQuery != null) {
            emit(Resource.Success(data = dbQuery.toDomain()))
        }

        // Try to fetch data from API and update database
        metService.getWeather(lat, lon).suspendOnSuccess {

            // If body is non empty
            if (data != null) {

                // Update database and emit success
                val model = data!!.toDomain()
                localWeather.insert(model)
                emit(Resource.Success(data = model))

            } else {

                // On unknown error
                emit(Resource.Error(message = UNKNOWN_ERROR))
            }
        }.suspendOnError {

            // On error
            emit(Resource.Error(message = message()))

        }.suspendOnException {

            // On exception
            emit(Resource.Error(message = message()))
        }
    }.flowOn(Dispatchers.IO)


    /**
     * Function that returns ocean data as a state. Success, error and loading states are
     * represented. A delay is added to show loading animation in UI, for testing.
     *
     * @param lat Latitude coordinate.
     * @param lon Longitude coordinate.
     */
    fun getOcean(lat: Double, lon: Double) = flow<Resource<Ocean>> {

        // Emit loading and delay for testing. (Delay included to show functionality in UI)
        emit(Resource.Loading())
        delay(1000)

        // Fetch any cached data in database and display until api call finishes
        val dbQuery = localOcean.getData(lat.toString() + lon.toString())
        if (dbQuery != null) {
            emit(Resource.Success(data = dbQuery.toDomain()))
        }

        // Try to fetch data from API, update database and emit result
        metService.getOcean(lat, lon).suspendOnSuccess {

            // If body is non empty
            if (data != null) {

                // Search string from UI
                val search = lat.toString() + lon.toString()

                // Data from API
                val model = data!!.toDomain()

                // Insert data from API, with search string and emit
                localOcean.insert(model, search)
                emit(Resource.Success(model))

            } else {

                // On unknown error
                emit(Resource.Error(message = UNKNOWN_ERROR))
            }
        }.suspendOnError {

            // On error
            emit(Resource.Error(message = message()))

        }.suspendOnException {

            // On exception
            emit(Resource.Error(message = message()))
        }
    }.flowOn(Dispatchers.IO)


    /**
     * Function to delete all ocean and weather data from database.
     */
    suspend fun deleteAll() {
        localWeather.deleteAll()
        localOcean.deleteAll()
    }

}