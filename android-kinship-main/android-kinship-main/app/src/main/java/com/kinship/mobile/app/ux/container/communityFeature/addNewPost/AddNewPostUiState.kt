package com.kinship.mobile.app.ux.container.communityFeature.addNewPost

import android.content.Context
import android.net.Uri
import com.kinship.mobile.app.model.domain.response.communities.communityPost.CommunityPostResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AddNewPostUiState(
    val showLoader: StateFlow<Boolean> = MutableStateFlow(false),
    val onBackClick: () -> Unit = {},
    val onCommunityValueChange:(String) -> Unit = {},
    val onCityValueChange:(String) -> Unit = {},
    val onIdeaValueChange:(String) -> Unit = {},
    val onClickOfSend:() -> Unit = {},
    val noDataFoundText: StateFlow<Boolean> = MutableStateFlow(false),
    val communityName: StateFlow<String> = MutableStateFlow(""),

    val postTitle: StateFlow<String> = MutableStateFlow(""),
    val onPostTitleValueChange:(String) -> Unit = {},

    val onImageFlag: (Boolean) -> Unit = {},

    val joinCommunity: StateFlow<Boolean> = MutableStateFlow(false),
    val communityPostList: StateFlow<List<CommunityPostResponse>> = MutableStateFlow(emptyList()),

    val openPickImgDialog: StateFlow<Boolean> = MutableStateFlow(false),
    val onOpenORDismissDialog: (Boolean) -> Unit = {},

    val addNewPostAPICall:()->Unit={},

    val communityPostFlow: StateFlow<String> = MutableStateFlow(""),
    val onCommunityPostImgPick: (String) -> Unit = {},

    val captureUri: StateFlow<Uri?> = MutableStateFlow(null),

    val onClearUnUsedUseState: () -> Unit = {},
    val onClickOfCamera: (Context) -> Unit = {},
    val launchCamera: StateFlow<Boolean> = MutableStateFlow(false),
)
