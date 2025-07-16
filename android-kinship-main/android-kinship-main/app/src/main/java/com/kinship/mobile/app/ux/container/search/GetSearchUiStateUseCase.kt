package com.kinship.mobile.app.ux.container.search
import android.content.Context
import androidx.paging.PagingData
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.response.chat.KinshipGroupChatListData
import com.kinship.mobile.app.model.domain.response.group.CreateGroup
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ux.container.chat.GroupChatRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetSearchUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore

) {
    private var groupId: String = ""
    private val searchMessageFlow = MutableStateFlow("")
    private val apiSearchList =
        MutableStateFlow<PagingData<KinshipGroupChatListData>>(
            PagingData.empty()
        )

    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): SearchUiState {
        groupId = getCreateGroupData(coroutineScope)?.chatGroupId.toString()
        callSearchListListAPI(
            coroutineScope = coroutineScope,
            id = getCreateGroupData(coroutineScope)?.chatGroupId ?: "",
            type = 2,
            imageOrLinkType = 0, search = ""
        )
        //  performSearch(groupId,coroutineScope)
        return SearchUiState(
            apiSearchResultFlow = apiSearchList,
            searchMessageFlow = searchMessageFlow,
            onSearchMessageValueChange = {
                searchMessageFlow.value = it
            },
            onBackClick = {
                navigate(NavigationAction.PopIntent)
            },
            navigateToGroupDetails = {
                navigate(NavigationAction.Navigate(GroupChatRoute.createRoute(messageId = it, screenName = "", subId = "")))
            }
        )
    }
    private fun callSearchListListAPI(
        coroutineScope: CoroutineScope,
        id: String,
        type: Int,
        imageOrLinkType: Int,
        search: String
    ) {
        coroutineScope.launch {
            apiRepository.getKinshipGroupChatListWithPaging(id, type, imageOrLinkType, search, apiCallback = null).collect {
                apiSearchList.value = it
            }
        }
    }
    private fun getCreateGroupData(coroutineScope: CoroutineScope): CreateGroup? {
        var data: CreateGroup? = null
        coroutineScope.launch { data = appPreferenceDataStore.getCreateGroupData() }
        return data
    }

}