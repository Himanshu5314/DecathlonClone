package com.example.decathlon.repository

import com.example.decathlon.model.NewSearchRequest
import com.example.decathlon.model.SearchResultResponse
import com.example.decathlon.network.RemoteDbService
import com.example.decathlon.network.model.RepoResult
import kotlinx.coroutines.delay
import javax.inject.Inject

class HomePageRepository @Inject constructor(private val remoteDbService: RemoteDbService) {
    suspend fun fetchSearchResults(searchRequest: NewSearchRequest): RepoResult<SearchResultResponse> {
        val result = RepoResult(data = remoteDbService.fetchSearchResults(searchRequest))
        //delay to mock api response
        delay(1500)
        return result
    }
}