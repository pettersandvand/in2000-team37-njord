package com.example.in2000_prosjektoppgave.di

import android.content.Context
import com.example.in2000_prosjektoppgave.ui.MainApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


/**
 * Hilt App module: Gives access to a context object.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Hilt provide singleton for context object.
     */
    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context): MainApplication {
        return app as MainApplication
    }

}