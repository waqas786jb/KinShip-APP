package com.kinship.mobile.app.ux.container.chat
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class GroupChatViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getGroupChatUiStateUseCase: GetGroupChatUiStateUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    private val messageId: String = savedStateHandle.get<String>(GroupChatRoute.Arg.MESSAGE_ID) ?: ""
    private val screenName: String = savedStateHandle.get<String>(GroupChatRoute.Arg.SCREEN_NAME) ?: ""
    private val subId: String = savedStateHandle.get<String>(GroupChatRoute.Arg.SUB_ID) ?: ""
    val uiState: GroupChatUiState = if (messageId.isEmpty()) {
        getGroupChatUiStateUseCase(context = context, coroutineScope = viewModelScope, messageId = "", screenName = "", subId = subId) { navigate(it) }
    } else {
        getGroupChatUiStateUseCase(messageId = messageId, context = context, coroutineScope = viewModelScope, screenName = screenName, subId = subId) { navigate(it) }
    }
}