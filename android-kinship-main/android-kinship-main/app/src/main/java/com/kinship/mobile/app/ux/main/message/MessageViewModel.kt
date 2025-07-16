package com.kinship.mobile.app.ux.main.message

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class MessageViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getMessageStateUseCase: GetMessageUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: MessageUiState = getMessageStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}