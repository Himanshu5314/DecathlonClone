package com.example.decathlon.state

import com.example.decathlon.model.SearchResultResponse

sealed class SearchResultListState {
    class Success(val data: SearchResultResponse) : SearchResultListState()
    class Failure(val message: String) : SearchResultListState()
    class Error(val message: String) : SearchResultListState()
    object Loading : SearchResultListState()
    object Idle : SearchResultListState()
}