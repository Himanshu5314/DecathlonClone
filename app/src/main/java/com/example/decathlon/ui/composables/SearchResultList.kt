package com.example.decathlon.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import com.example.decathlon.model.SearchResultItem
import com.example.decathlon.utils.getImageRequestBuilder
import com.example.decathlon.viewmodel.HomePageViewModel


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
