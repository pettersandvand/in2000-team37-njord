package com.example.in2000_prosjektoppgave.util

/**
 * Enum used to decide where to insert bundled data in communication between
 * detail fragment and map search fragment.
 */
enum class Communication{
    POINT,
    CONE
}


/**
 * Enum used to distinguish between coordinate types .
 */
enum class Coordinate{
    LATITUDE,
    LONGITUDE
}


/**
 * Enum used to distinguish between compass directions.
 */
enum class Direction(val dir: String){
    NORTH("N"),
    SOUTH("S"),
    EAST("Ø"),
    WEST("V")
}


/**
 * Enum used to distinguish between different forecast types.
 */
enum class ForecastType{
    OCEAN,
    WEATHER
}


/**
 * Enum used to distinguish between different geographical input types.
 */
enum class GeoData{
    POINT,
    CONE,
    POLYGON
}