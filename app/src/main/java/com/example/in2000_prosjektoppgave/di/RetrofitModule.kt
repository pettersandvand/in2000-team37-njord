package com.example.in2000_prosjektoppgave.di

import com.example.in2000_prosjektoppgave.data.remote.met.MetService
import com.example.in2000_prosjektoppgave.data.remote.met.MetService.Companion.BASE_URL_MET
import com.example.in2000_prosjektoppgave.data.remote.drifty.DriftyService
import com.example.in2000_prosjektoppgave.data.remote.drifty.DriftyService.Companion.BASE_URL_DRIFTY
import com.example.in2000_prosjektoppgave.data.remote.drifty.DriftyService.Companion.PASSWORD
import com.example.in2000_prosjektoppgave.data.remote.drifty.DriftyService.Companion.USERNAME
import com.example.in2000_prosjektoppgave.data.remote.drifty.RetryInterceptor
import com.example.in2000_prosjektoppgave.data.remote.met.MetService.Companion.TEAM_NAME
import com.example.in2000_prosjektoppgave.data.remote.met.MetService.Companion.USER_AGENT
import com.example.in2000_prosjektoppgave.util.DriftyClient
import com.example.in2000_prosjektoppgave.util.ForecastClient
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


/**
 * Hilt Retrofit module: Used to create retrofit services and associated interceptors,
 * clients, and converter factories.
 */
@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    /**
     * Hilt provide for generic Gson object used by Retrofit objects.
     */
    @Provides
    fun provideGson(): Gson = GsonBuilder()
            .setLenient()
            .create()


    /**
     * Hilt provide for logging interceptor used by OkHttp clients.
     */
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BASIC)


    /**
     * Hilt provide singleton for interceptor used by drifty service OkHttp client.
     */
    @Provides
    @Singleton
    fun provideRetryInterceptor(): RetryInterceptor = RetryInterceptor()


    /**
     * Hilt provide singleton for OkHttp Client used by Met API retrofit object.
     */
    @ForecastClient
    @Provides
    @Singleton
    fun provideForecastClient(logger: HttpLoggingInterceptor): OkHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder().header(USER_AGENT, TEAM_NAME).build()
                chain.proceed(request)
            }
            .addInterceptor(logger)
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()


    /**
     * Hilt provide singleton for OkHttp Client used by drifty API retrofit object.
     */
    @DriftyClient
    @Provides
    @Singleton
    fun provideDriftyClient(logger: HttpLoggingInterceptor, retry: RetryInterceptor): OkHttpClient =
            OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val credentials = Credentials.basic(USERNAME, PASSWORD)
                        val request =
                                chain.request().newBuilder().header(DriftyService.AUTHORIZATION, credentials).build()
                        chain.proceed(request)
                    }
                    .addInterceptor(retry)
                    .addInterceptor(logger)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build()


    /**
     * Hilt provide singleton for retrofit service used for communication with Met API.
     */
    @Provides
    @Singleton
    fun provideMetService(@ForecastClient client: OkHttpClient, gson: Gson): MetService =
            Retrofit.Builder()
                    .baseUrl(BASE_URL_MET)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory())
                    .build()
                    .create(MetService::class.java)


    /**
     * Hilt provide singleton for retrofit service used for communication with Drifty API.
     */
    @Provides
    @Singleton
    fun provideDriftyService(@DriftyClient client: OkHttpClient, gson: Gson): DriftyService =
            Retrofit.Builder()
                    .baseUrl(BASE_URL_DRIFTY)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory())
                    .build()
                    .create(DriftyService::class.java)

}



