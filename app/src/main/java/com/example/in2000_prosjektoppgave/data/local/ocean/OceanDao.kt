package com.example.in2000_prosjektoppgave.data.local.ocean

import androidx.room.*
import com.example.in2000_prosjektoppgave.model.Ocean


/**
 * Simulation data access object for Ocean data objects. That can be use to query, update,
 * insert, and delete data in the database.
 */
@Dao
abstract class OceanDao {

    /**
     * Function to insert a domain model properly into the database.
     */
    suspend fun insert(ocean: Ocean, search: String) {

        // Create the different entities
        val oceanEntity: OceanEntity = ocean.toOceanEntity(search)
        val dataEntity: List<DataOceanEntity> = ocean.toOceanDataEntity(oceanEntity.id)

        // Insert Ocean entity
        insertOcean(oceanEntity)

        // Insert all Ocean data entities
        insertData(dataEntity)
    }


    /**
     * Function to insert a Ocean entity.
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertOcean(oceanEntity: OceanEntity)


    /**
     * Function to insert a list of Ocean data entities.
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertData(dataOceanEntity: List<DataOceanEntity>)


    /**
     * Function to delete all ocean data in database.
     */
    @Query("DELETE FROM OceanEntity")
    abstract suspend fun deleteAll()


    /**
     * Function to fetch Ocean data for a single point.
     */
    @Transaction
    @Query("SELECT * FROM OceanEntity WHERE search = :id")
    abstract fun getData(id: String): OceanWithData?

}