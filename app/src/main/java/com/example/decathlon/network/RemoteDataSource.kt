package com.example.decathlon.network

object RemoteDataSource {
    lateinit var remoteDataService: RemoteDbService
    fun init() {
        remoteDataService = RemoteDbServiceImpl()
    }
}