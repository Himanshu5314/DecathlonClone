package com.example.decathlon.model
import com.google.gson.annotations.SerializedName


data class NewSearchRequest (
    @SerializedName("searchString") val searchString: String? = null,
    @SerializedName("pageNumber") val pageNumber: Int? = null,
    @SerializedName("pageSize") val pageSize: Int? = null,
    @SerializedName("sortBy") val sortBy: String? = null
)