@file:Suppress("unused")

package com.example.in2000_prosjektoppgave.util

import com.google.android.material.textfield.TextInputLayout
import java.text.DecimalFormat
import kotlin.math.*


/**
 * Function to check if the editText has a non empty text field. If the text field is set
 * an error is displayed.
 *
 * @param message Error string to display.
 */
fun TextInputLayout.checkEmpty(message: String) {
    if (this.editText?.text.toString().isEmpty()) {
        this.error = message
    }
}


/**
 * Converts an edit text to a Double type if not null.
 */
fun TextInputLayout.toDouble() = this.editText?.text.toString().toDoubleOrNull()


/**
 * Converts an edit text to a Double type if not null.
 */
fun TextInputLayout.toInt() = this.editText?.text.toString().toIntOrNull()


/**
 * Gets text of an edit text if not null.
 */
fun TextInputLayout.toText() = this.editText?.text.toString()


/**
 * Gets text of an edit text if not null.
 */
fun TextInputLayout.length() = this.editText?.text?.length ?: 0


/**
 * Function that returns the x-component of a vector with a size and direction.
 *
 * @param speed Speed absolute measure in given direction.
 * @param direction Direction measured in degrees.
 * @return
 */
fun convertToX(speed: Double, direction: Double): Double = -sin(direction / 180 * PI) * speed


/**
 * Function that returns the y-component of a vector with a size and direction.
 *
 * @param speed Speed absolute measure in given direction.
 * @param direction Direction measured in degrees.
 * @return
 */
fun convertToY(speed: Double, direction: Double): Double = -cos(direction / 180 * PI) * speed


/**
 * Function that converts degrees, minutes and seconds to decimal longitude and latitude.
 */
fun convertDMStoDD(degrees: Int, minutes: Int, seconds: Double): Double =
        degrees.toDouble() + (minutes.toDouble() / 60.0) + (seconds / 3600.0)


/**
 * Function that converts decimal longitude and latitude to degrees, minutes and seconds.
 */
fun convertDDtoDMSString(decimal: Double, coordinate: Coordinate): String {

    // Degrees
    val dd = abs(decimal)
    val degree = floor(dd).toInt()

    // Minutes
    val subMin = (dd - degree) * 60
    val min = floor(subMin).toInt()

    // Seconds
    val seconds = DecimalFormat("##.00").format((subMin - min) * 60)

    // Direction
    val direction = when (coordinate) {
        Coordinate.LONGITUDE -> if (decimal < 0) Direction.WEST else Direction.EAST
        Coordinate.LATITUDE -> if (decimal < 0) Direction.SOUTH else Direction.NORTH
    }

    return String.format("%d° %d' %s'' %s", degree, min, seconds, direction.dir)
}