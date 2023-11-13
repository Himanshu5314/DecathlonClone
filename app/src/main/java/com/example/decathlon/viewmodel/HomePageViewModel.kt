package com.example.decathlon.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.decathlon.model.NewSearchRequest
import com.example.decathlon.model.SearchResultItem
import com.example.decathlon.repository.HomePageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(private val repository: HomePageRepository) :
    BaseViewModel() {
    private val _userSearchText = MutableStateFlow("")
    val userSearchText: StateFlow<String> = _userSearchText.asStateFlow()

    private var prevUserSearchQuery = ""

    var pagingListSource: ResultListSource? = null
    var searchResultListPager: Flow<PagingData<SearchResultItem>>? = null

    private var _refreshSearchResultsFlow = MutableStateFlow(0)
    var refreshSearchResultsFlow = _refreshSearchResultsFlow.asStateFlow()

    private val _sortByStateFlow = MutableStateFlow(ResultListSource.DEFAULT_SORT_BY)
    val sortByStateFlow = _sortByStateFlow.asStateFlow()

    init {
        initialisePaging()
        viewModelScope.launch {
            launch {
                userSearchText.debounce(500).collect { queryText ->
                    if (queryText != prevUserSearchQuery) {
                        prevUserSearchQuery = queryText
                        updateSearchResults(queryText)
                    }
                }
            }
        }
    }

    private fun initialisePaging() {
        pagingListSource = ResultListSource(
            repository, NewSearchRequest(
                searchString = "",
                pageNumber = 0,
                pageSize = ResultListSource.RESULT_PAGE_ITEM_LIMIT,
                sortBy = ResultListSource.DEFAULT_SORT_BY
            )
        )
        searchResultListPager = Pager(
            config = PagingConfig(
                pageSize = ResultListSource.RESULT_PAGE_ITEM_LIMIT,
                prefetchDistance = ResultListSource.PRE_FETCH_DISTANCE
            )
        ) { pagingListSource!! }.flow.cachedIn(scope = viewModelScope)
    }

    fun updateSearchQuery(queryText: String?) {
        viewModelScope.launch {
            if (queryText != null)
                _userSearchText.emit(queryText)
        }
    }

    private fun updateSearchResults(queryText: String?) {
        viewModelScope.launch {
            queryText?.let { text ->
                pagingListSource?.newSearchRequestState?.copy(
                    searchString = text, pageNumber = 0, sortBy = sortByStateFlow.value
                )?.let {
                    resetSearchResults(it)
                }
            }
        }
    }

    fun updateSortBy(sortBy: String) {
        viewModelScope.launch {
            if (sortBy != sortByStateFlow.value && sortBy == SortByType.NAME.name || sortBy == SortByType.PRICE.name) {
                _sortByStateFlow.emit(sortBy)
                pagingListSource?.newSearchRequestState?.copy(
                    sortBy = sortBy
                )?.let {
                    resetSearchResults(it)
                }
            }
        }
    }

    private fun resetSearchResults(newSearchRequest: NewSearchRequest) {
        viewModelScope.launch {
            pagingListSource = ResultListSource(
                repository, newSearchRequest
            )
            searchResultListPager = Pager(
                config = PagingConfig(
                    pageSize = ResultListSource.RESULT_PAGE_ITEM_LIMIT,
                    prefetchDistance = ResultListSource.PRE_FETCH_DISTANCE
                )
            ) { pagingListSource!! }.flow.cachedIn(scope = viewModelScope)
            _refreshSearchResultsFlow.emit(refreshSearchResultsFlow.value + 1)
        }
    }

    enum class SortByType {
        NAME, PRICE
    }
}