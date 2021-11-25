package com.example.in2000_prosjektoppgave.data.remote.met.model.weather

import com.example.in2000_prosjektoppgave.model.DataForecast
import com.example.in2000_prosjektoppgave.model.Forecast
import com.google.gson.annotations.SerializedName


/**
 *     Main Forecast data transfer object. This object models the JSON response
 *     from the Met forecast API. See official Schema for details.
 */
data class ForecastDto(
		@SerializedName("geometry")
		val geometry: Geometry,

		@SerializedName("type")
		val type: String,

		@SerializedName("properties")
		val properties: Properties
) {

    /**
     * Function that converts the object Forecast domain model.
     */
    fun toDomain() = Forecast(
			updated = this.properties.meta.updated_at,
			longitude = geometry.coordinates[0],
			latitude = geometry.coordinates[1],
			timeSeries = this.properties.timeseries.map {
				DataForecast(
						time = it.time,
						windDir = it.data.instant.details.wind_from_direction,
						windSpeed = it.data.instant.details.wind_speed,
						temperature = it.data.instant.details.air_temperature,
						symbol = it.data.next_1_hours?.summary?.symbol_code
				)
			}
	)
}


/**
 * Data class used by Forecast DTO to model JSON response from Met API.
 */
data class Geometry(
		@SerializedName("type")
		val type: String,

		@SerializedName("coordinates")
		val coordinates: List<Double>
)


/**
 * Data class used by Forecast DTO to model JSON response from Met API.
 */
data class Properties(
		@SerializedName("timeseries")
		val timeseries: List<Timeseries>,

		@SerializedName("meta")
		val meta: Meta
)


/**
 * Data class used by Forecast DTO to model JSON response from Met API.
 */
data class Meta(
		@SerializedName("updated_at")
		val updated_at: String,

		@SerializedName("units")
		val units: Units
)


/**
 * Data class used by Forecast DTO to model JSON response from Met API.
 */
data class Units(
		@SerializedName("cloud_area_fraction")
		val cloud_area_fraction: String?,

		@SerializedName("wind_speed_of_gust")
		val wind_speed_of_gust: String?,

		@SerializedName("air_pressure_at_sea_level")
		val air_pressure_at_sea_level: String?,

		@SerializedName("wind_from_direction")
		val wind_from_direction: String?,

		@SerializedName("cloud_area_fraction_high")
		val cloud_area_fraction_high: String?,

		@SerializedName("probability_of_thunder")
		val probability_of_thunder: String?,

		@SerializedName("probability_of_precipitation")
		val probability_of_precipitation: String?,

		@SerializedName("air_temperature_min")
		val air_temperature_min: String?,

		@SerializedName("cloud_area_fraction_low")
		val cloud_area_fraction_low: String?,

		@SerializedName("air_temperature_max")
		val air_temperature_max: String?,

		@SerializedName("wind_speed")
		val wind_speed: String?,

		@SerializedName("precipitation_amount_min")
		val precipitation_amount_min: String?,

		@SerializedName("ultraviolet_index_clear_sky_max")
		val ultraviolet_index_clear_sky_max: String?,

		@SerializedName("precipitation_amount")
		val precipitation_amount: String?,

		@SerializedName("fog_area_fraction")
		val fog_area_fraction: String?,

		@SerializedName("dew_point_temperature")
		val dew_point_temperature: String?,

		@SerializedName("precipitation_amount_max")
		val precipitation_amount_max: String?,

		@SerializedName("cloud_area_fraction_medium")
		val cloud_area_fraction_medium: String?,

		@SerializedName("relative_humidity")
		val relative_humidity: String?,

		@SerializedName("air_temperature")
		val air_temperature: String?
)


/**
 * Data class used by Forecast DTO to model JSON response from Met API.
 */
data class Timeseries(
		@SerializedName("data")
		val data: Data,

		@SerializedName("time")
		val time: String
)


/**
 * Data class used by Forecast DTO to model JSON response from Met API.
 */
data class Data(
		@SerializedName("instant")
		val instant: Instant,

		@SerializedName("next_1_hours")
		val next_1_hours: NextHour?,

		@SerializedName("next_6_hours")
		val next_6_hours: NextHour?,

		@SerializedName("next_12_hours")
		val next_12_hours: NextHour?
)


/**
 * Data class used by Forecast DTO to model JSON response from Met API.
 */
data class Instant(
		@SerializedName("details")
		val details: DetailsInstant
)


/**
 * Data class used by Forecast DTO to model JSON response from Met API.
 */
data class DetailsInstant(
		@SerializedName("cloud_area_fraction_medium")
		val cloud_area_fraction_medium: Double?,

		@SerializedName("dew_point_temperature")
		val dew_point_temperature: Double?,

		@SerializedName("cloud_area_fraction_high")
		val cloud_area_fraction_high: Double?,

		@SerializedName("relative_humidity")
		val relative_humidity: Double?,

		@SerializedName("wind_speed")
		val wind_speed: Double?,

		@SerializedName("air_temperature")
		val air_temperature: Double?,

		@SerializedName("cloud_area_fraction_low")
		val cloud_area_fraction_low: Double?,

		@SerializedName("wind_speed_of_gust")
		val wind_speed_of_gust: Double?,

		@SerializedName("cloud_area_fraction")
		val cloud_area_fraction: Double?,

		@SerializedName("fog_area_fraction")
		val fog_area_fraction: Double?,

		@SerializedName("wind_from_direction")
		val wind_from_direction: Double?,

		@SerializedName("air_pressure_at_sea_level")
		val air_pressure_at_sea_level: Double?
)


/**
 * Data class used by Forecast DTO to model JSON response from Met API.
 */
data class NextHour(
		@SerializedName("details")
		val details: DetailsNext,

		@SerializedName("summary")
		val summary: Summary
)


/**
 * Data class used by Forecast DTO to model JSON response from Met API.
 */
data class DetailsNext(
		@SerializedName("air_temperature_max")
		val air_temperature_max: Double?,

		@SerializedName("precipitation_amount_min")
		val precipitation_amount_min: Double?,

		@SerializedName("probability_of_precipitation")
		val probability_of_precipitation: Double?,

		@SerializedName("probability_of_thunder")
		val probability_of_thunder: Double?,

		@SerializedName("air_temperature_min")
		val air_temperature_min: Double?,

		@SerializedName("precipitation_amount_max")
		val precipitation_amount_max: Double?,

		@SerializedName("ultraviolet_index_clear_sky_max")
		val ultraviolet_index_clear_sky_max: Double?,

		@SerializedName("precipitation_amount")
		val precipitation_amount: Double?

)


/**
 * Data class used by Forecast DTO to model JSON response from Met API.
 */
data class Summary(
		@SerializedName("symbol_code")
		val symbol_code: String
)