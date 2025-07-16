
package com.kinship.mobile.app.ux.container.communityFeature.searchCommunity

import androidx.paging.PagingData
import com.kinship.mobile.app.model.domain.response.chat.KinshipGroupChatListData
import com.kinship.mobile.app.model.domain.response.communities.MyCommunitiesResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class SearchCommunityUiState(
    val clearAllApiResultFlow: () -> Unit = {},
    val apiGalleryResultFlow: StateFlow<PagingData<KinshipGroupChatListData>> = MutableStateFlow(
        PagingData.empty()
    ),
    val searchMessageFlow: StateFlow<String> = MutableStateFlow(""),
    val onSearchMessageValueChange: (String) -> Unit = {},
    val showLoader: StateFlow<Boolean> = MutableStateFlow(false),
    val noDataFoundText: StateFlow<Boolean> = MutableStateFlow(false),
    val searchList: StateFlow<List<MyCommunitiesResponse>> = MutableStateFlow(emptyList()),
    val onBackClick: () -> Unit = {},
    val navigateToCommunityPost:(String,String)->Unit={_:String,_:String->},
    val myCommunitySearchAPICall:()->Unit={}
)
