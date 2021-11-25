package com.example.in2000_prosjektoppgave.util

import android.text.Editable


/**
 * Data class used to store information contained in edit texts, when navigating between
 * map search fragment and detail fragment.
 *
 * @property List List of editable.
 * @property topChip ID of top chip, default first chip.
 * @property subChip ID of sub chip, default first chip.
 * @property latitudeList List of latitude coordinates used by polygon selection.
 * @property longitudeList List of longitude coordinates used by polygon selection.
 * @property listText Displayed text in polygon selection.
 */
data class UserInput(
        val list: List<Editable?>,
        val topChip : Int = 1,
        val subChip : Int = 1,
        val latitudeList : List<Double> = emptyList(),
        val longitudeList : List<Double> = emptyList(),
        val listText : String = ""
)
