package com.example.decathlon.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.decathlon.R
import com.example.decathlon.viewmodel.HomePageViewModel

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
