package com.example.in2000_prosjektoppgave.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.in2000_prosjektoppgave.data.repository.DriftyRepository
import com.example.in2000_prosjektoppgave.util.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


/**
 * Network worker class that implements Coroutine Worker. A ListenableWorker that provides
 * interoperability with Kotlin Coroutines. Function is suspending.
 *
 * @property repository Reference to Drifty repository.
 *
 * @param appContext Application context.
 * @param workerParams Worker parameters.
 */
@HiltWorker
class NetworkWorker @AssistedInject constructor(
        @Assisted appContext: Context,
        @Assisted workerParams: WorkerParameters,
        private val repository: DriftyRepository
) : CoroutineWorker(appContext, workerParams) {

    // Constants
    companion object {
        const val ID = "ID"
        const val TAG = "Network Worker"
    }

    override suspend fun doWork(): Result {

        // Get primary key of database simulation object
        val id = inputData.getString(ID)?.toIntOrNull()

        // If key not null
        if (id != null) {

            // Fetch simulation object from database
            val obj = repository.getSimulationObject(id)

            // If object found in database
            if (obj != null) {

                // Send simulation object to drifty and wait for response
                return when (val res = repository.runSimulation(obj.toDto(obj.type))) {
                    is Resource.Success -> {

                        // On success save file and update database
                        repository.insertFile(id, res.data!!)

                        // Return worker success
                        Result.success()
                    }
                    is Resource.Error -> {

                        // On error log message if set
                        res.message?.let { Log.d(TAG, it) }

                        // Return worker failure - update database with failed simulation
                        repository.updateFail(id)
                        Result.failure()
                    }
                    else -> {
                        // Return worker failure - update database with failed simulation
                        repository.updateFail(id)
                        Result.failure()
                    }
                }
            }
        }
        // Return worker failure
        return Result.failure()
    }
}