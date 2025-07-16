package com.kinship.mobile.app.ux.container.likeMessage
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.model.domain.response.chat.KinshipGroupChatListData
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.networkresult.NetworkResultHandler
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.White
import es.dmoral.toasty.Toasty

@Preview
@Composable
fun LikeMessageScreen(
    navController: NavController = rememberNavController(),
    viewModel: LikeMessageViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    BackHandler {
        uiState.onBackClick()
    }
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = stringResource(id = R.string.liked_messages),
                isBackVisible = true,
                isLineVisible = true,
                onClick = {
                    uiState.onBackClick()
                },
            )
        }

    ) {
        LikeMessageContent(uiState)
        val context = LocalContext.current
        val apiResultFlow by uiState.apiResultFlow.collectAsStateWithLifecycle()
        apiResultFlow?.let { it1 ->
            NetworkResultHandler(
                networkResult = it1,
                onSuccess = {

                }, onError = {
                    Toasty.error(context, it, Toast.LENGTH_SHORT, false).show()

                },
                onUnAuthenticated = {
                    Toasty.warning(context, it, Toast.LENGTH_SHORT, false).show()

                }
            )
        }
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
fun LikeMessageContent(uiState: LikeMessageUiState) {
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .fillMaxSize(),
    ) {
        LikeMessageView(uiState)
    }
}
@Composable
fun LikeMessageView(uiState: LikeMessageUiState) {
    val lazyColumnListState = rememberLazyListState()
    val likeDislikeList =
        uiState.apiLikeDislikeResultFlow.collectAsLazyPagingItems()
    likeDislikeList.loadState.refresh.apply {
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
                                likeDislikeList.retry()
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
                if (likeDislikeList.itemCount > 0) {
                    LazyColumn(
                        state = lazyColumnListState,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(likeDislikeList.itemCount) { index ->
                            likeDislikeList[index]?.let {
                                LikeMessageItem(item = it, uiState)
                            }
                        }
                    }
                } else {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.there_are_no_liked_messages_yet),
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

@Composable
fun LikeMessageItem(item: KinshipGroupChatListData, uiState: LikeMessageUiState) {
    var checkedState by remember { mutableStateOf(item.isgrpLiked ?: false) }
    val context= LocalContext.current
    val checkedStateItem = if (checkedState) Pair(
        R.drawable.ic_filled_heart,
        "Checked"
    ) else Pair(R.drawable.ic_unfilled_heart, "Unchecked")
    var imagePreview by remember {
        mutableStateOf<Pair<Boolean, String?>>(
            Pair(
                false,
                null
            )
        )
    }

    Column {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(5.dp)
        ) {
            Surface(
                shape = CircleShape,
                border = BorderStroke(width = 1.dp, color = AppThemeColor),
                modifier = Modifier.size(30.dp)
            ) {
                AsyncImage(
                    model = item.profileImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.ic_placeholder),
                    placeholder = painterResource(R.drawable.ic_placeholder)
                )
            }
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = if (item.name.isNullOrEmpty()) "You" else item.name,
                    color = Black50,
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W600,
                    fontSize = 12.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )

                item.message?.let { message ->
                    if (message.isNotBlank()) {
                        val annotatedString = buildAnnotatedString {
                            val urlPattern =
                                "(https?://[\\w-]+(\\.[\\w-]+)+(/[#&\\w-./?%+=]*)?)|www\\.[\\w-]+(\\.[\\w-]+)+(/[\\w-./?%&=#]*)?"
                            val regex = Regex(urlPattern)
                            var lastIndex = 0
                            regex.findAll(message).forEach { matchResult ->
                                val start = matchResult.range.first
                                val end = matchResult.range.last + 1
                                if (start > lastIndex) {
                                    append(message.substring(lastIndex, start))
                                }
                                val url = message.substring(start, end)
                                val annotatedUrl = if (url.startsWith("www.")) {
                                    "http://$url"
                                } else {
                                    url
                                }
                                pushStringAnnotation(
                                    tag = "URL",
                                    annotation = annotatedUrl
                                )
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.Blue,
                                        textDecoration = TextDecoration.Underline
                                    )
                                ) {
                                    append(url)
                                }
                                pop()
                                lastIndex = end
                            }
                            if (lastIndex < message.length) {
                                append(message.substring(lastIndex))
                            }
                        }
                        ClickableText(
                            text = annotatedString,
                            style = TextStyle(
                                color = Black,
                                fontFamily = OpenSans,
                                fontWeight = FontWeight.W600,
                                fontSize = 13.sp,
                            ),
                            onClick = { offset ->
                                annotatedString.getStringAnnotations(
                                    tag = "URL",
                                    start = offset,
                                    end = offset
                                ).firstOrNull()?.let { annotation ->
                                    val intent =
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(annotation.item)
                                        )
                                    context.startActivity(intent)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }


                if (item.message.isNullOrEmpty() && item.image != null) {
                    Spacer(modifier = Modifier.height(5.dp))
                }
                if (item.type == Constants.Message.ONLY_IMAGE || item.type == Constants.Message.IMAGE_AND_MESSAGE) {
                    Surface(
                        border = BorderStroke(width = 1.dp, color = AppThemeColor),
                        shape = RoundedCornerShape(8.dp),
                        onClick = {
                            imagePreview = Pair(true, item.image)
                        },
                        modifier = Modifier.padding(start = 0.dp)
                    ) {
                        AsyncImage(
                            model = item.image,
                            contentDescription = null,
                            modifier = Modifier.size(95.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            Image(
                painter = painterResource(id = checkedStateItem.first),
                contentDescription = checkedStateItem.second,
                modifier = Modifier
                    .padding(top = 5.dp)
                    .size(20.dp)
                    .clickable {
                        checkedState = !checkedState
                        uiState.onIsLikeAndDislikeAPICall(item.id.toString())
                    }
            )
        }
    }
    if (imagePreview.first) {
        Dialog(onDismissRequest = { imagePreview = Pair(false, null) }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .clickable { imagePreview = Pair(false, null) },
            ) {
                AsyncImage(
                    model = imagePreview.second,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                )
            }
        }
    }
}