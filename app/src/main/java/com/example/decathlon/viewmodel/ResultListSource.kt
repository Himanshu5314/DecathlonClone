package com.example.decathlon.viewmodel
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.decathlon.model.NewSearchRequest
import com.example.decathlon.model.SearchResultItem
import com.example.decathlon.model.SearchResultResponse
import com.example.decathlon.network.model.RepoResult
import com.example.decathlon.repository.HomePageRepository
import com.example.decathlon.state.SearchResultListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class ResultListSource @Inject constructor(
    private val repository: HomePageRepository,
    var newSearchRequestState: NewSearchRequest
) : PagingSource<Int, SearchResultItem>() {

    private val _newSearchResultPageResponseData =
        MutableStateFlow<SearchResultListState>(SearchResultListState.Idle)
    val newSearchResultPageResponseData = _newSearchResultPageResponseData.asStateFlow()

    private val _currentPageNumber = MutableStateFlow(0)
    val currentPageNumber = _currentPageNumber.asStateFlow()
    companion object {
        const val RESULT_PAGE_ITEM_LIMIT = 20
        const val PRE_FETCH_DISTANCE = 10
        const val PAGINATION_START_KEY_VALUE = 0
        const val DEFAULT_SORT_BY = "NAME"
    }
    override fun getRefreshKey(state: PagingState<Int, SearchResultItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val page = state.closestPageToPosition(anchorPosition)
            page?.prevKey?.minus(1) ?: page?.nextKey?.plus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchResultItem> {
        when(params) {
            is LoadParams.Refresh -> {
                _currentPageNumber.update { PAGINATION_START_KEY_VALUE }
            }
            else -> {}
        }
        try {
            val currentKey = params.key ?: PAGINATION_START_KEY_VALUE
            var isValidResponse = false
            _currentPageNumber.update { currentKey }
            _newSearchResultPageResponseData.update { SearchResultListState.Loading }
            val response: RepoResult<SearchResultResponse> = repository.fetchSearchResults(
                newSearchRequestState.copy(pageNumber = currentKey)
            )

            if (response.errorMessage.isNullOrEmpty() && response.failureMessage.isNullOrEmpty() && response.data != null) {
                response.data?.let { pageData ->
                    isValidResponse = true
                    _newSearchResultPageResponseData.update { SearchResultListState.Success(pageData) }
                }
            } else {
                isValidResponse = false
                _newSearchResultPageResponseData.update { SearchResultListState.Failure("Something went wrong") }
                return LoadResult.Error(Throwable("Something went wrong"))
            }


            val hasNextPage = response.data?.loadNextPage ?: false
            val pageData = if (response.data != null) response.data!!.resultList else listOf(
            )
            return LoadResult.Page(
                data = pageData,
                prevKey = null,
                nextKey = if (hasNextPage) currentKey + 1 else null
            )

        } catch (exception: Exception) {
            _newSearchResultPageResponseData.update {
                SearchResultListState.Failure("Exception occurred")
            }
            return LoadResult.Error(throwable = exception)
        }
    }
}