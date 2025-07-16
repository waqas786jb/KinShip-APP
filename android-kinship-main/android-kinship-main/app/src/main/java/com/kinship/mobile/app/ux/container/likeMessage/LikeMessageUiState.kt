package com.kinship.mobile.app.ux.container.likeMessage

import androidx.paging.PagingData
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.chat.KinshipGroupChatListData
import com.kinship.mobile.app.model.domain.response.chat.isLIkeDislike.IsLikeDislikeResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class LikeMessageUiState(
    val clearAllApiResultFlow: () -> Unit = {},
    val apiLikeDislikeResultFlow: StateFlow<PagingData<KinshipGroupChatListData>> = MutableStateFlow(
        PagingData.empty()
    ),
    val onBackClick: () -> Unit = {},
    val onIsLikeAndDislikeAPICall: (String) -> Unit = {},
    val likeDislikeId: (String) -> Unit = {},
    val apiResultFlow: StateFlow<NetworkResult<ApiResponse<IsLikeDislikeResponse>>?> = MutableStateFlow(
        null
    ),
)