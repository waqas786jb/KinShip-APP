package com.kinship.mobile.app.ux.container.communityFeature.comment

import android.content.Context
import android.net.Uri
import androidx.compose.ui.platform.SoftwareKeyboardController
import com.kinship.mobile.app.model.domain.response.communities.communityPost.CommunityPostResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class CommentUiState(
    val clearAllApiResultFlow: () -> Unit = {},
    val onBackClick: () -> Unit = {},
    val commentList: StateFlow<List<CommunityPostResponse>> = MutableStateFlow(emptyList()),
    val showLoader: StateFlow<Boolean> = MutableStateFlow(false),
    val onClickOfNewSuggestion: () -> Unit = {},
    val onClickOfExploreCommunity: () -> Unit = {},
    val noDataFoundText: StateFlow<Boolean> = MutableStateFlow(false),
    val onClickSearchCommunity:()->Unit={},
    val navigateToCommunityPost:(String,String)->Unit={_:String,_:String->},
    val myCommunityAPICall:()->Unit={},
    val commentData: StateFlow<CommunityPostResponse> = MutableStateFlow(CommunityPostResponse()),

    val openPickImgDialog: StateFlow<Boolean> = MutableStateFlow(false),
    val onOpenORDismissDialog: (Boolean) -> Unit = {},

    val addNewPostAPICall:()->Unit={},

    val commentFlow: StateFlow<String> = MutableStateFlow(""),
    val onCommentValueChange: (String) -> Unit = {},

    val captureUri: StateFlow<Uri?> = MutableStateFlow(null),

    val onClearUnUsedUseState: () -> Unit = {},
    val onClickOfCamera: (Context) -> Unit = {},
    val launchCamera: StateFlow<Boolean> = MutableStateFlow(false),

    val onCommentSend: (SoftwareKeyboardController?) -> Unit = { },
    val communityPostLikeDislike:(String,Boolean)->Unit={_:String,_:Boolean->},
    val commentPostLikeDislike:(String,Boolean)->Unit={_:String,_:Boolean->},
    val screenName: StateFlow<String> = MutableStateFlow(""),
    val isLoader:StateFlow<Boolean> = MutableStateFlow(false),
    val commentListingAPICall:()->Unit={}


        // Add your comment sending logic here


    )
