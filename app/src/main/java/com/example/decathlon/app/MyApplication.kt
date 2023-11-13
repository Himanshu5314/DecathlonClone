package com.example.decathlon.app

import android.app.Application
import com.example.decathlon.network.RemoteDataSource
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        RemoteDataSource.init()
    }
}