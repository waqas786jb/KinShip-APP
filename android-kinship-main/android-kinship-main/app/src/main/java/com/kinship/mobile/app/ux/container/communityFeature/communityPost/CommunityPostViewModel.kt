package com.kinship.mobile.app.ux.container.communityFeature.communityPost

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import com.kinship.mobile.app.ux.container.chat.GroupChatRoute
import com.kinship.mobile.app.ux.container.chat.GroupChatUiState
import com.kinship.mobile.app.ux.container.communityFeature.suggestion.GetNewSuggestionUiStateUseCase
import com.kinship.mobile.app.ux.container.communityFeature.suggestion.NewSuggestionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class CommunityPostViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getCommunityPostUiStateUseCase: GetCommunityPostUiStateUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    private val communityId: String =
        savedStateHandle.get<String>(CommunityPostRoute.Arg.COMMUNITY_ID) ?: ""
    private val communityName: String =
        savedStateHandle.get<String>(CommunityPostRoute.Arg.COMMUNITY_NAME) ?: ""

    private val screenName: String =
        savedStateHandle.get<String>(CommunityPostRoute.Arg.SCREEN_NAME) ?: ""

    private val joinCommunity: Boolean =
        savedStateHandle.get<Boolean>(CommunityPostRoute.Arg.JOIN_COMMUNITY) ?: false

    private val notificationType: Int =
        savedStateHandle.get<Int>(CommunityPostRoute.Arg.NOTIFICATION_TYPE) ?: 0

    val uiState: CommunityPostUiState = getCommunityPostUiStateUseCase(
        context = context,
        coroutineScope = viewModelScope,
        communityId = communityId,
        communityName = communityName,
        joinCommunity = joinCommunity,
        screen = screenName,
        notificationType = notificationType
    ) { navigate(it) }
}

