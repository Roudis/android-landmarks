package com.example.landmarkmanager.di

import com.example.landmarkmanager.data.repository.LandmarkRepository
import com.example.landmarkmanager.data.repository.LandmarkRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindLandmarkRepository(
        landmarkRepositoryImpl: LandmarkRepositoryImpl
    ): LandmarkRepository
} 