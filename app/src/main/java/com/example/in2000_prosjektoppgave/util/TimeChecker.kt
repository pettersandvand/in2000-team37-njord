package com.example.in2000_prosjektoppgave.util

import android.text.Editable
import android.text.TextWatcher
import com.example.in2000_prosjektoppgave.R
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*


/**
 * Class that implements TextWatcher for verifying correct time-input.
 * When an object of this class is attached to an Editable, its methods will be called on text changes.
 * The class sets an error on a Text Input Layout if the edit text is greater than an allowed number.
 * It also restricts the number characters to be added.
 */
class TimeChecker(
        private val view: TextInputLayout
): TextWatcher {

    // Text before most recent change
    private var previousText = ""

    // Helper variable to apply ":" correctly
    private var canApply = true

    override fun afterTextChanged(s: Editable) {

        // Resets the canApply variable so that user can edit time
        if (s.length == 1) {
            canApply = true
        }

        // Logic for when s has length 2
        if (s.length == 2) {

            // Variable to fetch time items
            val hour = s.subSequence(0, 2).toString()

            // Checks if the two first characters is an integer
            if (hour.toIntOrNull() != null) {

                // Checks if the integer is above 23
                if (hour.toInt() <= 23) {
                    view.error = null
                } else if (hour.toInt() > 23) {
                    view.error = view.resources.getString(R.string.time_hours_max)
                }

                // Resets the canApply variable so that user can edit time
                if (canApply) {
                    s.append(":")
                    canApply = false
                }
            }

            // Checks if the two first characters disallowed units
            if (hour.contains(":")) view.error = view.resources.getString(R.string.time_format)
        }


        // Checks that hours and minutes are separated by ":"
        if (s.length >= 3) {
            if (!s.subSequence(2,3).toString().contains(":")) view.error = view.resources.getString(R.string.time_format)
        }

        // Logic for when s has length 5
        if (s.length == 5) {
            if(isValidTime(s.toString())){
                view.error = null
            } else{
                view.error = view.resources.getString(R.string.time_minutes_max)
            }
        }

        // Check if user input exceeds the allowed number of characters
        if (s.length > 5) {
            s.replace(0, s.length, previousText)
        }

        // Remember previous text
        previousText = s.toString()
    }


    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    /**
     * Function used to validate if time is correct.
     */
    private fun isValidTime(input: String): Boolean {
        return try {
            val format = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
            format.isLenient = false
            format.parse(input)
            true
        } catch (e: Exception) {
            false
        }
    }

    // Constants
    companion object{
        const val TIME_FORMAT = "HH:mm"
    }

}