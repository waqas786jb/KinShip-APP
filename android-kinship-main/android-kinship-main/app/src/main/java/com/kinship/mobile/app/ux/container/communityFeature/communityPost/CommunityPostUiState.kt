package com.kinship.mobile.app.ux.container.communityFeature.communityPost

import com.kinship.mobile.app.model.domain.response.communities.communityPost.CommunityPostResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class CommunityPostUiState(
    val showLoader: StateFlow<Boolean> = MutableStateFlow(false),
    val onBackClick: () -> Unit = {},
    val onCommunityValueChange:(String) -> Unit = {},
    val onCityValueChange:(String) -> Unit = {},
    val onIdeaValueChange:(String) -> Unit = {},
    val onJoinCommunityClick:() -> Unit = {},
    val noDataFoundText: StateFlow<Boolean> = MutableStateFlow(false),
    val communityName: StateFlow<String> = MutableStateFlow(""),
    val navigateToAddNewPost:()->Unit={},

    val joinCommunity: StateFlow<Boolean> = MutableStateFlow(false),

    val screenName: StateFlow<String> = MutableStateFlow(""),

    val communityId: StateFlow<String> = MutableStateFlow(""),

    val communityPostList: StateFlow<List<CommunityPostResponse>> = MutableStateFlow(emptyList()),
    val communityPostAPICall:()->Unit={},
    val navigateToCommentScreen:(CommunityPostResponse)->Unit={},
    val onLeaveKinshipClick:() ->Unit={},

    val openPickImgDialog: StateFlow<Boolean> = MutableStateFlow(false),
    val onOpenORDismissDialog: (Boolean) -> Unit = {},

    val isLoading: StateFlow<Boolean> = MutableStateFlow(false),


    val showLikeDislike: StateFlow<Boolean> = MutableStateFlow(false),
    val onShowLikeDislike: (Boolean) -> Unit = {},

    val isAllEventsRefreshing: StateFlow<Boolean> = MutableStateFlow(false),

    val showLikeCountDislike: StateFlow<Boolean> = MutableStateFlow(false),
    val onShowLikeCountDislike: (Boolean) -> Unit = {},




    val leaveCommunityAPICall:()->Unit={},
    val communityPostLikeDislike:(String,Boolean)->Unit={_:String,_:Boolean->},
)
