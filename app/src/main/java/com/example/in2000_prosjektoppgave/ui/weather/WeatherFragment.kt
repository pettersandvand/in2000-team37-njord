package com.example.in2000_prosjektoppgave.ui.weather

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.in2000_prosjektoppgave.R
import com.example.in2000_prosjektoppgave.databinding.FragmentWeatherBinding
import com.example.in2000_prosjektoppgave.model.DataForecast
import com.example.in2000_prosjektoppgave.model.DataOcean
import com.example.in2000_prosjektoppgave.util.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


/**
 * Weather fragment: Used to display weather data.
 */
@AndroidEntryPoint
class WeatherFragment : Fragment() {

    // Constant strings
    companion object {
        const val REQUEST_KEY = "REQUEST_KEY"
        const val LONGITUDE = "LONGITUDE"
        const val LATITUDE = "LATITUDE"
        const val ISO_TIME = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        const val DISPLAY_TIME = "d. MMMM, EEEE kk:00"
        const val TIME_FORMAT = "%02d:00"
        const val DRAWABLE = "drawable"
        const val DEPRECATION = "DEPRECATION"
        const val NB = "nb"
        const val NO = "NO"
    }

    // ViewModel for weather forecast
    private val viewModel: WeatherViewModel by viewModels()

    // Binding for views
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    // Slider object
    private lateinit var sliderListener: SliderListener


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        sliderListener = SliderListener()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        // Set up observers, binding of views and communication with map fragment
        setUpObservers()
        setUpBindings()
        setUpCommunication()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_menu, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Set up dialog to pop up when option button clicked
        return when (item.itemId) {
            R.id.option -> {
                MaterialAlertDialogBuilder(requireContext())
                        .setTitle(resources.getString(R.string.empty_database))
                        .setMessage(resources.getString(R.string.empty_database_des_weather))
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
     * Sets up observers for weather and ocean live data.
     */
    private fun setUpObservers() {

        // Observer for weather live data
        viewModel.weather.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    // Hide progressbar
                    binding.progressBar.visibility = View.GONE

                    if (it.data != null) {
                        // Set coordinate text in UI
                        setCoordinateText(it.data.latitude, it.data.longitude)

                        // Set weather list in slider object - show card and hide error
                        sliderListener.setWeather(it.data.timeSeries)
                        binding.weatherCard.visibility = View.VISIBLE
                        binding.errorText.visibility = View.GONE


                    } else {
                        // Show error text and hide card
                        sliderListener.setWeather(emptyList())
                        binding.weatherCard.visibility = View.GONE
                        binding.errorText.visibility = View.VISIBLE
                    }
                }
                is Resource.Error -> {
                    // Hide progressbar
                    binding.progressBar.visibility = View.GONE

                    // Set weather list in slider object - show error and hide card
                    sliderListener.setWeather(emptyList())
                    binding.weatherCard.visibility = View.GONE
                    binding.errorText.visibility = View.VISIBLE
                    Snackbar.make(
                            requireActivity().findViewById(R.id.weatherFragment),
                            it.message ?: resources.getString(R.string.generic_error),
                            Snackbar.LENGTH_LONG
                    ).show()

                    // Clear error, so that it does not reappear on config changes
                    viewModel.clearError(ForecastType.WEATHER)
                }
                is Resource.Loading -> {
                    // Show progressbar
                    binding.progressBar.visibility = View.VISIBLE
                }

            }
        }


        // Observer for ocean live data
        viewModel.ocean.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    // Hide progressbar
                    binding.progressBar.visibility = View.GONE

