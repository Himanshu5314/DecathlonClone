package com.example.decathlon.di

import com.example.decathlon.network.RemoteDataSource
import com.example.decathlon.network.RemoteDbService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Singleton
    @Provides
    fun provideApiService(): RemoteDbService = RemoteDataSource.remoteDataService
}