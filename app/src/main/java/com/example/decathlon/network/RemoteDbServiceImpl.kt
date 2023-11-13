package com.example.decathlon.network

import com.example.decathlon.model.NewSearchRequest
import com.example.decathlon.model.SearchResultItem
import com.example.decathlon.model.SearchResultResponse

class RemoteDbServiceImpl : RemoteDbService {
    private val mockItems = mutableListOf<SearchResultItem>()
    private val mockItemsByPrice = mutableListOf<SearchResultItem>()

    private var searchString = ""
    val imageUrls = listOf(
        "https://img.freepik.com/free-photo/isolated-handsome-bearded-man-hipster-outfit-dressed-jeans_285396-4781.jpg",
        "https://img.freepik.com/free-photo/casual-young-man-posing_144627-29198.jpg",
        "https://img.freepik.com/free-photo/clothes-set_1203-8109.jpg",
        "https://img.freepik.com/free-photo/elegance-color-apparel-clothes-clothing_1203-6544.jpg",
        "https://img.freepik.com/free-photo/jeans-shirt_1203-8170.jpg",
        "https://img.freepik.com/free-photo/portrait-stylish-handsome-young-man_155003-10194.jpg",
        "https://img.freepik.com/free-photo/man-beige-shirt-pants-casual-wear-fashion-full-body_53876-128784.jpg?size=626&ext=jpg&ga=GA1.1.1115390695.1699843388&semt=ais",
        "https://img.freepik.com/free-photo/portrait-young-handsome-model-man-dressed-gray-casual-hoodie-clothes-posing-white-wall-isolated_158538-6970.jpg",
        "https://img.freepik.com/free-photo/beautiful-men-fashion-fleece-jacket_1203-7648.jpg?size=626&ext=jpg&ga=GA1.1.1115390695.1699843388&semt=ais",
        "https://img.freepik.com/free-photo/full-portrait-smiling-happy-handsome-man-red-jacket-blue-jeans-gymshoes-beautiful-guy-standing-isolated-white_186202-3201.jpg?size=626&ext=jpg&ga=GA1.1.1115390695.1699843388&semt=ais",
        "https://img.freepik.com/premium-photo/summer-collection-men-clothes-set-with-checkered-shirt-jeans-shoes-belt-isolated-white-background_142957-1103.jpg"
    )

    init {
        repeat(2000) {
            val originalName = "Product ${2000-it}"
            val originalBrand = "Brand ${2000-it}"

            val item = SearchResultItem(
                name = originalName,
                id = it.toString(),
                price = (it * 10).toFloat(),
                imageUrl = randomImageUrlFromList(),
                brand = originalBrand
            )
            mockItems.add(item)
        }
        mockItemsByPrice.addAll(mockItems)
        mockItems.sortBy { it.name }
        mockItemsByPrice.sortBy { it.price }
    }

    override suspend fun fetchSearchResults(
        searchRequest: NewSearchRequest
    ): SearchResultResponse {
        //mock response helper
        return generateMockResponse(searchRequest)
    }

    suspend fun generateMockResponse(request: NewSearchRequest): SearchResultResponse {
        if(searchString != request.searchString) {
            searchString = request.searchString ?: ""
            mockItems.clear()
            mockItemsByPrice.clear()
        }
        val pageSize = request.pageSize ?: 20
        val totalItems = 2000
        val totalPages = (totalItems + pageSize - 1) / pageSize

        val pageNumber = request.pageNumber ?: 0
        val startIndex = (pageNumber) * pageSize
        val endIndex = minOf(startIndex + pageSize, totalItems - 1)

        val resultList =
            generateMockResultList(request.searchString, request.sortBy, startIndex, endIndex)

        return SearchResultResponse(resultList, loadNextPage = pageNumber < totalPages)
    }

    suspend fun generateMockResultList(
        searchString: String?,
        sortBy: String?,
        startIndex: Int,
        endIndex: Int
    ): List<SearchResultItem> {

        // Generate 2000 items for demonstration
        if(mockItems.size < 2000) {
            repeat(2000) {
                val originalName = "Product $it"
                val originalBrand = "Brand $it"

                // Simulate appending searchString randomly
                val shouldAppendSearchString = shouldAppendSearchString()
                val name =
                    if (!searchString.isNullOrEmpty() && shouldAppendSearchString) "$originalName $searchString" else originalName
                val brand =
                    if (!searchString.isNullOrEmpty() && !shouldAppendSearchString) "$originalBrand $searchString" else originalBrand

                val item = SearchResultItem(
                    name = name,
                    id = it.toString(),
                    price = (it * 10).toFloat(),
                    imageUrl = randomImageUrlFromList(),
                    brand = brand
                )
                mockItems.add(item)
            }
            mockItemsByPrice.addAll(mockItems)
            mockItems.sortBy { it.name }
            mockItemsByPrice.sortBy { it.price }
        }

        return when (sortBy) {
            "NAME" -> mockItems.subList(startIndex, endIndex)
            "PRICE" -> mockItemsByPrice.subList(startIndex, endIndex)
            else -> emptyList()
        }
    }

    private fun shouldAppendSearchString(): Boolean {
        // Adjust this logic based on how often you want to append searchString
        return (0..1).random() == 1
    }

    private fun randomImageUrlFromList(): String {
        return imageUrls.random()
    }
}