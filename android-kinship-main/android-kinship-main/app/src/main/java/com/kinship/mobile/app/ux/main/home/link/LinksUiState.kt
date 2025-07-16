package com.kinship.mobile.app.ux.main.home.link

import androidx.paging.PagingData
import com.kinship.mobile.app.model.domain.response.chat.KinshipGroupChatListData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class LinksUiState(
    val clearAllApiResultFlow: () -> Unit = {},
    val imageUrls: StateFlow<Map<String, String>> = MutableStateFlow(emptyMap()),
    val apiLinkResultFlow: StateFlow<PagingData<KinshipGroupChatListData>> = MutableStateFlow(
        PagingData.empty()
    ),
    val onBackClick: () -> Unit = {},
    val generatePreviewImgFromUrl: (String) -> Unit = {}

)