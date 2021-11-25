package com.example.in2000_prosjektoppgave.data.remote.drifty.model

import com.google.gson.annotations.SerializedName


/**
 *   Main simulation response data transfer object. This object models the JSON response
 *   from the Drifty API. See official Schema for details. Some details are excluded.
 */
data class DriftyResponseDto(
        @SerializedName("result")
        val result: Result
)


/**
 * Data class used by Drifty Response DTO to model JSON response from API.
 */
data class Result(
        @SerializedName("status")
        val status: Status,

        @SerializedName("files")
        val files: Files
)


/**
 * Data class used by Drifty Response DTO to model JSON response from API.
 */
data class Files(
        @SerializedName("main")
        val main: String?,

        @SerializedName("plot")
        val plot: String?,

        @SerializedName("log")
        val log: String?
)


/**
 * Data class used by Drifty Response DTO to model JSON response from API.
 */
data class Status(
        @SerializedName("success")
        val success: Boolean,

        @SerializedName("error")
        val error: String?
)