package com.example.in2000_prosjektoppgave.model

import com.example.in2000_prosjektoppgave.data.local.drifty.SimulationEntity
import com.example.in2000_prosjektoppgave.data.remote.drifty.model.*
import com.example.in2000_prosjektoppgave.util.GeoData
import com.example.in2000_prosjektoppgave.util.convertToX
import com.example.in2000_prosjektoppgave.util.convertToY
import java.text.SimpleDateFormat
import java.util.*


/** Data class for simulation objects, contains information on the object for which a
 * drift simulation is performed.
 *
 * @property name Display name of object.
 * @property nameAlias Object name used by server.
 * @property latitude List of latitude coordinates. Length depends on type property.
 * @property longitude List of latitude coordinates. Length depends on type property.
 * @property radius List of radius for points. Only used by Point and Cone type.
 * @property date List of dates for simulation.
 * @property time List of time instances for simulation.
 * @property simulationLength Length of simulation.
 * @property type Type of simulation. (Point, Polygon or Cone)
 * @property datePerformed Date the simulation was performed.
 * @property curDir Current direction.
 * @property curSpeed Current speed.
 * @property windDir Wind direction.
 * @property windSpeed Wind speed.
 * @property status Status on simulation completion. Where false = failed simulation,
 * true = success, null = simulation in progress.
 * @property path Path for NetCDF file associated with object.
 */
data class SimulationData(
        val name: String,
        val nameAlias: String,
        val latitude: List<Double>,
        val longitude: List<Double>,
        val radius: List<Int>,
        val date: List<String>,
        val time: List<String>,
        val simulationLength: Int,
        val type: String,
        val datePerformed: String = SimpleDateFormat(DATE, Locale.getDefault()).format(Date()),
        val curDir: Double? = null,
        val curSpeed: Double? = null,
        val windDir: Double? = null,
        val windSpeed: Double? = null,
        val status: Boolean? = null,
        val path: String? = null
) {

    /**
     * Converts the object to a data transfer object for the Drifty API.
     */
    fun toDto(type: String) = SimulationDto(
            geo = Geo(
                    point = if (type == POINT) createGeoObject(GeoData.POINT) as Point else null,
                    cone = if (type == CONE) createGeoObject(GeoData.CONE) as Cone else null,
                    polygon = if (type == POLYGON) createGeoObject(GeoData.POLYGON) as Polygon else null
            ),
            simulationDurationInHours = simulationLength,
            configuration = Configuration(
                    seed = Seed(
                            object_type = nameAlias
                    ),
                    environment = Environment(
                            constant = DetailEnvironment(
                                    xCurrent = if (curSpeed != null && curDir != null) convertToX(curSpeed, curDir - 180)  else null,
                                    yCurrent = if (curSpeed != null && curDir != null) convertToY(curSpeed, curDir - 180)  else null,
                                    xWind = if (windSpeed != null && windDir != null) convertToX(windSpeed, windDir)  else null,
                                    yWind = if (windSpeed != null && windDir != null) convertToY(windSpeed, windDir)  else null
                            )
                    )
            )
    )


    /**
     * Converts the object to a database entity object.
     */
    fun toEntity() = SimulationEntity(
            name = name,
            nameAlias = nameAlias,
            latitude = latitude,
            longitude = longitude,
            radius = radius,
            date = date,
            time = time,
            simulationLength = simulationLength,
            type = type,
            datePerformed = datePerformed,
            curDir = curDir,
            curSpeed = curSpeed,
            windDir = windDir,
            windSpeed = windSpeed,
            status = status,
            path = path
    )


    /**
     * Function used to convert between different date formats.
     */
    private fun setDate(date: String, time: String): String {
        val inputFormat = SimpleDateFormat(TIME_INPUT, Locale.getDefault())
        val outputFormat = SimpleDateFormat(TIME_ISO, Locale.getDefault())
        val input = inputFormat.parse("$date $time")
        return if (input is Date) {
            outputFormat.format(input)
        } else {
            ""
        }
    }


    /**
     * Function that returns a Point, Cone or Polygon object.
     */
    private fun createGeoObject(type: GeoData): Any = when (type) {
        GeoData.POINT -> Point(
                longitude = longitude[0],
                latitude = latitude[0],
                radiusInMeters = radius[0],
                time = setDate(date[0], time[0])
        )
        GeoData.CONE -> Cone(
                from = Point(
                        longitude = longitude[0],
                        latitude = latitude[0],
                        radiusInMeters = radius[0],
                        time = setDate(date[0], time[0])
                ),
                to = Point(
                        longitude = longitude[1],
                        latitude = latitude[1],
                        radiusInMeters = radius[1],
                        time = setDate(date[1], time[1])
                )
        )
        GeoData.POLYGON -> Polygon(
                time = setDate(date[0], time[0]),
                points = createPolyPoints()
        )
    }


    /**
     * Function to create a list of points for polygon simulation.
     */
    private fun createPolyPoints(): List<Points> {
        val size = latitude.size
        val list: MutableList<Points> = mutableListOf()
        for (i in 0 until size) {
            list.add(
                    Points(
                            latitude[i],
                            longitude[i]
                    )
            )
        }
        return list
    }


    // Constants
    companion object {
        const val POINT = "Punkt"
        const val CONE = "Kjegle"
        const val POLYGON = "Polygon"
        const val TIME_INPUT = "dd.MM.yyyy HH:mm"
        const val TIME_ISO = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        const val DATE = "dd.MM.yyyy HH:mm"
    }

}