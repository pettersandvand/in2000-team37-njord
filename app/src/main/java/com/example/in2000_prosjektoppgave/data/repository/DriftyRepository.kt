package com.example.in2000_prosjektoppgave.data.repository

import com.example.in2000_prosjektoppgave.data.local.drifty.DriftyLocalSource
import com.example.in2000_prosjektoppgave.data.remote.drifty.DriftyRemoteSource
import com.example.in2000_prosjektoppgave.data.remote.drifty.model.SimulationDto
import com.example.in2000_prosjektoppgave.model.SimulationData
import okhttp3.ResponseBody


/**
 * Class for Drifty repository. Collects functions needed for persistence, and communication
 * with Drifty API.
 *
 * @property remoteSource A Drifty remote source object.
 * @property localeSource A Drifty local source object.
 */
class DriftyRepository(
        private val remoteSource: DriftyRemoteSource,
        private val localeSource: DriftyLocalSource
) {

    /**
     * Function used to return a flow containing all simulation objects in the database.
     */
    fun getDatabaseData() = localeSource.getAllData()


    /**
     * Function used to insert simulation objects into database.
     */
    suspend fun insert(simulation: SimulationData) = localeSource.insert(simulation.toEntity())


    /**
     * Function used to delete all persisted files and objects in database.
     */
    suspend fun deleteAll() = localeSource.deleteAll()


    /**
     * Function used to save file and update path in database.
     *
     * @param id Primary key of object in database.
     * @param body A NetCDF file in ResponseBody format.
     */
    suspend fun insertFile(id: Int, body: ResponseBody) = localeSource.insertFile(id, body)


    /**
     * Function used to run a simulation on server.
     *
     * @param simulation A simulation DTO object.
     */
    suspend fun runSimulation(simulation: SimulationDto) = remoteSource.post(simulation)


    /**
     * Function used to get a single object from the database.
     *
     * @param id Primary key of object.
     */
    suspend fun getSimulationObject(id: Int) = localeSource.getSimulationObject(id)?.toDomain()


    /**
     * Function used to update database on failed simulation retrieval.
     */
    suspend fun updateFail(id : Int) = localeSource.updateFail(id)

}