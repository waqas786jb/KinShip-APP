package com.kinship.mobile.app.ux.main.events

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class EventsViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getEventsUiStateUseCase: GetEventsUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: EventsUiState = getEventsUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}