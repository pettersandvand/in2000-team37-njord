package com.example.in2000_prosjektoppgave.util


/**
 * Sealed class to represent different states, and expose network status.
 * Three available states: success, error and loading. All states can hold a message.
 * Success and error states can also hold a object of type T.
 *
 * @param T Type for data.
 */
sealed class Resource<T> {

    /**
     * Success state.
     *
     * @param T Type for Resource.
     * @property data Data.
     * @property message A message.
     */
    data class Success<T>(val data: T? = null, val message: String? = null) : Resource<T>()

    /**
     * Error state.
     *
     * @param T Type for Resource.
     * @property data Data.
     * @property message A message.
     */
    data class Error<T>(val data: T? = null, val message: String? = null): Resource<T>()

    /**
     * Loading state.
     *
     * @param T Type for Resource.
     * @property message A message.
     */
    data class Loading<T>(val message: String? = null) : Resource<T>()
    
}