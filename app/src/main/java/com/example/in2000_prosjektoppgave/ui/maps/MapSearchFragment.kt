package com.example.in2000_prosjektoppgave.ui.maps

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.in2000_prosjektoppgave.R
import com.example.in2000_prosjektoppgave.databinding.FragmentMapSearchBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import com.google.android.gms.maps.GoogleMap as GoogleMap


/**
 * Map search fragment: Used to retrieve longitude and latitude coordinates from map,
 * used by other fragments.
 */
@AndroidEntryPoint
class MapSearchFragment : Fragment() {

    // Constants
    companion object {
        const val LOCATION_PERMISSION_REQUEST = 1
        const val LONGITUDE = "LONGITUDE"
        const val LATITUDE = "LATITUDE"
        const val REQUEST_KEY = "REQUEST_KEY"
    }

    // Coordinate object
    object Coordinate {
        var state = false
        var lat = 0.0
        var lon = 0.0
    }

    // Binding for views
    private var _binding: FragmentMapSearchBinding? = null
    private val binding get() = _binding!!

    // Map and marker
    private lateinit var map: GoogleMap
    private lateinit var marker: Marker


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapSearchBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.googleMap.onCreate(savedInstanceState)
        binding.googleMap.onResume()

        // Set up and wait for map
        binding.googleMap.getMapAsync {
            map = it
            map.uiSettings.isCompassEnabled = true

            // Request location permission
            getLocationAccess()

            // Set up bindings
            setUpBindings()
            setUpMapBindings()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        // Ask user for location permission
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (!grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                // Display Snack bar if permission is not granted
                Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        resources.getString(R.string.location_error),
                        Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        binding.googleMap.onResume()
    }


    override fun onStart() {
        super.onStart()
        binding.googleMap.onStart()
    }


    override fun onStop() {
        super.onStop()
        binding.googleMap.onStop()
    }


    override fun onPause() {
        super.onPause()
        binding.googleMap.onPause()
    }


    override fun onLowMemory() {
        super.onLowMemory()
        binding.googleMap.onLowMemory()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.googleMap.onSaveInstanceState(outState)
    }


    /**
     * Sets up bindings for views, on click listeners.
     */
    private fun setUpBindings() {

        // On click listener for floating action button
        binding.buttonSend.setOnClickListener {

            // If marker is placed navigate back and send coordinates
            if (Coordinate.state) {
                val bundle = bundleOf(
                        LONGITUDE to String.format(Locale.US, "%.3f", Coordinate.lon),
                        LATITUDE to String.format(Locale.US, "%.3f", Coordinate.lat)
                )
                setFragmentResult(REQUEST_KEY, bundle)
                findNavController().navigateUp()
            }
        }

        // Disable button before marker is placed
        binding.buttonSend.isClickable = false

    }


    /**
     * Function to set up map, click listener etc.
     */
    private fun setUpMapBindings() {

        // Seeings for map
        map.uiSettings.isMapToolbarEnabled = false

        // On long click map place marker
        map.setOnMapLongClickListener { latLng ->

            // Remove previous marker
            if (this::marker.isInitialized) {
                marker.remove()
            }

            // Place marker on map
            marker = map.addMarker(
                    MarkerOptions()
                            .position(latLng)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(38.0F))
            ) as Marker

            // Save coordinate
            Coordinate.apply {
                state = true
                lat = marker.position.latitude
                lon = marker.position.longitude
            }

            // Enable floating action button
            binding.buttonSend.apply {
                alpha = 1f
                isClickable = true
            }
        }
    }


    /**
     * Function to ask for location permission.
     */
    private fun getLocationAccess() {
        if (ContextCompat.checkSelfPermission(
                        requireActivity(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                    requireActivity() as Activity, arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            ), LOCATION_PERMISSION_REQUEST
            )
        }
    }

}