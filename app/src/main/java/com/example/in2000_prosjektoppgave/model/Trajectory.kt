package com.example.in2000_prosjektoppgave.model

import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions


/**
 * Data class to hold information about trajectories used in visualization
 * of Drifty simulation.
 *
 * @property trajectoryNr Id number for the trajectory.
 * @property polyLine Polyline object for google maps.
 */
data class Trajectory(
        val trajectoryNr: Int,
        val polyLine: PolylineOptions
)

/**
 * Data class to hold detailed information about trajectories used in visualization
 * of Drifty simulation.
 *
 * @property trajectoryNr Id number for the trajectory.
 * @property dataPoints List of Data point objects.
 * @property polyLine Polyline object for google maps.
 * @property markers List of Google maps markers.
 */
data class TrajectoryWithDetails(
        val trajectoryNr: Int,
        val dataPoints: List<DataPoint>,
        val polyLine: PolylineOptions,
        val markers: List<MarkerOptions>
)

/**
 * Data class for data points. Describes position in space and time for objects.
 *
 * @property time Time instant.
 * @property lon Longitude coordinate.
 * @property lat Latitude coordinate.
 * @property stranded Status if object is stranded.
 */
data class DataPoint(
        val time: Int,
        val lon: Float,
        val lat: Float,
        val stranded: Boolean
)

