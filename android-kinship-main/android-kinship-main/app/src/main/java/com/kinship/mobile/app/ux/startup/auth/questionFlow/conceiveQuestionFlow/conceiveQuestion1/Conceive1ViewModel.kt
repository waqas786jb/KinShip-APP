package com.kinship.mobile.app.ux.startup.auth.questionFlow.conceiveQuestionFlow.conceiveQuestion1

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class Conceive1ViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getConceive1UiStateUseCase: GetConceive1UiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    var uiState: Conceive1UiState = getConceive1UiStateUseCase(
        context = context,
        coroutineScope = viewModelScope
    ) { navigate(it) }

}