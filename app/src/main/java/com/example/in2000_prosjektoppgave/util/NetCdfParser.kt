@file:Suppress("unused")

package com.example.in2000_prosjektoppgave.util

import com.example.in2000_prosjektoppgave.R
import com.example.in2000_prosjektoppgave.model.DataPoint
import com.example.in2000_prosjektoppgave.model.Trajectory
import com.example.in2000_prosjektoppgave.model.TrajectoryWithDetails
import com.example.in2000_prosjektoppgave.ui.MainApplication
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import ucar.nc2.dataset.NetcdfDataset
import ucar.nc2.dataset.NetcdfDatasets

/**
 * Class with access to local files directory used  to parse NetCDF files, into
 * a domain model.
 *
 * @property app Application context.
 */
class NetCdfParser(private val app: MainApplication) {

    // Constants
    companion object {
        const val TIME = "time"
        const val TRAJECTORY = "trajectory"
        const val LAT = "lat"
        const val LON = "lon"
        const val STATUS = "status"
        const val YES = "Ja"
        const val NO = "Nei"
    }

    /**
     * Function used to parse a NetCDF file and create a array of domain models.
     *
     * @param fileName File name for NetCDF file.
     * @return Array of trajectory objects, with only polylines.
     */
    fun getData(fileName: String): List<Trajectory> {

        // File information
        val basePath = app.filesDir.absolutePath
        val filePath = "$basePath$fileName"

        // Net Cdf data set
        var netCdf: NetcdfDataset? = null


        try {

            // Open file and create a data set
            netCdf = NetcdfDatasets.openDataset(filePath)

            // List of trajectory objects
            val simulationList = mutableListOf<Trajectory>()

            // Get the total time of the simulation
            val totTime: Int = netCdf.findVariable(TIME)?.getShape(0) ?: -1

            // Gets the numbers of trajectories in the simulation
            val totTrajectory: Int = netCdf?.findVariable(TRAJECTORY)?.getShape(0) ?: -1


            // Check if variables found
            if (totTime != -1 && totTrajectory != -1) {

                // Iterate over all trajectories
                for (i in 0 until totTrajectory) {

                    // Defines where to start to read
                    val origin = intArrayOf(i, 0)

                    // Defines how many steps to read
                    val size = intArrayOf(1, totTime - 1)

                    // Gets all the relevant information from file
                    val lat = netCdf.findVariable(LAT)!!.read(origin, size)
                    val lon = netCdf.findVariable(LON)!!.read(origin, size)
                    val status = netCdf.findVariable(STATUS)!!.read(origin, size)


                    // Temp data structures
                    val polyLine = PolylineOptions()

                    // Write information about trajectory to object
                    for (index in 0 until totTime - 1) {

                        val stranded = status.getInt(index) != 0
                        val longitude = lon.getDouble(index)
                        val latitude = lat.getDouble(index)

                        // Polyline creation
                        polyLine.add(LatLng(latitude, longitude))

                        // Stop iteration if object is stranded
                        if (stranded) break
                    }

                    // Add trajectory to array
                    simulationList.add(
                            Trajectory(
                                    trajectoryNr = i,
                                    polyLine = polyLine
                            )
                    )
                }
            }

            // Return empty array upon missing information
            return simulationList


        } catch (e: Exception) {

            // Return empty error on fail
            return emptyList()

        } finally {

            // Close file
            netCdf?.close()
        }

    }

    /**
     * Function used to parse a NetCDF file and create a array of trajectories with
     * information about polylines, markers and data points from the netCdf file.
     * This function is currently unused due to performance, but not removed.
     *
     * @param fileName File name for NetCDF file.
     * @return Array of trajectoryWithDetails objects, with polylines, markers and data points.
     */
    fun getDetailedData(fileName: String): List<TrajectoryWithDetails> {

        // File information
        val basePath = app.filesDir.absolutePath
        val filePath = "$basePath$fileName"

        // Net Cdf data set
        var netCdf: NetcdfDataset? = null


        try {

            // Open file and create a data set
            netCdf = NetcdfDatasets.openDataset(filePath)

            // List of trajectory objects
            val simulationList = mutableListOf<TrajectoryWithDetails>()

            // Get the total time of the simulation
            val totTime: Int = netCdf.findVariable(TIME)?.getShape(0) ?: -1

            // Gets the numbers of trajectories in the simulation
            val totTrajectory: Int = netCdf?.findVariable(TRAJECTORY)?.getShape(0) ?: -1


            // Check if variables found
            if (totTime != -1 && totTrajectory != -1) {

                // Iterate over all trajectories
                for (i in 0 until totTrajectory) {

                    // Defines where to start to read
                    val origin = intArrayOf(i, 0)

                    // Defines how many steps to read
                    val size = intArrayOf(1, totTime - 1)

                    // Gets all the relevant information from file
                    val lat = netCdf.findVariable(LAT)!!.read(origin, size)
                    val lon = netCdf.findVariable(LON)!!.read(origin, size)
                    val time = netCdf.findVariable(TIME)!!.read(intArrayOf(0), intArrayOf(totTime - 1))
                    val status = netCdf.findVariable(STATUS)!!.read(origin, size)


                    // Temp data structures
                    val dataPoints = mutableListOf<DataPoint>()
                    val polyLine = PolylineOptions()
                    val markers = mutableListOf<MarkerOptions>()

                    // Write information about trajectory to object
                    for (index in 0 until totTime - 1) {

                        val stranded = status.getInt(index) != 0
                        val longitude = lon.getDouble(index)
                        val latitude = lat.getDouble(index)

                        // Polyline creation
                        polyLine.add(LatLng(latitude, longitude))

                        // Data point information
                        dataPoints.add(
                                DataPoint(
                                        time.getInt(index),
                                        lon.getFloat(index),
                                        lat.getFloat(index),
                                        stranded
                                )
                        )

                        // Create markers
                        markers.add(
                                MarkerOptions()
                                        .position(LatLng(latitude, longitude))
                                        .title(
                                                app.resources.getString(
                                                        R.string.simulation_marker_title,
                                                        index,
                                                        time.toString()
                                                )
                                        )
                                        .snippet(
                                                app.resources.getString(
                                                        R.string.simulation_marker_lat,
                                                        latitude
                                                )
                                        )
                                        .snippet(
                                                app.resources.getString(
                                                        R.string.simulation_marker_lon,
                                                        longitude
                                                )
                                        )
                                        .snippet(
                                                app.resources.getString(
                                                        R.string.simulation_marker_stranded,
                                                        if (stranded) YES else NO
                                                )
                                        )
                        )

                        // Stop iteration if object is stranded
                        if (stranded) break
                    }

                    // Add trajectory to array
                    simulationList.add(
                            TrajectoryWithDetails(
                                    trajectoryNr = i,
                                    dataPoints = dataPoints as List<DataPoint>,
                                    polyLine = polyLine,
                                    markers = markers as List<MarkerOptions>
                            )
                    )
                }
            }

            // Return empty array upon missing information
            return simulationList


        } catch (e: Exception) {

            // Return empty error on fail
            return emptyList()

        } finally {

            // Close file
            netCdf?.close()
        }

    }

}