                    if (it.data != null) {
                        // Set ocean list in slider object - enable expand button
                        sliderListener.setOcean(it.data.time_series)
                        binding.buttonExpand.isEnabled = true
                    } else {
                        // Disable expand button
                        sliderListener.setOcean(emptyList())
                        binding.buttonExpand.isEnabled = false
                        binding.groupExpand.visibility = View.GONE
                        binding.buttonExpand.text = resources.getString(R.string.expand)
                    }
                }
                is Resource.Error -> {
                    // Hide progressbar
                    binding.progressBar.visibility = View.GONE

                    // Disable expand button and display error
                    sliderListener.setOcean(emptyList())
                    binding.buttonExpand.isEnabled = false
                    binding.groupExpand.visibility = View.GONE
                    binding.buttonExpand.text = resources.getString(R.string.expand)
                    Snackbar.make(
                            requireActivity().findViewById(R.id.weatherFragment),
                            it.message ?: resources.getString(R.string.generic_error),
                            Snackbar.LENGTH_LONG
                    ).show()

                    // Clear error, so that it does not reappear on config changes
                    viewModel.clearError(ForecastType.OCEAN)
                }
                is Resource.Loading -> {
                    // Show progressbar
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }

    }


    /**
     * Sets up bindings for views, on click listeners, text change listeners etc.
     */
    private fun setUpBindings() {

        // Set action on floating action button click
        binding.buttonSend.setOnClickListener {
            if (inputCheck()) {
                viewModel.fetchAllData(
                        binding.latitudeInput.toDouble()!!,
                        binding.longitudeInput.toDouble()!!
                )
            }
        }


        // Set action on latitude start icon click
        binding.latitudeInput.setStartIconOnClickListener {
            findNavController().navigate(R.id.action_weatherFragment_to_mapsSearchFragment)
        }


        // Set action on longitude start icon click
        binding.longitudeInput.setStartIconOnClickListener {
            findNavController().navigate(R.id.action_weatherFragment_to_mapsSearchFragment)
        }


        // Set action on expand button click
        binding.buttonExpand.setOnClickListener {
            if (binding.groupExpand.visibility == View.VISIBLE) {
                binding.groupExpand.visibility = View.GONE
                binding.buttonExpand.text = resources.getString(R.string.expand)
            } else {
                binding.groupExpand.visibility = View.VISIBLE
                binding.buttonExpand.text = resources.getString(R.string.close)
            }
        }


        // Set up slider
        binding.slider.addOnChangeListener(sliderListener)
        binding.slider.setLabelFormatter(sliderListener)


        // Sets text watcher to inform user if input is outside allowed bounds
        binding.latitudeInput.editText?.addTextChangedListener(
                TextChecker(
                        -90.0,
                        90.0,
                        3,
                        binding.latitudeInput
                )
        )
        binding.longitudeInput.editText?.addTextChangedListener(
                TextChecker(
                        -180.0,
                        180.0,
                        3,
                        binding.longitudeInput
                )
        )

    }


    /**
     * Sets up communication with Map Search Fragment - to get longitude and latitude from map.
     */
    private fun setUpCommunication() {
        setFragmentResultListener(REQUEST_KEY) { _, bundle ->
            val lon = bundle.getString(LONGITUDE)
            val lat = bundle.getString(LATITUDE)
            binding.longitudeInput.editText?.setText(lon)
            binding.latitudeInput.editText?.setText(lat)
            if (inputCheck()) {
                viewModel.fetchAllData(
                        binding.latitudeInput.toDouble()!!,
                        binding.longitudeInput.toDouble()!!
                )
            }
        }
    }


    /**
     * Function to validate if required edit text input are filled correctly.
     */
    private fun inputCheck(): Boolean {

        // List of input views that are required
        val required = listOf(binding.longitudeInput, binding.latitudeInput)

        var out = true

        // Checks if view is empty and if error is set
        required.forEach {
            it.checkEmpty(resources.getString(R.string.required))
            if (it.error != null) out = false
        }
        return out
    }


    /**
     * Function that binds weather information to text views in UI.
     */
    private fun setWeather(data: DataForecast) {
        binding.tempText.text = String.format("% 5.1f", data.temperature)
        binding.windText.text = resources.getString(R.string.wind_speed_des, data.windSpeed)
        binding.windDirectionText.text = resources.getString(R.string.direction, data.windDir)
        binding.timeText.text = changeDateFormat(data.time)
        data.symbol?.let { loadImage(it) }
    }


    /**
     * Function that binds ocean information to text views in UI.
     */
    private fun setOcean(data: DataOcean) {
        binding.waveHeightText.text = resources.getString(R.string.wave_height, data.waveHeight)
        binding.waveDirectionText.text = resources.getString(R.string.direction, data.waveDirection)
        binding.curSpeedText.text = resources.getString(R.string.current_speed, data.currentSpeed)
        binding.curDirectionText.text = resources.getString(R.string.direction, data.currentDirection)
    }


    /**
     * Function that loads an image using its name into UI.
     *
     * @param symbol A symbol name.
     */
    private fun loadImage(symbol: String) {
        val drawableResourceId =
                resources.getIdentifier(symbol, DRAWABLE, context?.packageName)

        if (drawableResourceId != 0) {
            Glide.with(this)
                    .load(drawableResourceId)
                    .into(binding.imageView)
        } else {
            Glide.with(this)
                    .clear(binding.imageView)
        }

    }


    /**
     * Function that changes date format from ISO to desired display format.
     */
    private fun changeDateFormat(date: String): String {
        val inputFormat = SimpleDateFormat(ISO_TIME, Locale.getDefault())
        val outputFormat = SimpleDateFormat(DISPLAY_TIME, Locale(NB, NO))
        val input = inputFormat.parse(date)
        return if (input is Date) {
            outputFormat.format(input).split(" ").joinToString(" ") {
                it.capitalize(Locale.ROOT)
            }
        } else {
            ""
        }
    }


    /**
     * Function that binds information to coordinate text view in UI.
     */
    private fun setCoordinateText(lat: Double, lon: Double) {
        binding.placeText.text = if (lat >= 0 && lon >= 0) {
            resources.getString(R.string.north_east, abs(lat), abs(lon))
        } else if (lat >= 0 && lon < 0) {
            resources.getString(R.string.north_west, abs(lat), abs(lon))
        } else if (lat < 0 && lon >= 0) {
            resources.getString(R.string.south_east, abs(lat), abs(lon))
        } else {
            resources.getString(R.string.south_west, abs(lat), abs(lon))
        }
    }


    /**
     * Inner class that implements slider interface functions and hold data objects.
     */
    inner class SliderListener : Slider.OnChangeListener, LabelFormatter {

        // Ocean and weather data
        private var ocean: List<DataOcean> = emptyList()
        private var weather: List<DataForecast> = emptyList()

        // Index and offsets between lists
        private var value: Float = 0f
        private var offsetO: Int = 0
        private var offsetW: Int = 0


        override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {

            this.value = value

            // Display new values in UI on value change
            if (ocean.isNotEmpty() && ocean.size > value - 1 + offsetO) {
                setOcean(ocean[value.toInt() + offsetO])
            }
            if (weather.isNotEmpty() && weather.size > value - 1 + offsetW) {
                setWeather(weather[value.toInt() + offsetW])
            }
        }


        override fun getFormattedValue(value: Float): String {

            @Suppress(DEPRECATION)
            if (weather.isNotEmpty()) {
                val date = weather[offsetW].time
                val dateFormat = SimpleDateFormat(ISO_TIME, Locale.getDefault())
                val format = dateFormat.parse(date)
                val t0 = format?.hours ?: 0

                // Format displayed value on slider
                return if (t0 + value <= 24) {
                    String.format(TIME_FORMAT, t0 + value.toInt())
                } else {
                    String.format(TIME_FORMAT, t0 + value.toInt() - 24)
                }
            }
            return value.toString()
        }


        /**
         * Set new ocean list object and refresh UI.
         */
        fun setOcean(list: List<DataOcean>) {
            ocean = list
            determineOffsets()
            if (list.isNotEmpty() && list.size > value + offsetO) {
                setOcean(list[value.toInt() + offsetO])
            }
        }


        /**
         * Set new weather list object and refresh UI.
         */
        fun setWeather(list: List<DataForecast>) {
            weather = list
            determineOffsets()
            if (list.isNotEmpty() && list.size > value + offsetW) {
                setWeather(list[value.toInt() + offsetW])
            }
        }


        /**
         * Function used to calculate any offset in the time variable between ocean and
         * weather data. It sets internal variables to the offset, if any difference is found.
         * If everything is ok, or the data sets have no common time the offset is set to zero.
         */
        private fun determineOffsets() {

            // Check if data sets are empty
            if (ocean.isNotEmpty() && weather.isNotEmpty()) {

                var off1: Int = -1
                var off2: Int = -1

                // Check if ocean data has earlier time stamp
                for (i in ocean.indices) {
                    if (ocean[i].time < weather[0].time) {
                        continue
                    }
                    if (ocean[i].time == weather[0].time) {
                        off1 = i
                    }
                    break
                }

                // Check if weather data has earlier time stamp
                for (i in weather.indices) {
                    if (weather[i].time < ocean[0].time) {
                        continue
                    }
                    if (weather[i].time == ocean[0].time) {
                        off2 = i
                    }
                    break
                }

                // Set internal offset variables
                offsetO = if (off1 == -1) 0 else off1
                offsetW = if (off2 == -1) 0 else off2


            } else {
                // Set internal offset variables to zero
                offsetO = 0
                offsetW = 0
            }
        }
    }

}