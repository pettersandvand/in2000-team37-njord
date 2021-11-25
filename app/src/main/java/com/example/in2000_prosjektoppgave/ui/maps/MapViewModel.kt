package com.example.in2000_prosjektoppgave.ui.maps

import android.graphics.Color.argb
import androidx.lifecycle.*
import com.example.in2000_prosjektoppgave.R
import com.example.in2000_prosjektoppgave.model.Trajectory
import com.example.in2000_prosjektoppgave.util.NetCdfParser
import com.example.in2000_prosjektoppgave.util.Resource
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject


/**
 * View model used by map view fragment.
 *
 * @property data A NetCDF parser object.
 */
@HiltViewModel
class MapViewModel @Inject constructor(
        private val data: NetCdfParser,
        savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Constants
    companion object {
        const val Z_INDEX = 2f
        const val KEY = "FILE_1"
    }

    // File name from saved state handle - passed by Drifty fragment
    private val filename = savedStateHandle.get<String>(KEY)

    // Polyline and bounds for map
    private var polylines = listOf<Polyline>()
    private var mapBounds: LatLngBounds? = null

    // List Trajectory objects
    private lateinit var trajectories: List<Trajectory>

    // Live data used to describe the status of file parsing
    private val _status: MutableLiveData<Resource<String>> = MutableLiveData()
    val status: LiveData<Resource<String>>
        get() = _status


    init {

        // Parse NetCDF file on default dispatcher
        viewModelScope.launch(Dispatchers.Default) {

            // Post loading state
            _status.postValue(Resource.Loading())

            // Check if filename is provided
            if (filename != null) {

                // Parse the file
                trajectories = getData(filename)

                if (trajectories.isNotEmpty()) {

                    // Post success if non empty trajectory list
                    _status.postValue(Resource.Success())
                } else {
                    // Post error if failed
                    _status.postValue(Resource.Error())
                }
            } else {

                // File name not passed properly to view model
                trajectories = emptyList()
                _status.postValue(Resource.Error())
            }
        }

    }


    /**
     * Function that retrieves parsed NetCDF data.
     *
     * @param fileName Filename of locally persisted NetCDF file.
     * @return List of trajectory objects.
     */
    private fun getData(fileName: String): List<Trajectory> = data.getData(fileName)


    /**
     * Function to create and add poly lines to google map, it also calculates longitude
     * and latitude bounds.
     *
     * @param map Google Map reference.
     */
    fun viewSimulation(map: GoogleMap) {
        viewModelScope.launch(viewModelScope.coroutineContext) {

            // Temp poly line list
            val list = mutableListOf<Polyline>()

            // Color for lines
            val col = argb(50, 255, 160, 0)

            // Bounds for latitude and longitude camera zoom
            val builder = LatLngBounds.Builder()

            // Add poly line to map for all trajectories
            trajectories.forEach { it ->

                val poly = map.addPolyline(it.polyLine)

                // Set options for polyline
                poly.apply {
                    isClickable = true
                    color = col
                    width = 5F
                    isGeodesic = true
                    zIndex = Z_INDEX
                }

                // Add the poly line to array
                list.add(poly)

                // Add coordinate information to LatLngBounds builder
                poly.points.forEach {
                    builder.include(it)
                }
            }

            // Set bounds and add poly lines list
            polylines = list

            // Might fail if southern latitude exceeds northern latitude, catch not to crash app
            mapBounds = try{builder.build()} catch (e: Exception){null}
        }
    }


    /**
     * Function that returns the longitude and latitude bounds for the simulation, if not
     * existing it zooms to Norway.
     */
    fun getBounds(): LatLngBounds = mapBounds ?: LatLngBounds.builder()
            .include(LatLng(58.0788841824, 4.99207807783))
            .include(LatLng(72.6571442736, 31.29341841))
            .build()


    /**
     * Function to change the visibility of a list of polyline objects.
     */
    fun changeVisibility(fab: FloatingActionButton){
        polylines.forEach { it.isVisible = !it.isVisible }
        if(polylines.isNotEmpty()){
            if(!polylines[0].isVisible){
                fab.setImageResource(R.drawable.ic_outline_visibility_24)
            } else{
                fab.setImageResource(R.drawable.ic_outline_visibility_off_24)
            }
        }
    }

}