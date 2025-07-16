package com.kinship.mobile.app.ux.container.communityFeature.myCommunities

import com.kinship.mobile.app.model.domain.response.communities.MyCommunitiesResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class MyCommunitiesUiState(
    val clearAllApiResultFlow: () -> Unit = {},
    val onBackClick: () -> Unit = {},
    val communitiesList: StateFlow<List<MyCommunitiesResponse>> = MutableStateFlow(emptyList()),
    val showLoader: StateFlow<Boolean> = MutableStateFlow(false),
    val onClickOfNewSuggestion: () -> Unit = {},
    val onClickOfExploreCommunity: () -> Unit = {},
    val noDataFoundText: StateFlow<Boolean> = MutableStateFlow(false),
    val onClickSearchCommunity:()->Unit={},
    val navigateToCommunityPost:(String,String)->Unit={_:String,_:String->},
    val myCommunityAPICall:()->Unit={},
)
