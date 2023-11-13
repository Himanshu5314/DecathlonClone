package com.example.decathlon.network

import com.example.decathlon.model.NewSearchRequest
import com.example.decathlon.model.SearchResultResponse

//1. Api to fetch a list of items on the home page in paginated manner.
//2. Api to fetch a sorted list, sort by name or price.
//3. Api to fetch filtered list of items, filtering will happen on the query
//that user types in the search box in UI. Filtering will happen against the
//name or the brand name of the item.

interface RemoteDbService {
    suspend fun fetchSearchResults(searchRequest: NewSearchRequest): SearchResultResponse?
}