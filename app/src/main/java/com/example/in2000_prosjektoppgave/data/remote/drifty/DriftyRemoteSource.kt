package com.example.in2000_prosjektoppgave.data.remote.drifty

import com.example.in2000_prosjektoppgave.data.remote.drifty.model.*
import com.example.in2000_prosjektoppgave.util.Resource
import com.skydoves.sandwich.*
import okhttp3.ResponseBody


/**
 * Class used to communicate with Drifty API and handle errors and exception.
 *
 * @property webService A retrofit service.
 */
class DriftyRemoteSource(
        private val webService: DriftyService
) {

    // Constants
    companion object {
        const val UNKNOWN_ERROR = "Ukjent feil"
        const val FAILED_SIMULATION = "Simulering mislykket: %s"
        const val X_ID = "x-id"
    }

    /**
     * Function used to post an object to the server. The object is referred to as a seed,
     * and will start a simulation.
     *
     * @param simulation A simulation DTO object.
     */
    suspend fun post(simulation: SimulationDto): Resource<ResponseBody> {

        // Generic response
        var out: Resource<ResponseBody> = Resource.Error(message = UNKNOWN_ERROR)

        webService.postLeeway(simulation).suspendOnSuccess {

            // If status code is 202 and and an simulation id is provided continue
            if (statusCode.code == 202 && headers[X_ID] != null) {
                out = get(headers[X_ID]!!)
            }

        }.suspendOnError {

            // On error
            out = Resource.Error(message = (message()))

        }.suspendOnException {

            // On exception
            out = Resource.Error(message = (message()))

        }
        return out
    }


    /**
     * Function used to retrieve results from simulation.
     * The response body contains information on simulation success or failure.
     * File locations are provided on success.
     *
     * @param id An id used by the Drifty API to distinguish between simulations.
     */
    private suspend fun get(id: String): Resource<ResponseBody> {

        // Generic response
        var out: Resource<ResponseBody> = Resource.Error(message = UNKNOWN_ERROR)

        webService.getSimulation(id).suspendOnSuccess {

            // If main body / data is non empty
            if (data != null) {

                // Check if server side simulation was success
                out = if (data!!.result.status.success) {

                    // If simulation on server was successful continue
                    download(data!!.result.files.main!!)

                } else {

                    // If simulation on server failed
                    Resource.Error(message = String.format(FAILED_SIMULATION, data!!.result.status.error))
                }
            }

        }.suspendOnError {

            // On error
            out = Resource.Error(message = (message()))

        }.suspendOnException {

            // On exception
            out = Resource.Error(message = (message()))

        }
        return out
    }


    /**
     * Function used to download NetCDF results from server.
     *
     * @param id An id used by the Drifty API to distinguish between simulations.
     */
    private suspend fun download(id: String): Resource<ResponseBody> {

        // Generic response
        var out: Resource<ResponseBody> = Resource.Error(message = UNKNOWN_ERROR)

        webService.getNetCdf(id).suspendOnSuccess {

            // If main body / data is non empty continue
            if (data != null) {
                out = Resource.Success(data = data)
            }

        }.suspendOnError {

            // On error
            out = Resource.Error(message = (message()))

        }.suspendOnException {

            // On exception
            out = Resource.Error(message = (message()))
        }
        return out
    }

}