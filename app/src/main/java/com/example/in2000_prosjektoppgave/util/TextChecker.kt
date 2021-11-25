package com.example.in2000_prosjektoppgave.util

import android.text.Editable
import android.text.TextWatcher
import com.example.in2000_prosjektoppgave.R
import com.google.android.material.textfield.TextInputLayout


/**
 * Class that implements TextWatcher. When an object of this class is attached to
 * and Editable, its methods will be called on text changes. The class sets an error
 * on a Text Input Layout if the edit text is outside an allowed interval. It also
 * restricts the number of decimal points on the input.
 *
 * @property lb Lower bound on allowed input.
 * @property ub Upper bound on allowed input.
 * @property precision Number of decimals allowed.
 * @property view A TextInputLayout for error display.
 */
class TextChecker(
    private val lb: Double,
    private val ub: Double,
    private val precision: Int,
    private val view: TextInputLayout
) : TextWatcher {

    // Text before most recent change
    private var previousText = ""


    override fun afterTextChanged(s: Editable) {

        // Displays an error if the input is outside lb and ub
        if (s.isNotEmpty()) {

            // Check for special allowed inputs
            if (!(s.firstOrNull() == '-' && s.length == 1) && !(s.contains("-.") && s.length == 2) && !(s.firstOrNull() == '.' && s.length == 1)) {

                // Remove error or set error text
                if (s.toString().toDouble() in lb..ub) {
                    view.error = null
                } else {
                    view.error = view.resources.getString(R.string.error_interval, lb, ub)

                }
            }
        }

        // Check if user input exceeds the allowed precision
        if (s.contains('.')) {
            if (s.indexOf('.') + precision + 1 < s.length) {
                s.replace(0, s.length, previousText)
            }
        }

        // Remember previous text
        previousText = s.toString()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

}
