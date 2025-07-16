package com.kinship.mobile.app.ux.main.home.link

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
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
import com.kinship.mobile.app.ui.theme.BlackLiteF0
import com.kinship.mobile.app.ui.theme.Blue30
import com.kinship.mobile.app.ui.theme.ColorLinkBg
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White
import es.dmoral.toasty.Toasty

@Preview
@Composable
fun LinkScreen(
    navController: NavController = rememberNavController(),
    viewModel: LinksViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = stringResource(id = R.string.links),
                isBackVisible = true,
                isLineVisible = true,
                onClick = {
                    uiState.onBackClick()
                },
            )
        }

    ) {
        LinkScreenContact(uiState)
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
fun LinkScreenContact(uiState: LinksUiState) {
    val linkList = uiState.apiLinkResultFlow.collectAsLazyPagingItems()
    val imageUrls by uiState.imageUrls.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .padding(horizontal = 25.dp, vertical = 20.dp)
            .background(White)
            .fillMaxSize()
    ) {
        linkList.loadState.refresh.apply {
            when (this) {
                LoadState.Loading -> {
                    CustomLoader()
                }
                is LoadState.NotLoading -> {
                    if (linkList.itemCount > 0) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            items(linkList.itemCount) { index ->
                                linkList[index].let {
                                    LaunchedEffect(key1 = it?.message) {
                                        uiState.generatePreviewImgFromUrl(it?.message ?: "")
                                    }
                                    val imgUrl = imageUrls[it?.message].orEmpty()

                                    LinkItem(item = it, imgUrl = imgUrl)

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
                                    text = stringResource(id = R.string.no_links_found),
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    color = Black,
                                    fontFamily = OpenSans,
                                )
                            }
                        }
                    }
                }

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
                                    linkList.retry()
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
            }
        }
    }
}

@Composable
fun LinkItem(item: KinshipGroupChatListData?, imgUrl: String) {
    val context = LocalContext.current
    val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse(item?.message)) }
    Surface(
        modifier = Modifier.clip(shape = RoundedCornerShape(10.dp))
    ) {
        Column(modifier = Modifier.background(BlackLiteF0)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = ColorLinkBg)
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imgUrl, contentDescription = null,
                    contentScale = ContentScale.Inside,
                    placeholder = painterResource(id = R.drawable.ic_link_placeholder),
                    error = painterResource(id = R.drawable.ic_link_placeholder),
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            if (item?.message?.startsWith("http") == true) {
                                context.startActivity(intent)
                            } else {
                                Toasty
                                    .warning(
                                        context,
                                        context.getString(R.string.not_a_valid_link),
                                        Toast.LENGTH_SHORT,
                                        false
                                    )
                                    .show()
                            }
                        }
                )
            }
            Spacer(modifier = Modifier.padding(5.dp))
            Text(
                text = if (item?.name.isNullOrEmpty()) stringResource(id = R.string.you) else item?.name ?: "",
                fontSize = 12.sp,
                color = Color.Black,
                fontFamily = OpenSans,
                fontWeight = FontWeight.W600,
                modifier = Modifier
                    .padding(start = 10.dp, end = 50.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier
                    .padding(start = 10.dp, top = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_event_link),
                    contentDescription = null,
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.padding(3.dp))

                item?.message?.let { message ->
                    val urlPattern =
                        "(https?://[\\w-]+(\\.[\\w-]+)+(/[#&\\w-./?%+=]*)?)|www\\.[\\w-]+(\\.[\\w-]+)+(/[\\w-./?%&=#]*)?"
                    val regex = Regex(urlPattern)
                    regex.findAll(message).forEach { matchResult ->
                        val url = matchResult.value
                        Text(
                            text = AnnotatedString(
                                url,
                                spanStyle = SpanStyle(
                                    color = Color.Blue,
                                    textDecoration = TextDecoration.Underline
                                )
                            ),
                            fontSize = 8.sp,
                            color = Blue30,
                            fontWeight = FontWeight.W600,
                            maxLines = 1,
                            modifier = Modifier
                                .padding(end = 40.dp),
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = OpenSans,
                        )
                    }
                }

            }
            Spacer(modifier = Modifier.padding(5.dp))
        }
    }
}

@Preview
@Composable
fun GalleryScreenContactPreview(modifier: Modifier = Modifier) {
    val uiState = LinksUiState()
    LinkScreenContact(uiState)
}


