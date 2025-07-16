package com.kinship.mobile.app.ux.startup.auth.questionFlow.adopted

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
@HiltViewModel
class Adopted1ViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getAdopted1UiStateUseCase: GetAdopted1UiStateUseCase
):ViewModel(),ViewModelNav by ViewModelNavImpl() {
    val uiState:Adopted1UiState=getAdopted1UiStateUseCase(context = context, coroutineScope = viewModelScope){navigate(it)}
}