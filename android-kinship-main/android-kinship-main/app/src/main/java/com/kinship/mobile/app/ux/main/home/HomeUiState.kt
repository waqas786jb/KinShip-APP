package com.kinship.mobile.app.ux.main.home

import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.group.CreateGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class HomeUiState(
    //data
    val homeDataFlow: StateFlow<String> = MutableStateFlow(""),
    val homeGetCreateAPICall:() ->Unit ={},
    val userName: String = "",
    val userEmail: String = "",
    val kinshipReason: Int = 0,
    //apiResult
    val apiResultFlow: StateFlow<NetworkResult<ApiResponse<CreateGroup>>?> = MutableStateFlow(null),
    val clearAllApiResultFlow: () -> Unit = {},
    //event
    val onChatClick: () -> Unit = {},
    val navigateToMyCommunitiesScreen:()-> Unit ={},
    val onNotificationClick: () -> Unit = {},

    val onBack: () -> Unit = {},
    val onBackFinish: () -> Unit = {},
    val groupAPICall:() -> Unit = {},
    val clearBackEntry:()->  Unit ={},
    val appRestart:()->Unit={},


    val onSaveGroupData:(CreateGroup?) -> Unit = {},
)