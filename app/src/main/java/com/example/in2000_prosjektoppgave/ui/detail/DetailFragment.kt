package com.example.in2000_prosjektoppgave.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.in2000_prosjektoppgave.R
import com.example.in2000_prosjektoppgave.databinding.FragmentDetailBinding
import com.example.in2000_prosjektoppgave.model.SimulationData
import com.example.in2000_prosjektoppgave.util.*
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


/**
 * Detail fragment: Displays a form the user can fill out to run drifty simulations.
 */
@AndroidEntryPoint
class DetailFragment : Fragment() {

    //Constants
    companion object {
        const val TAG = "TAG"
        const val DATE_DISPLAY = "dd.MM.yyyy"
        const val UTC = "UTC"
        const val REQUEST_KEY = "REQUEST_KEY"
        const val LONGITUDE = "LONGITUDE"
        const val LATITUDE = "LATITUDE"
        const val NB = "nb_NO"
    }

    // ViewModel for detail fragment
    private val viewModel: DetailViewModel by viewModels()

    // Binding for views
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    // List of points
    private var polyLatitude: MutableList<Double> = mutableListOf()
    private var polyLongitude: MutableList<Double> = mutableListOf()


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up bindings
        setUpBinding()

        // Set up communication with map search fragment
        setUpCommunication()

        // Set norwegian as language
        Locale.setDefault(Locale(NB))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    /**
     * Sets up bindings for views, on click listeners, text change listeners etc.
     */
    private fun setUpBinding() {


        // Icon click listener for time (from)
        binding.editTime.setStartIconOnClickListener {
            displayTimePicker(binding.editTime)
        }


        // Icon click listener for date (from)
        binding.editDate.setStartIconOnClickListener {
            displayDatePicker(binding.editDate)
        }


        // Icon click listener for time (to)
        binding.editTimeC.setStartIconOnClickListener {
            displayTimePicker(binding.editTimeC)
        }


        // Icon click listener for date (to)
        binding.editDateC.setStartIconOnClickListener {
            displayDatePicker(binding.editDateC)
        }


        // Set up on click lister for geographical toggle button
        binding.toggleGeo.addOnButtonCheckedListener(toggleGeoOnClick())


        // Click listener for polygon add point button
        binding.buttonPointList.setOnClickListener {
            var error = true

            // Checks if view is empty and if error is set
            listOf(binding.editLat, binding.editLon).forEach {
                it.checkEmpty(resources.getString(R.string.required))
                if (it.error != null) error = false
            }

            // Add and display point if no errors
            if (error) {

                // Adding points to lists
                binding.editLat.toDouble()?.let { it1 -> polyLatitude.add(it1) }
                binding.editLon.toDouble()?.let { it1 -> polyLongitude.add(it1) }

                binding.textPointList.append(
                        resources.getString(
                                R.string.point_display,
                                binding.editLat.toDouble(),
                                binding.editLon.toDouble()
                        )
                )
            }
        }


        // Click listener for simulation button
        binding.buttonRun.setOnClickListener {
            // Find type : polygon, point or cone
            val type = findType()

            // If no errors in form run a simulation and navigate up
            if (inputCheck() && type.isNotEmpty()) {
                viewModel.simulationStarter(createSimulationObject(type))
                findNavController().navigateUp()
            }
        }


        // Set up on click lister for communication with map fragment for point icons
        val navigate = View.OnClickListener {

            // Saving information
            viewModel.communication = Communication.POINT
            viewModel.userInput = saveState()

            // Navigate to search fragment
            findNavController().navigate(R.id.action_driftyDetailFragment_to_mapsSearchFragment)
        }

        binding.editLat.setStartIconOnClickListener(navigate)
        binding.editLon.setStartIconOnClickListener(navigate)


        // Set up on click lister for communication with map fragment for cone icons
        val navigateC = View.OnClickListener {

            // Saving information
            viewModel.communication = Communication.CONE
            viewModel.userInput = saveState()

            // Navigate to search fragment
            findNavController().navigate(R.id.action_driftyDetailFragment_to_mapsSearchFragment)
        }

        binding.editLatC.setStartIconOnClickListener(navigateC)
        binding.editLonC.setStartIconOnClickListener(navigateC)


        // Set up Text watchers for all Edit text fields with numerical input
        binding.editLon.editText?.addTextChangedListener(
                TextChecker(
                        -180.0,
                        180.0,
                        3,
                        binding.editLon
                )
        )
        binding.editLat.editText?.addTextChangedListener(
                TextChecker(
                        -90.0,
                        90.0,
                        3,
                        binding.editLat
                )
        )
        binding.editRadius.editText?.addTextChangedListener(
                TextChecker(
                        0.0,
                        500.0,
                        0,
                        binding.editRadius
                )
        )

        binding.editLonC.editText?.addTextChangedListener(
                TextChecker(
                        -180.0,
                        180.0,
                        3,
                        binding.editLonC
                )
        )
        binding.editLatC.editText?.addTextChangedListener(
                TextChecker(
                        -90.0,
                        90.0,
                        3,
                        binding.editLatC
                )
        )
        binding.editRadiusC.editText?.addTextChangedListener(
                TextChecker(
                        0.0,
                        1000.0,
                        0,
                        binding.editRadiusC
                )
        )

        binding.editSimTime.editText?.addTextChangedListener(
                TextChecker(
                        -240.0,
                        240.0,
                        0,
                        binding.editSimTime
                )
        )

        binding.editCurSpeed.editText?.addTextChangedListener(
                TextChecker(
                        0.0,
                        14.142,
                        3,
                        binding.editCurSpeed
                )
        )
        binding.editCurDir.editText?.addTextChangedListener(
                TextChecker(
                        0.0,
                        360.0,
                        3,
                        binding.editCurDir
                )
        )
        binding.editWindSpeed.editText?.addTextChangedListener(
                TextChecker(
                        0.0,
                        14.142,
                        3,
                        binding.editWindSpeed
                )
        )
        binding.editWindDir.editText?.addTextChangedListener(
                TextChecker(
                        0.0,
                        360.0,
                        3,
                        binding.editWindDir
                )
        )


        // Set up Text watchers for all edit text with time input
        binding.editTime.editText?.addTextChangedListener(
                TimeChecker(binding.editTime)
        )
        binding.editTimeC.editText?.addTextChangedListener(
                TimeChecker(binding.editTimeC)
        )


        // Set up Text watchers for all edit text with date input
        binding.editDate.editText?.addTextChangedListener(
                DateChecker(binding.editDate)
        )
        binding.editDateC.editText?.addTextChangedListener(
                DateChecker(binding.editDateC)
        )


        // Set up for top level chip group
        with(binding.chipsTop) {

            // Make sure all views are removed
            removeAllViews()

            // Create all chips
            setChips(resources.getStringArray(R.array.top_display_name), this)

            // Set on checked listener
            setOnCheckedChangeListener { _, id ->
                // Delete all chips in sub group
                binding.chipsSub.removeAllViews()

                // Add new sub group
                val res = resources.obtainTypedArray(R.array.sub_groups)

                if (id - 1 < res.length()) {
                    setChips(
                            resources.getStringArray(res.getResourceId(id - 1, 0)),
                            binding.chipsSub
                    )
                }
                res.recycle()
                val sec = binding.chipsSub.getChildAt(0) as Chip?
                sec?.isChecked = true
            }

            // Set first to checked
            if (checkedChipId == View.NO_ID) {
                val first = this.getChildAt(0) as Chip
                first.isChecked = true
            }
        }

    }


