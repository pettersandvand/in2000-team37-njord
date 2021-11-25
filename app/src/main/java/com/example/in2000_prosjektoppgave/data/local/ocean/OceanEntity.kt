package com.example.in2000_prosjektoppgave.data.local.ocean

import androidx.room.*
import com.example.in2000_prosjektoppgave.model.Ocean
import com.example.in2000_prosjektoppgave.model.DataOcean


/**
 * Data class used by database for storage of ocean data objects.
 *
 * @property id Primary key, coordinates.
 * @property updated Date for when the data was updated server side.
 * @property longitude Real longitude coordinate.
 * @property latitude Real latitude coordinate.
 * @property search The coordinates used when searching in app. Met service snaps to
 * different coordinates.
 */
@Entity
data class OceanEntity(
        @PrimaryKey
        var id: String,
        var updated: String,
        var longitude: Double,
        var latitude: Double,
        var search: String = ""
)


/**
 * Data class used by database to store information about ocean conditions at a single point in
 * time, at a specific location.
 *
 * @property dataId Primary key, autogenerated.
 * @property oceanId Foreign key.
 * @property time Time instant.
 * @property waveDirection Direction of waves.
 * @property waveHeight Significant wave height.
 * @property currentSpeed Speed of current.
 * @property temperature Water temperature.
 * @property currentDirection Direction of current.
 */
@Entity(
        foreignKeys = [ForeignKey(
                entity = OceanEntity::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("oceanId"),
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        )],
        indices = [Index(value = ["oceanId"])]
)
data class DataOceanEntity(
        var oceanId: String,
        @PrimaryKey(autoGenerate = true)
        var dataId: Int = 0,
        var time: String,
        var waveDirection: Double? = null,
        var waveHeight: Double? = null,
        var currentSpeed: Double? = null,
        var temperature: Double? = null,
        var currentDirection: Double? = null
)


/**
 * Data class for relation modeling, one to many between ocean entity and data ocean entity.
 */
data class OceanWithData(
        @Embedded val oceanEntity: OceanEntity,
        @Relation(
                parentColumn = "id",
                entityColumn = "oceanId"
        )
        val data: List<DataOceanEntity>
) {

    /**
     * Function used to convert a OceanWithData object to a domain model.
     */
    fun toDomain() = Ocean(
            updated = oceanEntity.updated,
            longitude = oceanEntity.longitude,
            latitude = oceanEntity.latitude,
            time_series = data.map {
                DataOcean(
                        time = it.time,
                        waveDirection = it.waveDirection,
                        waveHeight = it.waveHeight,
                        currentSpeed = it.currentSpeed,
                        temperature = it.temperature,
                        currentDirection = it.currentDirection
                )
            }
    )

}