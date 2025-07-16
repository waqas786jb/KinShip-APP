package com.kinship.mobile.app.ux.startup.auth.questionFlow.pregnantQuestionFlow.pregnantQuestion1

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class Pregnant1ViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getConceive2UiStateUseCase: GetPregnant1UiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    var uiState: Pregnant1UiState = getConceive2UiStateUseCase(
        context = context,
        coroutineScope = viewModelScope) { navigate(it) }

}