package com.kinship.mobile.app.ux.container.rsvp
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class RsvpViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getRsvpUiStateUseCase: GetRsvpUiStateUseCase,
    ) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
        val uiState: RsvpUiState = getRsvpUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}