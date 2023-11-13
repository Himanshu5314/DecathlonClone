package com.example.decathlon.model

import com.google.gson.annotations.SerializedName

data class SearchResultItem(
    @SerializedName("name") val name: String,
    @SerializedName("id") val id: String,
    @SerializedName("price") val price: Float,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("brand") val brand: String
)
