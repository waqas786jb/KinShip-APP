package com.kinship.mobile.app.ux.container.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.kinship.mobile.app.R
import com.kinship.mobile.app.model.domain.response.chat.KinshipGroupChatListData
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.HoneyFlower50
import com.kinship.mobile.app.ui.theme.White

@Preview
@Composable
fun SearchScreen(
    navController: NavController = rememberNavController(),
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    BackHandler {
        uiState.onBackClick()
    }
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = stringResource(R.string.search_message),
                isBackVisible = true,
                isLineVisible = true,
                onClick = {
                    uiState.onBackClick()
                }
            )
        }

    ) {
        SearchScreenContact(uiState)
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
fun SearchScreenContact(uiState: SearchUiState) {
    val searchList =
        uiState.apiSearchResultFlow.collectAsLazyPagingItems()
    val searchMessage by uiState.searchMessageFlow.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .padding(horizontal = 25.dp)
    ) {
        SearchField(uiState)
        searchList.loadState.refresh.apply {
            when (this) {
                is LoadState.Error -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.something_went_wrong),
                                fontSize = 16.sp,
                                maxLines = 1,
                                color = Black,
                                fontFamily = OpenSans,
                            )
                            Text(
                                modifier = Modifier.clickable {
                                    searchList.retry()
                                },
                                text = stringResource(id = R.string.tap_here_to_refresh_it),
                                fontSize = 16.sp,
                                maxLines = 1,
                                color = Black,
                                fontFamily = OpenSans,
                            )
                        }
                    }
                }

                LoadState.Loading -> {
                    CustomLoader()
                }

                is LoadState.NotLoading -> {
                    if (searchMessage.isNotEmpty()) {
                        val searchItem = searchList.itemSnapshotList.items.filter { item ->
                            item.message?.contains(searchMessage, ignoreCase = true) == true
                        }
                        if (searchItem.isNotEmpty()) {
                            LazyColumn {
                                items(searchItem) { item ->
                                    SearchMessageItem(item, searchMessage, uiState)
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.no_search_results_found),
                                        fontSize = 16.sp,
                                        maxLines = 1,
                                        color = Black,
                                        fontFamily = OpenSans,
                                    )
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.please_search_messages),
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    color = Black,
                                    fontFamily = OpenSans,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchField(uiState: SearchUiState) {
    val searchMessage by uiState.searchMessageFlow.collectAsStateWithLifecycle()
    Spacer(modifier = Modifier.padding(10.dp))
    Surface(
        shape = RoundedCornerShape(30.dp),
        border = BorderStroke(width = 1.dp, color = HoneyFlower50),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .background(color = White)
        ) {
            OutlinedTextField(
                value = searchMessage,
                onValueChange = { uiState.onSearchMessageValueChange(it) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = White,
                    unfocusedIndicatorColor = White,
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    cursorColor = Black,
                    disabledIndicatorColor = White
                ),
                modifier = Modifier,
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_message_search),
                        contentDescription = stringResource(R.string.searchmessage)
                    )
                },
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = true,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.search_message),
                        fontFamily = OpenSans,
                        fontWeight = FontWeight.W400,
                        fontSize = 12.sp,
                        color = Black50,
                    )
                }
            )
        }
    }
}

@Composable
fun SearchMessageItem(
    item: KinshipGroupChatListData,
    searchMessage: String,
    uiState: SearchUiState
) {
    Spacer(modifier = Modifier.padding(13.dp))
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            uiState.navigateToGroupDetails(item.id.toString())
        }) {
        Surface(
            shape = CircleShape,
            border = BorderStroke(width = 1.dp, color = AppThemeColor),
            modifier = Modifier
                .size(30.dp)
        ) {
            AsyncImage(
                model = item.profileImage,
                placeholder = painterResource(id = R.drawable.ic_placeholder),
                error = painterResource(id = R.drawable.ic_placeholder),
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
        }
        Column(
            modifier = Modifier
                .padding(start = 10.dp)

        ) {
            Text(
                text = if (item.name.isNullOrEmpty()) stringResource(id = R.string.you) else item.name,
                color = Black50,
                fontFamily = OpenSans,
                fontWeight = FontWeight.W600,
                fontSize = 10.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                text = highlightSearchTerm(item.message ?: "", searchMessage),
                fontFamily = OpenSans,
                fontWeight = FontWeight.W400,
                color = Black,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
fun highlightSearchTerm(text: String, searchTerm: String): AnnotatedString {
    val startIndex = text.indexOf(searchTerm, ignoreCase = true)
    if (startIndex == -1 || searchTerm.isEmpty()) {
        return AnnotatedString(text)
    }
    val endIndex = startIndex + searchTerm.length
    return buildAnnotatedString {
        append(text.substring(0, startIndex))
        withStyle(style = SpanStyle(color = AppThemeColor)) {
            append(text.substring(startIndex, endIndex))
        }
        append(text.substring(endIndex))
    }
}