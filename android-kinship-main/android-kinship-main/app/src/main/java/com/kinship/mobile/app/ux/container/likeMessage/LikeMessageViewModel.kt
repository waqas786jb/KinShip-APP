package com.kinship.mobile.app.ux.container.likeMessage

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class LikeMessageViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getLikeMessageUiStateUseCase: GetLikeMessageUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: LikeMessageUiState = getLikeMessageUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}