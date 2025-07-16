package com.kinship.mobile.app.ux.container.likeMessage

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.paging.PagingData
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.request.resendOtp.IsLikeDislikeRequest
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.chat.KinshipGroupChatListData
import com.kinship.mobile.app.model.domain.response.chat.isLIkeDislike.IsLikeDislikeResponse
import com.kinship.mobile.app.model.domain.response.group.CreateGroup
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ux.container.ContainerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetLikeMessageUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore

) {
    private var groupId: String = ""
    private val isLikeDislikeId = MutableStateFlow("")

    private val apiLikeDisLikeList =
        MutableStateFlow<PagingData<KinshipGroupChatListData>>(
            PagingData.empty()
        )
    private val dislikedUserIds = MutableStateFlow<List<String>>(emptyList())
    private val apiResultFlow =
        MutableStateFlow<NetworkResult<ApiResponse<IsLikeDislikeResponse>>?>(null)

    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): LikeMessageUiState {

        groupId = getCreateGroupData(coroutineScope)?.chatGroupId.toString()
        callLikeDislikeGroupChatListAPI(
            coroutineScope = coroutineScope,
            id = getCreateGroupData(coroutineScope)?.chatGroupId ?: "",
            type = 3,
            imageOrLinkType = 0,
            search = ""
        )
        return LikeMessageUiState(
            apiLikeDislikeResultFlow = apiLikeDisLikeList,
            onBackClick = {
                navigateToWithOutBottomNavigationScreen(
                    context,
                    navigate,
                    Constants.ContainerScreens.GROUP_CHAT_SCREEN
                )
                //navigate(NavigationAction.Navigate(GroupChatRoute.createRoute(messageId = "", dislikeIds = dislikedUserIds.value)))
            },
            onIsLikeAndDislikeAPICall = {
                if (dislikedUserIds.value.contains(it)) {
                    // If the user is already disliked, remove them from the list
                    dislikedUserIds.value = dislikedUserIds.value.filter { it != it }
                } else {
                    // If the user is not disliked, add them to the list
                    dislikedUserIds.value += it
                }

                isLikeDislikeId.value = it
                makeIsLikeAndDislikeReq(coroutineScope)
                Log.d("TAG", "id: $it")
                Log.d("TAG", "dislikeId: ${dislikedUserIds.value}")
            },
            apiResultFlow = apiResultFlow,
        )
    }

    private fun navigateToWithOutBottomNavigationScreen(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        screenName: String
    ) {
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constants.IntentData.SCREEN_NAME, screenName)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }
    private fun makeIsLikeAndDislikeReq(coroutineScope: CoroutineScope) {
        val isLikeAndDislike = IsLikeDislikeRequest(
            messageId = isLikeDislikeId.value
        )
        callIsLikeAndDislike(coroutineScope, isLikeAndDislike)
    }

    private fun callIsLikeAndDislike(
        coroutineScope: CoroutineScope,
        isLikeAndDislike: IsLikeDislikeRequest,
    ) {
        Log.d("TAG", "callIsLikeAndDislike: ${isLikeDislikeId.value}")
        coroutineScope.launch {
            apiRepository.isLikeDislike(isLikeAndDislike).collect {
                apiResultFlow.value = it
            }
        }
    }
    private fun callLikeDislikeGroupChatListAPI(
        coroutineScope: CoroutineScope,
        id: String,
        type: Int,
        imageOrLinkType: Int,
        search: String
    ) {
        coroutineScope.launch {
            apiRepository.getKinshipDataChatList(id, type, imageOrLinkType, search).collect {
                apiLikeDisLikeList.value = it
            }
        }
    }
    private fun getCreateGroupData(coroutineScope: CoroutineScope): CreateGroup? {
        var data: CreateGroup? = null
        coroutineScope.launch { data = appPreferenceDataStore.getCreateGroupData() }
        return data
    }


}