package com.example.in2000_prosjektoppgave.data.local.drifty

import android.util.Log
import com.example.in2000_prosjektoppgave.model.SimulationData
import com.example.in2000_prosjektoppgave.ui.MainApplication
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Exception


/**
 * Class to persist local date associated with Drifty Simulations. The class handles
 * files and database operations.
 *
 * @property app Application context.
 * @property database A simulation data access object.
 */
class DriftyLocalSource(
        private val app: MainApplication,
        private val database: SimulationDao
) {

    // Constants
    companion object{
        const val FILE_FORMAT = "/%s.nc"
        const val FILE_TYPE = ".nc"
        const val FILE_ERROR = "File error"
    }

    /**
     * Function to insert a simulation object into the database.
     */
    suspend fun insert(simulation: SimulationEntity) = database.insert(simulation)


    /**
     * Function to fetch a single simulation object from the database.
     */
    suspend fun getSimulationObject(id: Int) = database.getSingle(id)


    /**
     * Function to delete all simulation objects in the database and empty files directory.
     */
    suspend fun deleteAll() {
        database.deleteAll()
        deleteFiles()
    }


    /**
     * Function that maps and creates a flow of simulation objects.
     */
    fun getAllData(): Flow<List<SimulationData>> = database.getAll()
            .map { list ->
                list.map { item ->
                    item.toDomain()
                }
            }


    /**
     * Function used to save a NetCdf file and update database.
     *
     * @param id Primary key of associated database object.
     * @param body File content from API.
     */
    suspend fun insertFile(id: Int, body: ResponseBody) {

        //File name is current time
        val fileName: String = String.format(FILE_FORMAT, System.currentTimeMillis().toString())

        //If save file is successful - update database
        if (saveFile(body, fileName)) {
            update(id, fileName, true)
        } else{
            updateFail(id)
        }
    }


    /**
     * Function used to update database on successful NetCDF file download and save.
     */
    private suspend fun update(id: Int, fileName: String, done: Boolean) = database.updatePath(id, fileName, done)


    /**
     * Function used to update database on failed simulation retrieval.
     */
     suspend fun updateFail(id: Int) = database.updateFail(id, false)


    /**
     * Function used to save a NetCDF file to application local files directory.
     */
    private fun saveFile(body: ResponseBody, fileName: String): Boolean {

        var input: InputStream? = null
        val path = app.filesDir.absolutePath

        // Try to write content to file
        try {
            input = body.byteStream()
            val fos = FileOutputStream(path + fileName)
            fos.use { output ->
                val buffer = ByteArray(1 * 1024)
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()

            }
            return true
        } catch (e: Exception) {
            Log.e(FILE_ERROR, e.toString())
            return false
        } finally {
            input?.close()
        }
    }


    /**
     * Function to delete all NetCDF files in app local files directory.
     */
    private fun deleteFiles() {
        try {
            app.filesDir.listFiles()?.forEach { file ->
                val name = file.name
                if (name.endsWith(FILE_TYPE)) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            Log.d(FILE_ERROR, e.toString())
        }
    }

}