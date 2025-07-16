package com.kinship.mobile.app.ux.main.message

import com.kinship.mobile.app.model.domain.response.chat.userGroup.MessageTabResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class MessageUiState(
    val clearAllApiResultFlow: () -> Unit = {},
    val noDataFoundText: StateFlow<Boolean> = MutableStateFlow(false),
    val onNoDataFound: (Boolean) -> Unit = {},
    val onNavigateToNewMessage:()-> Unit ={},

    var apiReload:String="",
    val messageListAPICall:()-> Unit ={},
    val navigateToSingleUserChat: (MessageTabResponse) -> Unit= {},
    val callMessageListAPI:()->Unit={},

    val showLoader: StateFlow<Boolean> = MutableStateFlow(false),
    val noChatAvailable: StateFlow<Boolean> = MutableStateFlow(false),

    val isLoading: StateFlow<Boolean> = MutableStateFlow(false),

    val apiUserMessageList: MutableStateFlow<List<MessageTabResponse>> = MutableStateFlow(emptyList()),

    val isAllEventsRefreshing: StateFlow<Boolean> = MutableStateFlow(false),


    )