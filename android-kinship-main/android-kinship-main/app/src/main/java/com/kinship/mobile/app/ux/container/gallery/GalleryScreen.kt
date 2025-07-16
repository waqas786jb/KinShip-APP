package com.kinship.mobile.app.ux.container.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White

@Preview
@Composable
fun GalleryScreen(
    navController: NavController = rememberNavController(),
    viewModel: GalleryViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = stringResource(id = R.string.gallery),
                isBackVisible = true,
                isLineVisible = true,
                onClick = {
                    uiState.onBackClick()

                }
            )
        }

    ) {
        GalleryScreenContact(uiState)
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
fun GalleryScreenContact(uiState: GalleryUiState) {
    val galleryList = uiState.apiGalleryResultFlow.collectAsLazyPagingItems()
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .fillMaxSize()
    ) {
        galleryList.loadState.refresh.apply {
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
                                    galleryList.retry()
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
                    if (galleryList.itemCount > 0) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            items(galleryList.itemCount) { index ->
                                galleryList[index]?.let {
                                    GalleryItem(it)
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
                                    text = stringResource(R.string.no_images_found),
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
fun GalleryItem(item: KinshipGroupChatListData) {
    var imagePreview by remember { mutableStateOf<Pair<Boolean, String?>>(Pair(false, null)) }
    Surface(modifier = Modifier.padding()) {
        item.image.let {img->
            AsyncImage(
                model = item.image, contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_gallery_placeholder),
                error = painterResource(id = R.drawable.ic_gallery_placeholder),
                modifier = Modifier
                    .clip(RectangleShape)
                    .size(115.dp)
                    .clickable {
                        imagePreview = Pair(true, img)
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