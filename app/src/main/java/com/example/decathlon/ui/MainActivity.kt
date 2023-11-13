package com.example.decathlon.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.decathlon.R
import com.example.decathlon.model.SearchResultItem
import com.example.decathlon.state.SearchResultListState
import com.example.decathlon.utils.getImageRequestBuilder
import com.example.decathlon.viewmodel.HomePageViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content of MainActivity to Composable view
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
            ) {
                HomePage(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchResultList(
    scrollState: LazyGridState,
    searchResultList: LazyPagingItems<SearchResultItem>,
    showLoader: MutableState<Boolean>,
    keyboardController: SoftwareKeyboardController?,
    viewModel: HomePageViewModel
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = scrollState,
        modifier = Modifier.background(Color.White)
    ) {
        items(count = searchResultList.itemCount) { index ->
            val item = searchResultList[index]
            item?.let { searchResult ->
                SearchResultItemComposable(searchResult)
                if (index == searchResultList.itemCount - 1 && showLoader.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(28.dp)
                                .wrapContentSize(Alignment.Center),
                            color = Color.Blue
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultItemComposable(item: SearchResultItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding()
            .border(1.dp, Color.LightGray, shape = RoundedCornerShape(0.dp))
    ) {
        // Image
        AsyncImage(
            model = LocalContext.current.getImageRequestBuilder(data = item.imageUrl),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color.White),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 4.dp, bottom = 4.dp)) {
            // Brand Name
            Text(
                text = item.brand, style = MaterialTheme.typography.subtitle1, maxLines = 1, overflow = TextOverflow.Ellipsis
            )

            // Product Name
            Text(
                text = item.name,
                style = MaterialTheme.typography.body2.copy(
                    fontSize = 12.sp
                ),
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )

            // Row with Discounted Price and Actual Price
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Discounted Price
                Text(
                    text = item.price.toString(), style = MaterialTheme.typography.h6.copy(
                        fontWeight = FontWeight.Bold,
                        background = MaterialTheme.colors.secondary,
                        color = MaterialTheme.colors.onSecondary,
                        fontSize = 18.sp
                    ), modifier = Modifier.padding(end = 4.dp)
                )

                // Actual Price
                Text(
                    text = item.price.toString(), style = MaterialTheme.typography.body2, textDecoration = TextDecoration.LineThrough
                )
            }
        }
    }
}


@Composable
fun ComposeShimmer(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val shimmer = remember {
        ShimmerFrameLayout(context).apply {
            addView(LayoutInflater.from(context).inflate(R.layout.shimmer_placeholder, null))
        }
    }
    AndroidView(
        modifier = modifier,
        factory = { shimmer }
    ) { it.startShimmer() }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    onSearchValueChange: (String) -> Unit,
    onSortByChange: (String) -> Unit,
    keyboardController: SoftwareKeyboardController?,
    hint: String = stringResource(id = R.string.search_string),
    icon: ImageVector = Icons.Default.Search
) {
    val text = remember { mutableStateOf(TextFieldValue()) }
    val isNameSortSelected = remember { mutableStateOf(true) }
    Column {
        Box(
            modifier = modifier
                .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 12.dp)
                .height(48.dp)
                .background(Color.White)
                .border(1.dp, Color(0xFFBED7FF), shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = icon, contentDescription = null, tint = Color.Gray
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                    BasicTextField(
                        value = text.value.text,
                        onValueChange = {
                            text.value = TextFieldValue(it)
                            onSearchValueChange(it)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                        textStyle = TextStyle(fontSize = 16.sp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Placeholder text
                    if (text.value.text.isEmpty()) {
                        Text(
                            text = hint,
                            fontSize = 16.sp,
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.Gray.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
        Row(modifier = Modifier.height(48.dp)) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(color = if (isNameSortSelected.value) Color.LightGray else Color.White)
                    .clickable {
                        isNameSortSelected.value = true
                        onSortByChange(HomePageViewModel.SortByType.NAME.name)
                    }, contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.sort_by_name),
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.SemiBold),
                    textAlign = TextAlign.Center,
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(color = if (!isNameSortSelected.value) Color.LightGray else Color.White)
                    .clickable {
                        isNameSortSelected.value = false
                        onSortByChange(HomePageViewModel.SortByType.PRICE.name)
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(id = R.string.sort_by_price),
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.SemiBold),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ItemPreview() {
    HomePage()
}

