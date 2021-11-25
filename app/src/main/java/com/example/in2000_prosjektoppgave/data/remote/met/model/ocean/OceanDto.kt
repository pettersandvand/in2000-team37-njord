package com.example.in2000_prosjektoppgave.data.remote.met.model.ocean

import com.example.in2000_prosjektoppgave.model.DataOcean
import com.example.in2000_prosjektoppgave.model.Ocean
import com.google.gson.annotations.SerializedName


/**
 *   Main Ocean forecast data transfer object. This object models the JSON response
 *   from the Met ocean forecast API. See official Schema for details.
 */
data class OceanDto(
		@SerializedName("type")
		val type: String,

		@SerializedName("geometry")
		val geometry: Geometry,

		@SerializedName("properties")
		val properties: Properties
) {

    /**
     * Function that converts the object Forecast domain model.
     */
    fun toDomain() = Ocean(
			updated = this.properties.meta.updated_at,
			longitude = geometry.coordinates[0],
			latitude = geometry.coordinates[1],
			time_series = this.properties.timeseries.map {
				DataOcean(
						time = it.time,
						waveDirection = it.data.instant.details.sea_surface_wave_from_direction,
						waveHeight = it.data.instant.details.sea_surface_wave_height,
						currentSpeed = it.data.instant.details.sea_water_speed,
						temperature = it.data.instant.details.sea_water_temperature,
						currentDirection = it.data.instant.details.sea_water_to_direction
				)
			}
	)
}


/**
 * Data class used by Ocean DTO to model JSON response from Met API.
 */
data class Geometry(
		@SerializedName("type")
		val type: String,

		@SerializedName("coordinates")
		val coordinates: List<Double>
)


/**
 * Data class used by Ocean DTO to model JSON response from Met API.
 */
data class Properties(
		@SerializedName("meta")
		val meta: Meta,

		@SerializedName("timeseries")
		val timeseries: List<Timeseries>
)


/**
 * Data class used by Ocean DTO to model JSON response from Met API.
 */
data class Meta(
		@SerializedName("updated_at")
		val updated_at: String,

		@SerializedName("units")
		val units: Units
)


/**
 * Data class used by Ocean DTO to model JSON response from Met API.
 */
data class Units(
		@SerializedName("sea_surface_wave_from_direction")
		val sea_surface_wave_from_direction: String,

		@SerializedName("sea_surface_wave_height")
		val sea_surface_wave_height: String,

		@SerializedName("sea_water_speed")
		val sea_water_speed: String,

		@SerializedName("sea_water_temperature")
		val sea_water_temperature: String,

		@SerializedName("sea_water_to_direction")
		val sea_water_to_direction: String
)


/**
 * Data class used by Ocean DTO to model JSON response from Met API.
 */
data class Timeseries(
		@SerializedName("time")
		val time: String,

		@SerializedName("data")
		val data: Data
)


/**
 * Data class used by Ocean DTO to model JSON response from Met API.
 */
data class Data(
		@SerializedName("instant")
		val instant: Instant
)


/**
 * Data class used by Ocean DTO to model JSON response from Met API.
 */
data class Instant(
		@SerializedName("details")
		val details: Details
)


/**
 * Data class used by Ocean DTO to model JSON response from Met API.
 */
data class Details(
		@SerializedName("sea_surface_wave_from_direction")
		val sea_surface_wave_from_direction: Double?,

		@SerializedName("sea_surface_wave_height")
		val sea_surface_wave_height: Double?,

		@SerializedName("sea_water_speed")
		val sea_water_speed: Double?,

		@SerializedName("sea_water_temperature")
		val sea_water_temperature: Double?,

		@SerializedName("sea_water_to_direction")
		val sea_water_to_direction: Double?
)