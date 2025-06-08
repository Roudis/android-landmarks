package com.example.landmarkmanager.di

import com.example.landmarkmanager.data.remote.AuthInterceptor
import com.example.landmarkmanager.data.remote.AuthService
import com.example.landmarkmanager.data.remote.LandmarkService
import com.example.landmarkmanager.data.api.LandmarkApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/")  // Android emulator localhost
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideLandmarkService(retrofit: Retrofit): LandmarkService {
        return retrofit.create(LandmarkService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideLandmarkApi(retrofit: Retrofit): LandmarkApi {
        return retrofit.create(LandmarkApi::class.java)
    }
} 