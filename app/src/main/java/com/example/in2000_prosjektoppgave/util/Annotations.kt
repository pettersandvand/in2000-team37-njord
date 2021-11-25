package com.example.in2000_prosjektoppgave.util

import javax.inject.Qualifier


/**
 * Annotation for OkHTTP client used in Hilt dependency injection.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DriftyClient


/**
 * Annotation for OkHTTP client used in Hilt dependency injection.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ForecastClient