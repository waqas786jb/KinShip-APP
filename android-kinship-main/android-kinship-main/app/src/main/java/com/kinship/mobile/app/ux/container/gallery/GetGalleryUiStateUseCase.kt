package com.kinship.mobile.app.ux.container.gallery

import android.content.Context
import androidx.paging.PagingData
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore

import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.response.chat.KinshipGroupChatListData
import com.kinship.mobile.app.model.domain.response.group.CreateGroup
import com.kinship.mobile.app.navigation.NavigationAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetGalleryUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore

) {
    private var groupId: String = ""

    private val apiGalleryList =
        MutableStateFlow<PagingData<KinshipGroupChatListData>>(
            PagingData.empty()
        )
    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): GalleryUiState {
        groupId= getCreateGroupData(coroutineScope)?.chatGroupId.toString()
        callKinshipGroupChatListAPI(coroutineScope = coroutineScope,
            id = getCreateGroupData(coroutineScope)?.chatGroupId?:"",
            type = Constants.Gallery.TYPE,
            imageOrLinkType = Constants.Gallery.IMAGE_LINK_TYPE, search = "" )
        return GalleryUiState(
            apiGalleryResultFlow = apiGalleryList,
            onBackClick = {
                navigate(NavigationAction.PopIntent)
            }
        )
    }
   private fun callKinshipGroupChatListAPI(coroutineScope: CoroutineScope, id: String, type: Int, imageOrLinkType: Int, search: String) {
        coroutineScope.launch {
            apiRepository.getKinshipGroupChatListWithPaging(id, type, imageOrLinkType, search , apiCallback = null).collect {
                apiGalleryList.value=it
            }
        }
    }
    private fun getCreateGroupData(coroutineScope: CoroutineScope): CreateGroup? {
        var data: CreateGroup? = null
        coroutineScope.launch { data = appPreferenceDataStore.getCreateGroupData() }
        return data
    }
}