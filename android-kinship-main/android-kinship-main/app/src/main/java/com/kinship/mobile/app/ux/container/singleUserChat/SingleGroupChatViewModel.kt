package com.kinship.mobile.app.ux.container.singleUserChat

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import com.kinship.mobile.app.ux.container.chat.GroupChatRoute
import com.kinship.mobile.app.ux.container.communityFeature.communityPost.CommunityPostRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SingleGroupChatViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getSingleGroupChatUiStateUseCase: GetSingleGroupChatUiStateUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    private val mainId: String = savedStateHandle.get<String>(SingleGroupChatRoute.Arg.MAIN_ID) ?: ""
    private val subId: String = savedStateHandle.get<String>(SingleGroupChatRoute.Arg.SUB_ID) ?: ""
    private val screenName: String = savedStateHandle.get<String>(SingleGroupChatRoute.Arg.SCREEN_NAME) ?: ""
    private val isSingle: Boolean =
        savedStateHandle.get<String>(SingleGroupChatRoute.Arg.SINGLE)?.toBoolean() ?: false
    val uiState: SingleGroupChatUiState = getSingleGroupChatUiStateUseCase(context = context, coroutineScope = viewModelScope, mainId = mainId, subId = subId, screenName = screenName, isSingle = isSingle) { navigate(it) }
}