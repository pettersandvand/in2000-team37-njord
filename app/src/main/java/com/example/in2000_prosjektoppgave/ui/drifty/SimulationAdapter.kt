package com.example.in2000_prosjektoppgave.ui.drifty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.example.in2000_prosjektoppgave.R
import com.example.in2000_prosjektoppgave.databinding.ItemSimulationBinding
import com.example.in2000_prosjektoppgave.model.SimulationData


/**
 * A recyclerview designed to hold Simulation Data objects.
 *
 * @constructor Create Simulation adapter.
 */
class SimulationAdapter : RecyclerView.Adapter<SimulationViewHolder>() {

    // Simulation data items
    private val items: MutableList<SimulationData> = mutableListOf()

    // Function for button click
    private var listener: ((b: Bundle) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimulationViewHolder {
        val binding: ItemSimulationBinding =
                ItemSimulationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SimulationViewHolder(binding, listener)
    }


    override fun onBindViewHolder(holder: SimulationViewHolder, position: Int) =
            holder.bind(items[position])


    override fun getItemCount(): Int = items.size


    /**
     * Function to set the lister variable.
     *
     * @param listener A function to be used by view holder.
     */
    fun setOnItemClickListener(listener: (b: Bundle) -> Unit) {
        this.listener = listener
    }


    /**
     * Function to refresh and set items in recyclerview.
     *
     * @param items List of simulation data objects.
     */
    fun setItems(items: List<SimulationData>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

}


/**
 * Recyclerview view holder for Simulation Adapter class.
 *
 * @property itemBinding View binding reference.
 * @property listener Open button on click listener function.
 */
class SimulationViewHolder(
        private val itemBinding: ItemSimulationBinding,
        private var listener: ((b: Bundle) -> Unit)? = null
) : RecyclerView.ViewHolder(itemBinding.root) {

    // Simulation data object for view holder
    private lateinit var simulation: SimulationData


    init {

        // Set on click listener for open button
        itemBinding.buttonOpen.setOnClickListener {

            // Allow action only on completed simulation
            if (simulation.status == true) {
                val b = bundleOf(KEY to simulation.path)
                listener?.invoke(b)
            }
        }
    }


    /**
     * Function that bind simulation data to views.
     *
     * @param simulation Simulation data object.
     */
    fun bind(simulation: SimulationData) {
        this.simulation = simulation
        itemBinding.titleText.text = simulation.name
        itemBinding.secText.text = itemView.resources.getString(
                R.string.simulation_date,
                simulation.datePerformed
        )
        itemBinding.mainText.text = itemView.resources.getString(
                R.string.simulation_description,
                simulation.latitude[0],
                simulation.longitude[0],
                status()
        )
    }


    /**
     * Function to get status message for different simulation states.
     */
    private fun status() = when(simulation.status){
        true -> itemView.resources.getString(R.string.success)
        false -> itemView.resources.getString(R.string.failed)
        null -> itemView.resources.getString(R.string.in_progress)
    }

    // Constants
    companion object{
        const val KEY = "FILE_1"
    }

}