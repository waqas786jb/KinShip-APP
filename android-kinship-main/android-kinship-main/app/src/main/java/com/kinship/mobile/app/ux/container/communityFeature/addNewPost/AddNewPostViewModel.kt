package com.kinship.mobile.app.ux.container.communityFeature.addNewPost

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import com.kinship.mobile.app.ux.container.chat.GroupChatRoute
import com.kinship.mobile.app.ux.container.communityFeature.communityPost.CommunityPostRoute
import com.kinship.mobile.app.ux.container.communityFeature.suggestion.GetNewSuggestionUiStateUseCase
import com.kinship.mobile.app.ux.container.communityFeature.suggestion.NewSuggestionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class AddNewPostViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getAddNewPostUiStateUseCase: GetAddNewPostUiStateUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    private val communityId: String =
        savedStateHandle.get<String>(AddNewPostRoute.Arg.COMMUNITY_ID) ?: ""
    val uiState: AddNewPostUiState = getAddNewPostUiStateUseCase(context = context, coroutineScope = viewModelScope, communityId = communityId) { navigate(it) }
}