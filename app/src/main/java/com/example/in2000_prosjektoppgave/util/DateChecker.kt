package com.example.in2000_prosjektoppgave.util

import android.text.Editable
import android.text.TextWatcher
import com.example.in2000_prosjektoppgave.R
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*


/**
 * Class that implements TextWatcher. When an object of this class is attached to
 * an Editable, its methods will be called on text changes. The class sets an error
 * on a Text Input Layout if the edit text is greater than an allowed number. It also
 * restricts the number characters to be added.
 */
class DateChecker(
        private val view: TextInputLayout
) : TextWatcher {

    // Text before most recent change
    private var previousText = ""

    // Helper variable to apply "." correctly
    private var canApplyFirst = true

    // Helper variable to apply "." correctly
    private var canApplySecond = true

    // List of months with 30 days
    private var monthList = listOf("04", "06", "09", "11")


    override fun afterTextChanged(s: Editable) {

        // Resets the canApply* variable so that user can edit date
        if (s.length == 1) {
            canApplyFirst = true
        } else if (s.length == 4) {
            canApplySecond = true
        }

        // Actions for when input has length two
        if (s.length == 2) {

            // Variable to fetch date items
            val day = s.subSequence(0, 2).toString()

            // Checks if the two first characters is an integer
            if (day.toIntOrNull() != null) {

                // Checks if the integer is above 31
                if (day.toInt() <= 31) {
                    view.error = null
                } else if (day.toInt() > 31) {
                    view.error = view.resources.getString(R.string.days_max)
                }

                // Resets the canApplyFirst variable so that user can edit time
                if (canApplyFirst) {
                    s.append(".")
                    canApplyFirst = false
                }
            }

            // Checks if the two first characters disallowed units
            if (day.contains(".") || day.contains("/")) {
                view.error = view.resources.getString(R.string.use_format_date)
            }

        }

        // Actions for when s has length 5
        if (s.length == 5) {

            // Variables to fetch date items
            val day = s.subSequence(0, 2).toString()
            val month = s.subSequence(3, 5).toString()

            // Checks if the two middle characters is an integer
            if (month.toIntOrNull() != null) {

                // Checks if the integer is above 12
                if (month.toInt() <= 12) {
                    view.error = null
                } else if (month.toInt() > 12) {
                    view.error = view.resources.getString(R.string.month_max)
                }

                // Checks if month has not 31 days
                if (monthList.contains(month) && day.toInt() == 31) {
                    view.error = view.resources.getString(R.string.month_max_variant_one)
                } else if (month == "02" && day.toInt() > 29) {
                    view.error = view.resources.getString(R.string.month_max_variant_two)
                }

                // Resets the canApplySecond variable so that user can edit time
                if (canApplySecond) {
                    s.append(".")
                    canApplySecond = false
                }
            }

            // Checks if the two last characters disallowed units
            if (month.contains(".") || month.contains("/")) {
                view.error = view.resources.getString(R.string.use_format_date)
            }
        }

        // Logic for when s has length 10
        if (s.length == 10) {

            if (isValidDate(s.toString())) {
                view.error = null
            } else {
                view.error = view.resources.getString(R.string.date_not_approved)
            }
        }

        // Checks correct format
        if (s.length >= 3) {
            if (!s.subSequence(2, 3).toString().contains(".")) {
                view.error = view.resources.getString(R.string.use_format_date)
            }
        } else if (s.length >= 5) {
            if (!s.subSequence(5, 6).toString().contains(".")) {
                view.error = view.resources.getString(R.string.use_format_date)
            }
        }

        // Check if user input exceeds the allowed number of characters
        if (s.length > 10) {
            s.replace(0, s.length, previousText)
        }

        // Remember previous text
        previousText = s.toString()

    }


    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}


    /**
     * Function used to validate if a date is correct.
     */
    private fun isValidDate(input: String): Boolean {
        return try {
            val format = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            format.isLenient = false
            format.parse(input)
            true
        } catch (e: Exception) {
            false
        }
    }


    // Constants
    companion object {
        const val DATE_FORMAT = "dd.MM.yyyy"
    }
}