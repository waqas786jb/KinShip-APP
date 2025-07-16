package com.kinship.mobile.app.ux.container.newMessage
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class NewMessageViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getNewMessageUiStateUseCase: GetNewMessageUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: MessageNewMessageUiState = getNewMessageUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}