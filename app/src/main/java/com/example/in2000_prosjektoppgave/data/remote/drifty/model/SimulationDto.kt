package com.example.in2000_prosjektoppgave.data.remote.drifty.model

import com.google.gson.annotations.SerializedName


/**
 *   Main simulation data transfer object. This object models the JSON format required
 *   for post request by the Drifty API. See official Schema for details. Some details excluded.
 */
data class SimulationDto(
        @SerializedName("model")
        val model: String = "leeway",

        @SerializedName("geo")
        val geo: Geo,

        @SerializedName("simulationDurationInHours")
        val simulationDurationInHours: Int,

        @SerializedName("configuration")
        val configuration: Configuration
)


/**
 * Data class used by Simulation DTO object to model JSON response from API.
 */
data class Geo(
        @SerializedName("point")
        val point: Point? = null,

        @SerializedName("cone")
        val cone: Cone? = null,

        @SerializedName("polygon")
        val polygon: Polygon? = null
)


/**
 * Data class used by Simulation DTO object to model JSON response from API.
 */
data class Cone(
        @SerializedName("from")
        val from: Point,

        @SerializedName("to")
        val to: Point
)


/**
 * Data class used by Simulation DTO object to model JSON response from API.
 */
data class Point(
        @SerializedName("longitude")
        val longitude: Double,

        @SerializedName("latitude")
        val latitude: Double,

        @SerializedName("radiusInMeters")
        val radiusInMeters: Int? = null,

        @SerializedName("time")
        val time: String
)


/**
 * Data class used by Simulation DTO object to model JSON response from API.
 */
data class Polygon(
        @SerializedName("time")
        val time: String,

        @SerializedName("points")
        val points: List<Points>
)


/**
 * Data class used by Simulation DTO object to model JSON response from API.
 */
data class Points(
        @SerializedName("longitude")
        val longitude: Double,

        @SerializedName("latitude")
        val latitude: Double
)


/**
 * Data class used by Simulation DTO object to model JSON response from API.
 */
data class Configuration(
        @SerializedName("seed")
        val seed: Seed,

        @SerializedName("environment")
        val environment: Environment? = null
)


/**
 * Data class used by Simulation DTO object to model JSON response from API.
 */
data class Seed(
        @SerializedName("object_type")
        val object_type: String
)


/**
 * Data class used by Simulation DTO object to model JSON response from API.
 */
data class Environment(
        @SerializedName("constant")
        val constant: DetailEnvironment? = null,

        @SerializedName("fallback")
        val fallback: DetailEnvironment? = null
)


/**
 * Data class used by Simulation DTO object to model JSON response from API.
 */
data class DetailEnvironment(
        @SerializedName("x_sea_water_velocity")
        val xCurrent: Double? = null,

        @SerializedName("x_wind")
        val xWind: Double? = null,

        @SerializedName("y_sea_water_velocity")
        val yCurrent: Double? = null,

        @SerializedName("y_wind")
        val yWind: Double? = null
)