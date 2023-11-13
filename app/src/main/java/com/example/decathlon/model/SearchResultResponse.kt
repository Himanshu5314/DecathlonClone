package com.example.decathlon.model

import com.google.gson.annotations.SerializedName

data class SearchResultResponse(
    @SerializedName("resultList") val resultList: List<SearchResultItem>,
    @SerializedName("loadNextPage") val loadNextPage: Boolean,
)
