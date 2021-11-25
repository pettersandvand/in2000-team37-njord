package com.example.in2000_prosjektoppgave.data.local.drifty

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


/**
 * Simulation data access object for Drifty Simulation data. That can be use to query,
 * update, insert, and delete data in the database.
 */
@Dao
interface SimulationDao {

    /**
     * Flow containing all simulation entity objects in database.
     */
    @Query("SELECT * FROM SimulationEntity")
    fun getAll(): Flow<List<SimulationEntity>>


    /**
     * Function to fetch a single simulation object, using its primary key.
     */
    @Query("SELECT * FROM SimulationEntity WHERE id = :id")
    suspend fun getSingle(id: Int): SimulationEntity?


    /**
     * Function to insert a simulation object in the database.
     */
    @Insert
    suspend fun insert(sim: SimulationEntity): Long


    /**
     * Function to delete all simulation object in the database.
     */
    @Query("DELETE FROM SimulationEntity")
    suspend fun deleteAll()


    /**
     * Function to update a single simulation object with a reference to its associated
     * NetCDF file.
     *
     * @param id Primary key of object.
     * @param path Name of NetCDF file.
     * @param status Status of simulation.
     */
    @Query("UPDATE SimulationEntity SET path = :path, status = :status WHERE id = :id")
    suspend fun updatePath(id: Int, path: String, status: Boolean)


    /**
     * Function to update a single simulation object with a failed simulation status.
     *
     * @param id Primary key of object.
     * @param status Status of simulation.
     */
    @Query("UPDATE SimulationEntity SET status = :status WHERE id = :id")
    suspend fun updateFail(id: Int, status: Boolean)

}