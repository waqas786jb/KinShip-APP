package com.kinship.mobile.app.ux.main.home.link

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class LinksViewModel
@Inject constructor(
    getLinkUiStateUseCase: GetLinksUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: LinksUiState = getLinkUiStateUseCase(coroutineScope = viewModelScope) { navigate(it) }
}