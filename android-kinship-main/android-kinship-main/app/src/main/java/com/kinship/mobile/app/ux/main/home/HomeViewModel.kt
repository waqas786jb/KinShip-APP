package com.kinship.mobile.app.ux.main.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getHomeUiStateUseCase: GetHomeUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: HomeUiState = getHomeUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}