package com.kinship.mobile.app.ux.startup.auth.questionFlow.conceiveQuestionFlow.conceiveQuestion2

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class Conceive2ViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getConceive2UiStateUseCase: GetConceive2UiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    var uiState: Conceive2UiState = getConceive2UiStateUseCase(
        context = context,
        coroutineScope = viewModelScope
    ) { navigate(it) }

}