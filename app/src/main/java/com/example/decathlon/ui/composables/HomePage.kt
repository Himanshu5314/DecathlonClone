package com.example.decathlon.ui.composables

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.decathlon.model.SearchResultItem
import com.example.decathlon.state.SearchResultListState
import com.example.decathlon.viewmodel.HomePageViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomePage(modifier: Modifier = Modifier, viewModel: HomePageViewModel = hiltViewModel()) {
    val pagingItems: MutableState<LazyPagingItems<SearchResultItem>?> = remember {
        mutableStateOf(null)
    }
    if (viewModel.searchResultListPager != null) {
        pagingItems.value = viewModel.searchResultListPager?.collectAsLazyPagingItems()
    }
    val latestPageResponseState =
        viewModel.pagingListSource?.newSearchResultPageResponseData?.collectAsState()
    val refreshKeyState = viewModel.refreshSearchResultsFlow.collectAsState()
    val refreshCount = remember { mutableIntStateOf(0) }
    val refreshCountUpdated = remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }
    val scrollState = rememberLazyGridState()
    val showLoader = remember {
        mutableStateOf(true)
    }

    LaunchedEffect(refreshKeyState.value) {
        if (refreshKeyState.value > 0) {
            refreshCount.intValue = refreshKeyState.value
            refreshCountUpdated.value = true
        }
    }

    if (refreshCount.intValue > 0 && refreshCountUpdated.value) {
        refreshCountUpdated.value = false
        pagingItems.value = viewModel.searchResultListPager?.collectAsLazyPagingItems()
    }

    when (latestPageResponseState?.value) {
        is SearchResultListState.Success -> {
            if ((latestPageResponseState.value as? SearchResultListState.Success)?.data?.loadNextPage == false) {
                showLoader.value = false
            }
        }

        else -> {}
    }

    Column(modifier = modifier) {
        SearchField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            onSortByChange = { viewModel.updateSortBy(it) },
            onSearchValueChange = {
                viewModel.updateSearchQuery(it)
            },
            keyboardController = keyboardController
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            ComposeShimmer(modifier = Modifier.fillMaxSize())
            SearchResultList(
                scrollState, pagingItems.value!!, showLoader, keyboardController, viewModel
            )
        }
    }
}