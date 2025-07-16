
package com.kinship.mobile.app.ux.container.gallery

import androidx.paging.PagingData
import com.kinship.mobile.app.model.domain.response.chat.KinshipGroupChatListData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class GalleryUiState(
    val clearAllApiResultFlow: () -> Unit = {},
    val apiGalleryResultFlow: StateFlow<PagingData<KinshipGroupChatListData>> = MutableStateFlow(
        PagingData.empty()
    ),
    val onBackClick: () -> Unit = {},
)
