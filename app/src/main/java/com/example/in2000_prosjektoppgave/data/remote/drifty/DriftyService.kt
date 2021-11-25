package com.example.in2000_prosjektoppgave.data.remote.drifty

import com.example.in2000_prosjektoppgave.data.remote.drifty.model.SimulationDto
import com.example.in2000_prosjektoppgave.data.remote.drifty.model.DriftyResponseDto
import com.skydoves.sandwich.ApiResponse
import okhttp3.ResponseBody
import retrofit2.http.*


/**
 * Retrofit service interface with functions for Drifty API.
 */
interface DriftyService {

    // Constants
    companion object {
        const val BASE_URL_DRIFTY = "https://in2000.drifty.met.no"
        const val LEEWAY = "/api/leeway/v1"
        const val SIMULATION = "/api/simulation/{id}"
        const val ID = "id"
        const val USERNAME = "ADDUSERNAMEHERE"
        const val PASSWORD = "ADDPASSWORDHERE"
        const val AUTHORIZATION = "Authorization"

    }

    /**
     * Posts a simulation data object to the server. Only headers of response is used.
     *
     * @param body A simulation DTO object.
     */
    @POST(LEEWAY)
    suspend fun postLeeway(
            @Body body: SimulationDto
    ): ApiResponse<ResponseBody>


    /**
     * Gets the simulation results and meta data. Used to get URL for server NetCDF file.
     *
     * @param id An id used by the Drifty API to distinguish between simulations.
     * @return A Drifty respond DTO.
     */
    @GET(SIMULATION)
    suspend fun getSimulation(
            @Path(ID) id: String
    ): ApiResponse<DriftyResponseDto>


    /**
     * Get request used to download a NetCDF file. Streaming is used due to large size.
     *
     * @param url URL for NetCDF download.
     * @return A generic ResponseBody wrapped in an ApiResponse.
     */
    @Streaming
    @GET
    suspend fun getNetCdf(
            @Url url: String
    ): ApiResponse<ResponseBody>

}