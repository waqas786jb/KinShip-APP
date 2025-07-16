package com.kinship.mobile.app.ux.main.events.createEvent

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getCreateUiStateUseCase: GetCreateEventUiStateUseCase,
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: CreateEventUiState = getCreateUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}