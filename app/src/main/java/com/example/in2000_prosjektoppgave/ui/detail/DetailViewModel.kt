package com.example.in2000_prosjektoppgave.ui.detail

import androidx.lifecycle.*
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.in2000_prosjektoppgave.data.repository.DriftyRepository
import com.example.in2000_prosjektoppgave.model.SimulationData
import com.example.in2000_prosjektoppgave.ui.MainApplication
import com.example.in2000_prosjektoppgave.util.Communication
import com.example.in2000_prosjektoppgave.util.UserInput
import com.example.in2000_prosjektoppgave.workers.NetworkWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * View model used by Detail fragment.
 *
 * @property repository Drifty Reposistory object.
 * @param app Application context.
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
        private val repository: DriftyRepository,
        app: MainApplication
) : ViewModel() {

    // Constants
    companion object{
        const val KEY = "ID"
    }

    // Saved variables for communication with map search fragment
    var communication : Communication = Communication.POINT

    // Saved variable for edit texts
    var userInput : UserInput = UserInput(emptyList())

    // Work manager object
    private val workManager = WorkManager.getInstance(app.applicationContext)


    /**
     * Main function for running simulations. The function adds an object to the database, and
     * sets a worker on the task of retrieving data from the server. On success it the proceeds to
     * save a NetCDF file to local storage and updates the database.
     *
     * @param data A simulation Data object.
     */
    fun simulationStarter(data: SimulationData) {
        viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
            // Insert a file to repository and get Primary key
            val id = repository.insert(data)

            // Add Primary key as input and run worker
            val key = Data.Builder().putString(KEY, id.toString()).build()
            runWorker(key)
        }
    }


    /**
     * Function that runs a worker with a one time request to fetch simulation results from
     * server.
     *
     * @param data Input Data for worker.
     */
    private fun runWorker(data: Data) {
        val request = OneTimeWorkRequestBuilder<NetworkWorker>()
                .setInputData(data)
                .build()
        workManager.enqueue(request)
    }

}



