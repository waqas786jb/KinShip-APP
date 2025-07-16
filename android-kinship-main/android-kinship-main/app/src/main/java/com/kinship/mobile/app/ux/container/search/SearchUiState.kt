
package com.kinship.mobile.app.ux.container.search

import androidx.paging.PagingData
import com.kinship.mobile.app.model.domain.response.chat.KinshipGroupChatListData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class SearchUiState(
    val searchMessageFlow: StateFlow<String> = MutableStateFlow(""),
    val onSearchMessageValueChange: (String) -> Unit = {},
    val clearAllApiResultFlow: () -> Unit = {},
    val apiSearchResultFlow: StateFlow<PagingData<KinshipGroupChatListData>> = MutableStateFlow(
        PagingData.empty()
    ),
    val navigateToGroupDetails:(String) -> Unit={},
    val onBackClick: () -> Unit = {},

    )
