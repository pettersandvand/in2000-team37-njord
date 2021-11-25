package com.example.in2000_prosjektoppgave.ui

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Main application: Required for Hilt Dependency Injection.
 */
@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {

    // Sets up Hilt worker factory
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

}