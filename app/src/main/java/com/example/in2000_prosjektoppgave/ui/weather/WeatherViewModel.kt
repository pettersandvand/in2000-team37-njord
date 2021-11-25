package com.example.in2000_prosjektoppgave.ui.weather

import androidx.lifecycle.*
import com.example.in2000_prosjektoppgave.model.Forecast
import com.example.in2000_prosjektoppgave.data.repository.WeatherRepository
import com.example.in2000_prosjektoppgave.model.Ocean
import com.example.in2000_prosjektoppgave.util.ForecastType
import com.example.in2000_prosjektoppgave.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * View model used by weather fragment.
 *
 * @property weatherRepository Weather Repository object.
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(private val weatherRepository: WeatherRepository) :
        ViewModel() {

    // Live data for forecast weather object
    private val _weather: MediatorLiveData<Resource<Forecast>> = MediatorLiveData()
    val weather: LiveData<Resource<Forecast>>
        get() = _weather

    // Live data for ocean object
    private val _ocean: MediatorLiveData<Resource<Ocean>> = MediatorLiveData()
    val ocean: LiveData<Resource<Ocean>>
        get() = _ocean


    /**
     * Function used to fetch ocean and forecast data from source.
     */
    fun fetchAllData(lat: Double, lon: Double) {
        updateWeather(lat, lon)
        updateOcean(lat, lon)
    }


    /**
     * Function to empty out the database for ocean and weather data.
     */
    fun emptyDatabase() = viewModelScope.launch(Dispatchers.IO) {
        weatherRepository.deleteAll()
    }


    /**
     * Function to fetch and update live data for weather data.
     */
    private fun updateWeather(lat: Double, lon: Double) {
        _weather.addSource(weatherRepository.getWeather(lat, lon).asLiveData()) {
            _weather.value = it
        }
    }


    /**
     * Function to fetch and update live data for ocean data.
     */
    private fun updateOcean(lat: Double, lon: Double) {
        _ocean.addSource(weatherRepository.getOcean(lat, lon).asLiveData()) {
            _ocean.value = it
        }
    }


    /**
     * Function used to clear error in observable live data.
     */
    fun clearError(type: ForecastType) = when (type) {
        ForecastType.OCEAN -> _ocean.postValue(null)
        ForecastType.WEATHER -> _weather.postValue(null)
    }

}