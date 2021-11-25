package com.example.in2000_prosjektoppgave.ui.drifty

import androidx.lifecycle.*
import com.example.in2000_prosjektoppgave.data.repository.DriftyRepository
import com.example.in2000_prosjektoppgave.model.SimulationData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * View model used by Drifty fragment.
 *
 * @property repository Drifty Reposistory object.
 */
@HiltViewModel
class DriftyViewModel @Inject constructor(
        private val repository: DriftyRepository
) : ViewModel() {

    // Live Data of simulation objects in database
    private var _simulationList: MutableLiveData<List<SimulationData>> = getDatabaseFlow() as MutableLiveData
    val simulationList: LiveData<List<SimulationData>>
        get() = _simulationList


    /**
     * Function used to connect flow from database to the observable Live data
     */
    private fun getDatabaseFlow(): LiveData<List<SimulationData>> = repository.getDatabaseData().asLiveData()


    /**
     * Function used to delete all simulation entries in database and NetCDF files.
     */
    fun emptyDatabase(){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteAll()
        }
    }

}



