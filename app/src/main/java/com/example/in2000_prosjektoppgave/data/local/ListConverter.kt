package com.example.in2000_prosjektoppgave.data.local

import androidx.room.TypeConverter


/**
 * Class containing type converters for room database. The converters are used
 * to persist different types of lists in the database.
 */
class ListConverter {

    // Constants
    companion object{
        const val PATTERN = ";"
    }

    /**
     * Type converter for room database : Converts a string to a list of doubles.
     */
    @TypeConverter
    fun stringToDoubleList(data: String?): List<Double>? {
        return data?.let { it ->
            it.split(PATTERN).map {
                try {
                    it.toDouble()
                } catch (e: Exception) {
                    null
                }
            }
        }?.filterNotNull()
    }


    /**
     * Type converter for room database : Converts a list of doubles to a string.
     */
    @TypeConverter
    fun doubleListToString(doubles: List<Double>?): String? {
        return doubles?.joinToString(PATTERN)
    }


    /**
     * Type converter for room database : Converts a string to a list of ints.
     */
    @TypeConverter
    fun stringToIntList(data: String?): List<Int>? {
        return data?.let { it ->
            it.split(PATTERN).map {
                try {
                    it.toInt()
                } catch (e: Exception) {
                    null
                }
            }
        }?.filterNotNull()
    }


    /**
     * Type converter for room database : Converts a list of ints to a string.
     */
    @TypeConverter
    fun intListToString(ints: List<Int>?): String? {
        return ints?.joinToString(PATTERN)
    }


    /**
     * Type converter for room database : Converts a string to a list of strings.
     */
    @TypeConverter
    fun stringToStringList(data: String?): List<String>? {
        return data?.let { it ->
            it.split(PATTERN).map {
                try {
                    it
                } catch (e: Exception) {
                    null
                }
            }
        }?.filterNotNull()
    }


    /**
     * Type converter for room database : Converts a list of strings to a string.
     */
    @TypeConverter
    fun stringListToString(string: List<String>?): String? {
        return string?.joinToString(PATTERN)
    }

}