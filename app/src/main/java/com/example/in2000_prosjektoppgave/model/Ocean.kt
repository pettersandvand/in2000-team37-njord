package com.example.in2000_prosjektoppgave.model

import com.example.in2000_prosjektoppgave.data.local.ocean.DataOceanEntity
import com.example.in2000_prosjektoppgave.data.local.ocean.OceanEntity


/**
 * Data class for ocean objects, contains detailed information about conditions at sea at a
 * specific location.
 *
 * @property updated Last time the data was updated.
 * @property longitude Longitude coordinate.
 * @property latitude Latitude coordinate.
 * @property time_series List of Data Ocean objects.
 */
data class Ocean(
    val updated: String,
    val longitude: Double,
    val latitude: Double,
    val time_series: List<DataOcean>
) {

    /**
     * Extracts needed information for database insertion of Ocean Entity.
     */
    fun toOceanEntity(search: String) = OceanEntity(
        id = longitude.toString() + latitude.toString(),
        updated = updated,
        longitude = longitude,
        latitude = latitude,
        search = search
    )


    /**
     * Extracts needed information for database insertion of Data Ocean Entity.
     */
    fun toOceanDataEntity(id: String) = this.time_series.map {
        DataOceanEntity(
            oceanId = id,
            time = it.time,
            waveDirection = it.waveDirection,
            waveHeight = it.waveHeight,
            currentSpeed = it.currentSpeed,
            temperature = it.temperature,
            currentDirection = it.currentDirection
        )
    }

}


/**
 * Data class containing information about ocean conditions at a single point in time, at
 * a specific location.
 *
 * @property time Time instant.
 * @property waveDirection Wave direction in degrees.
 * @property waveHeight Significant wave height.
 * @property currentSpeed Current speed.
 * @property temperature Temperature of sea water.
 * @property currentDirection Direction of currents.
 */
data class DataOcean(
    val time: String,
    val waveDirection: Double?,
    val waveHeight: Double?,
    val currentSpeed: Double?,
    val temperature: Double?,
    val currentDirection: Double?
)