    /**
     * Function that builds and displays a time picker.
     *
     * @param edit TextInputLayout object that will display picked time and error.
     */
    private fun displayTimePicker(edit: TextInputLayout) {

        // Build time picker
        val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText(R.string.pick_time)
                .build()

        // Show the picker
        picker.show(parentFragmentManager, TAG)

        // Set time in edit text on positive action click
        picker.addOnPositiveButtonClickListener {
            edit.editText?.setText(resources.getString(R.string.generic_24_hour, picker.hour, picker.minute))
            edit.error = null
        }
    }


    /**
     * Function that builds and displays a date picker.
     *
     * @param edit TextInputLayout object that will display picked time and error.
     */
    private fun displayDatePicker(edit: TextInputLayout) {


        // Build date picker
        val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.pick_date)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        // Set output format
        val outputDateFormat = SimpleDateFormat(DATE_DISPLAY, Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone(UTC)
        }

        // Show the picker
        picker.show(parentFragmentManager, TAG)

        // Set date in edit text on positive action click
        picker.addOnPositiveButtonClickListener {
            edit.editText?.setText(outputDateFormat.format(it))
            edit.error = null
        }

    }


    /**
     * Function to validate if required edit text input are filled.
     */
    private fun inputCheck(): Boolean {

        // List of views that are required in different scenarios
        val requiredAll = listOf(binding.editSimTime, binding.editTime, binding.editDate)
        val requiredPoint = listOf(binding.editLon, binding.editLat)
        val requiredCone =
                listOf(binding.editLatC, binding.editLonC, binding.editTimeC, binding.editDateC)
        var out = true


        // Checks if view is empty and if error is set
        requiredAll.forEach {
            it.checkEmpty(resources.getString(R.string.required))
            if (it.error != null) out = false
        }

        when (binding.toggleGeo.checkedButtonId) {
            binding.buttonPoint.id -> {
                requiredPoint.forEach {
                    it.checkEmpty(resources.getString(R.string.required))
                    if (it.error != null) out = false
                }
            }
            binding.buttonCone.id -> {
                requiredPoint.forEach {
                    it.checkEmpty(resources.getString(R.string.required))
                    if (it.error != null) out = false
                }
                requiredCone.forEach {
                    it.checkEmpty(resources.getString(R.string.required))
                    if (it.error != null) out = false
                }
            }
            binding.buttonPolygon.id -> {
                if (polyLatitude.size < 3) out = false
            }
        }


        // Checks that time has the correct length
        if (binding.editTime.length() in 1..4) {
            binding.editTime.error = resources.getString(R.string.format_error)
            out = false
        }

        if (binding.editTimeC.length() != 0 && binding.editTimeC.length() in 1..4) {
            binding.editTimeC.error = resources.getString(R.string.format_error)
            out = false
        }

        // Checks that date has the correct length
        if (binding.editDate.length() in 1..9) {
            binding.editDate.error = resources.getString(R.string.format_error)
            out = false
        }
        if (binding.editDateC.length() != 0 && binding.editDateC.length() in 1..9) {
            binding.editDateC.error = resources.getString(R.string.format_error)
            out = false
        }

        // Check if only speed or direction for wind and current is given.
        if (binding.editWindSpeed.length() != 0) {
            with(binding.editWindDir) {
                checkEmpty(resources.getString(R.string.required))
                if (error != null) out = false
            }
        }
        if (binding.editWindDir.length() != 0) {
            with(binding.editWindSpeed) {
                checkEmpty(resources.getString(R.string.required))
                if (error != null) out = false
            }
        }
        if (binding.editCurSpeed.length() != 0) {
            with(binding.editCurDir) {
                checkEmpty(resources.getString(R.string.required))
                if (error != null) out = false
            }
        }
        if (binding.editCurDir.length() != 0) {
            with(binding.editCurSpeed) {
                checkEmpty(resources.getString(R.string.required))
                if (error != null) out = false
            }
        }

        return out
    }


    /**
     * Function to create a toggle button on checked listener for geo data.
     */
    private fun toggleGeoOnClick() =
            MaterialButtonToggleGroup.OnButtonCheckedListener { _, checkedId, _ ->
                when (checkedId) {
                    binding.buttonPoint.id -> {
                        binding.textGeo.text = resources.getString(R.string.geo_data)
                        binding.textPointList.text = null
                        polyLatitude.clear()
                        polyLongitude.clear()
                        listOf(
                                binding.editLatC,
                                binding.editRadiusC,
                                binding.editLonC,
                                binding.editTimeC,
                                binding.editDateC
                        ).forEach {
                            it.editText?.text = null
                            it.error = null
                        }
                        binding.groupCone.visibility = View.GONE
                        binding.groupPoly.visibility = View.GONE
                        binding.editRadius.visibility = View.VISIBLE
                    }
                    binding.buttonCone.id -> {
                        binding.textGeo.text = resources.getString(R.string.geo_data_from)
                        binding.textPointList.text = null
                        polyLatitude.clear()
                        polyLongitude.clear()
                        binding.groupCone.visibility = View.VISIBLE
                        binding.groupPoly.visibility = View.GONE
                        binding.editRadius.visibility = View.VISIBLE
                    }
                    binding.buttonPolygon.id -> {
                        binding.textGeo.text = resources.getString(R.string.geo_data)
                        listOf(
                                binding.editRadius,
                                binding.editLatC,
                                binding.editRadiusC,
                                binding.editLonC,
                                binding.editTimeC,
                                binding.editDateC
                        ).forEach {
                            it.editText?.text = null
                            it.error = null
                        }
                        binding.groupCone.visibility = View.GONE
                        binding.editRadius.visibility = View.INVISIBLE
                        binding.groupPoly.visibility = View.VISIBLE
                    }
                }
            }


    /**
     * Function that configures and adds chips to a chip group.
     *
     * @param list List of chip display name.
     * @param group Chip group view.
     */
    private fun setChips(list: Array<String>, group: ChipGroup) = list.forEachIndexed { id, text ->
        val chip = layoutInflater.inflate(R.layout.item_chip, group, false) as Chip
        chip.text = text
        chip.id = id + 1
        group.addView(chip)
    }


    /**
     * Function to create a simulation data object from edit text input.
     */
    private fun findType(): String = when (binding.toggleGeo.checkedButtonId) {
        binding.buttonPoint.id -> {
            binding.buttonPoint.tag.toString()
        }
        binding.buttonCone.id -> {
            binding.buttonCone.tag.toString()
        }
        binding.buttonPolygon.id -> {
            binding.buttonPolygon.tag.toString()
        }
        else -> {
            ""
        }
    }


    /**
     * Function to create a simulation data object from edit text input.
     */
    private fun createSimulationObject(type: String) = SimulationData(
            name = binding.chipsTop.findViewById<Chip>(binding.chipsTop.checkedChipId).text.toString(),
            nameAlias = NameParser.findNameAlias(
                    if(binding.chipsTop.checkedChipId - 1 < 0) 0 else binding.chipsTop.checkedChipId - 1,
                    if(binding.chipsSub.checkedChipId - 1 < 0) 0 else binding.chipsSub.checkedChipId - 1),
            latitude = if (type == SimulationData.POLYGON) {
                polyLatitude
            } else {
                listOfNotNull(binding.editLat.toDouble(), binding.editLatC.toDouble())
            },
            longitude = if (type == SimulationData.POLYGON) {
                polyLongitude
            } else {
                listOfNotNull(binding.editLon.toDouble(), binding.editLonC.toDouble())
            },
            radius = listOf(binding.editRadius.toInt() ?: 0, binding.editRadiusC.toInt() ?: 0),
            date = listOfNotNull(binding.editDate.toText(), binding.editDateC.toText()),
            time = listOfNotNull(binding.editTime.toText(), binding.editTimeC.toText()),
            simulationLength = binding.editSimTime.toInt() ?: 10,
            type = type,
            curDir = binding.editCurDir.toDouble(),
            curSpeed = binding.editCurSpeed.toDouble(),
            windDir = binding.editWindDir.toDouble(),
            windSpeed = binding.editWindSpeed.toDouble()
    )


    /**
     * Function to save UserInput object.
     */
    private fun saveState() = UserInput(
            list = listOf(
                    binding.editLat.editText?.text,
                    binding.editLon.editText?.text,
                    binding.editRadius.editText?.text,
                    binding.editDate.editText?.text,
                    binding.editTime.editText?.text,
                    binding.editLatC.editText?.text,
                    binding.editLonC.editText?.text,
                    binding.editRadiusC.editText?.text,
                    binding.editDateC.editText?.text,
                    binding.editTimeC.editText?.text,
                    binding.editSimTime.editText?.text,
                    binding.editWindDir.editText?.text,
                    binding.editWindSpeed.editText?.text,
                    binding.editCurSpeed.editText?.text,
                    binding.editCurDir.editText?.text
            ),
            topChip = binding.chipsTop.checkedChipId,
            subChip = binding.chipsSub.checkedChipId,
            latitudeList = polyLatitude,
            longitudeList = polyLongitude,
            listText = binding.textPointList.text.toString()
    )


    /**
     * Function to write UserInput to text views.
     */
    private fun setUpTextViews(userInput: UserInput) {

        // Write to text fields
        listOf(
                binding.editLat,
                binding.editLon,
                binding.editRadius,
                binding.editDate,
                binding.editTime,
                binding.editLatC,
                binding.editLonC,
                binding.editRadiusC,
                binding.editDateC,
                binding.editTimeC,
                binding.editSimTime,
                binding.editWindDir,
                binding.editWindSpeed,
                binding.editCurSpeed,
                binding.editCurDir
        ).forEachIndexed { i, edit ->
            edit.editText?.text = userInput.list[i]
        }

        // Set selected chips
        val topChip = binding.chipsTop.getChildAt(userInput.topChip - 1) as Chip?
        topChip?.isChecked = true

        val subChip = binding.chipsSub.getChildAt(userInput.subChip - 1) as Chip?
        subChip?.isChecked = true

        // Set latitude and longitude lists if polygon is selected
        polyLatitude = userInput.latitudeList as MutableList<Double>
        polyLongitude = userInput.longitudeList as MutableList<Double>
        binding.textPointList.text = userInput.listText

    }


    /**
     * Sets up communication with Map Search Fragment - to get longitude and latitude from map.
     */
    private fun setUpCommunication() {
        setFragmentResultListener(REQUEST_KEY) { _, bundle ->

            setUpTextViews(viewModel.userInput)
            val lon = bundle.getString(LONGITUDE)
            val lat = bundle.getString(LATITUDE)

            when (viewModel.communication) {
                Communication.POINT -> {
                    binding.editLon.editText?.setText(lon)
                    binding.editLat.editText?.setText(lat)
                }
                Communication.CONE -> {
                    binding.editLonC.editText?.setText(lon)
                    binding.editLatC.editText?.setText(lat)
                }
            }

        }
    }

}