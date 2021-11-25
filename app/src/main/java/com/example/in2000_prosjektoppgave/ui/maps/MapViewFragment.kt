package com.example.in2000_prosjektoppgave.ui.maps

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.in2000_prosjektoppgave.databinding.FragmentMapViewBinding
import com.example.in2000_prosjektoppgave.R
import com.example.in2000_prosjektoppgave.util.Coordinate
import com.example.in2000_prosjektoppgave.util.Resource
import com.example.in2000_prosjektoppgave.util.convertDDtoDMSString
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


/**
 * Map View fragment: Used to display Drifty simulation results.
 */
@AndroidEntryPoint
class MapViewFragment : Fragment() {


    // Constants
    companion object {
        const val LOCATION_PERMISSION_REQUEST = 1
    }

    // View model for NetCDF data
    private val viewModel: MapViewModel by viewModels()

    // Binding for views
    private var _binding: FragmentMapViewBinding? = null
    private val binding get() = _binding!!

    // Google Map and marker
    private lateinit var map: GoogleMap
    private lateinit var marker: Marker


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMapViewBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.googleMap.onCreate(savedInstanceState)
        binding.googleMap.onResume()

        binding.googleMap.getMapAsync {
            map = it

            // Request location permission
            getLocationAccess()

            // Sets up observers
            setUpObservers()

            // Set up map
            setUpMapBinding()
            setUpBinding()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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


    /**
     * Function to set up map, click listener etc.
     */
    private fun setUpMapBinding() {

        // Seeings for map
        map.uiSettings.isMapToolbarEnabled = false

        // Change color of poly lines on click
        map.setOnPolylineClickListener {
            it.color = it.color xor 0x00ffffff
        }

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
                            .icon(BitmapDescriptorFactory.defaultMarker(37.5F))
                            .title(resources.getString(R.string.position))
                        .icon(BitmapDescriptorFactory.defaultMarker(38.0F))
                        .snippet(
                                    resources.getString(R.string.show_coordinates,
                                            convertDDtoDMSString(latLng.latitude, Coordinate.LATITUDE),
                                            convertDDtoDMSString(latLng.longitude, Coordinate.LONGITUDE)
                                    )

                            )
            ) as Marker

        }

    }


    /**
     * Set up binding, button click listeners etc
     */
    private fun setUpBinding() {

        // Change visibility of all poly lines on click
        binding.buttonActions.setOnClickListener {
            viewModel.changeVisibility(binding.buttonActions)
        }
    }


    /**
     * Sets up observers for live data.
     */
    private fun setUpObservers() {
        viewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {

                    // If success hide progress bar
                    binding.progressBar.visibility = View.GONE

                    // Draw poly lines on map and zoom to location
                    viewModel.viewSimulation(map)
                    zoomToSimulation(viewModel.getBounds())
                }
                is Resource.Error -> {

                    // If failed hide progress bar and show error message
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(
                            requireActivity().findViewById(android.R.id.content),
                            it.message ?: resources.getString(R.string.generic_error),
                            Snackbar.LENGTH_LONG
                    ).show()
                }
                is Resource.Loading -> {

                    // While parsing show progress bar
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }


    /**
     * Function that zooms to location of a simulation.
     */
    private fun zoomToSimulation(bounds: LatLngBounds) =
            map.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                            bounds,
                            20
                    )
            )


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
