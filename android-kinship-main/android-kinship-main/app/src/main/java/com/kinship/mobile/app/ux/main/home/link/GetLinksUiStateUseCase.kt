package com.kinship.mobile.app.ux.main.home.link

import androidx.paging.PagingData
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.response.chat.KinshipGroupChatListData
import com.kinship.mobile.app.model.domain.response.group.CreateGroup
import com.kinship.mobile.app.navigation.NavigationAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import javax.inject.Inject

class GetLinksUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore

) {
    private var groupId: String = ""
    private val _imageUrls = MutableStateFlow<Map<String, String>>(emptyMap())
    private val imageUrls: StateFlow<Map<String, String>> get() = _imageUrls

    private val apiLinkList =
        MutableStateFlow<PagingData<KinshipGroupChatListData>>(
            PagingData.empty()
        )

    operator fun invoke(
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): LinksUiState {
        groupId = getCreateGroupData(coroutineScope)?.chatGroupId.toString()
        callKinshipGroupChatListAPI(
            coroutineScope = coroutineScope,
            id = getCreateGroupData(coroutineScope)?.chatGroupId ?: "",
            type = 1,
            imageOrLinkType = 2,
            search = ""
        )
        return LinksUiState(
            apiLinkResultFlow = apiLinkList,
            generatePreviewImgFromUrl = {
                fetchImageUrl(message = it, coroutineScope = coroutineScope)
            },
            imageUrls = imageUrls,
            onBackClick = {
                navigate(NavigationAction.PopIntent)
            }

        )
    }
    private fun callKinshipGroupChatListAPI(
        coroutineScope: CoroutineScope,
        id: String,
        type: Int,
        imageOrLinkType: Int,
        search: String
    ) {
        coroutineScope.launch {
            apiRepository.getKinshipGroupChatListWithPaging(id, type, imageOrLinkType, search, apiCallback = null).collect {
                apiLinkList.value = it
            }
        }
    }
    private fun getCreateGroupData(coroutineScope: CoroutineScope): CreateGroup? {
        var data: CreateGroup? = null
        coroutineScope.launch { data = appPreferenceDataStore.getCreateGroupData() }
        return data
    }
    private fun fetchImageUrl(message: String?, coroutineScope: CoroutineScope) {
        if (message.isNullOrEmpty()) return
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val document = Jsoup.connect(message).get()
                val imageUrl = document.select("meta[property=og:image]").attr("content")
                _imageUrls.value += (message to imageUrl)
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }
}