package com.example.in2000_prosjektoppgave.ui.drifty

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.in2000_prosjektoppgave.R
import dagger.hilt.android.AndroidEntryPoint
import com.example.in2000_prosjektoppgave.databinding.FragmentDriftyBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


/**
 * Drifty fragment: Used to display previous data about previous simulation objects.
 */
@AndroidEntryPoint
class DriftyFragment : Fragment() {

    // ViewModel for drifty
    private val viewModel: DriftyViewModel by viewModels()

    // Binding for views
    private var _binding: FragmentDriftyBinding? = null
    private val binding get() = _binding!!

    // Adapter for recyclerview
    private lateinit var adapter: SimulationAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDriftyBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setupRecyclerView()
        setUpBindings()
        setUpObservers()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_menu, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Sets up on option selection for menu
        return when (item.itemId) {
            R.id.option -> {
                MaterialAlertDialogBuilder(requireContext())
                        .setTitle(resources.getString(R.string.empty_database))
                        .setMessage(resources.getString(R.string.empty_database_des_simulation))
                        .setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
                            dialog.cancel()
                        }
                        .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                            viewModel.emptyDatabase()
                            dialog.cancel()
                        }
                        .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    /**
     * Function to set up recycler view.
     */
    private fun setupRecyclerView() {
        adapter = SimulationAdapter()
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerview.adapter = adapter

        // Sets on click listener function for cards: Navigation to map view fragment
        adapter.setOnItemClickListener {
            findNavController().navigate(
                    R.id.action_driftyFragment_to_mapViewFragment,
                    it
            )
        }
    }


    /**
     * Sets up observer for simulation data objects live data
     */
    private fun setUpObservers() {
        viewModel.simulationList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                // Set recyclerview items if database is not empty
                binding.recyclerview.visibility = View.VISIBLE
                binding.emptyGroup.visibility = View.GONE
                adapter.setItems(it.reversed())
            } else {
                // Show empty box image in UI if database is empty
                binding.recyclerview.visibility = View.GONE
                binding.emptyGroup.visibility = View.VISIBLE
            }
        }
    }


    /**
     * Sets up bindings for views, on click listeners
     */
    private fun setUpBindings() {
        // Set action on floating action button click
        binding.buttonNewSim.setOnClickListener {
            // Navigate to drifty detail fragment
            findNavController().navigate(R.id.action_driftyFragment_to_driftyDetailFragment)
        }
    }

}