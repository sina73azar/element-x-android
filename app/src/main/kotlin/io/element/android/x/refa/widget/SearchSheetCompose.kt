package com.drp.shared_ui.widget

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.element.android.x.R
import com.drp.shared_ui.model.SearchSheetItemModel
import com.drp.utils.convertP2EDigits

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SearchSheet(
    sheetVisible: Boolean,
    isGrid: Boolean = true,
    gridCount: Int = 4,
    onDismiss: () -> Unit,
    items: List<SearchSheetItemModel>,
    onItemClick: (SearchSheetItemModel) -> Unit
) {

    var query by remember {
        mutableStateOf("")
    }
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val active by remember { mutableStateOf(true) }

    var dataList by remember {
        mutableStateOf(items)
    }
    if (sheetVisible) {


        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = modalBottomSheetState,
            modifier = Modifier.fillMaxWidth()/*.safeDrawingPadding()*/,
            containerColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {

            LaunchedEffect(key1 = query, block = {
                if (query.isEmpty()) {
                    dataList = items
                }
            })
            ProvideTextStyle(value = MaterialTheme.typography.bodyMedium) {
                SearchBar(
                    query = query,
                    onQueryChange = { newQuery ->
                        query = newQuery
                        if (newQuery.isEmpty()) {
                            dataList = items
                            return@SearchBar
                        } else {
                            dataList =
                                items.filter {
                                    it.value.contains(convertP2EDigits(newQuery)) || it.name?.contains(
                                        convertP2EDigits(newQuery)
                                    ) == true
                                }
                        }
                    },
                    onSearch = {
                    },
                    active = active,
                    enabled = true,
                    onActiveChange = { /*active = it*/ },
                    trailingIcon = {
                        if (query.isEmpty()) {
                            IconButton(onClick = { }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_search),
                                    contentDescription = ""
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    query = ""
                                }
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_close_fill),
                                    contentDescription = "close Icon",
                                    tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f)
                                )
                            }
                        }
                    },
                    colors = SearchBarDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        dividerColor = MaterialTheme.colorScheme.outline,
                        inputFieldColors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Yellow,
                            focusedContainerColor = Color.Yellow
                        )
                    ),
                    tonalElevation = 4.dp,
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.search_hint),
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.alpha(0.7f)
                        )
                    },
                    shape = ShapeDefaults.Large,
                    windowInsets = WindowInsets(12.dp)
                ) {
                    if (isGrid)
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(gridCount),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .scrollable(ScrollableState { it }, Orientation.Vertical)
                        ) {

                            items(dataList) { item ->
                                SearchGridSheetItem(model = item,
                                    onItemClick = { onItemClick(item) })
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    else
                        LazyColumn {
                            itemsIndexed(items = dataList) { index, item ->
                                SearchSheetItem(
                                    model = item,
                                    onItemClick = { onItemClick(item) }
                                )
                                if (index != dataList.lastIndex)
                                    CustomLine(
                                        modifier = Modifier.padding(
                                            horizontal = dimensionResource(
                                                id = R.dimen.medium_padding
                                            )
                                        ),
                                        dashed = true,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                            }
                        }
                }
            }
        }
    }
}

@Preview
@Composable
fun SheetPreview() {
    SearchSheet(
        sheetVisible = true,
        onDismiss = { /*TODO*/ },
        items = listOf(),
        onItemClick = {}
    )
}
