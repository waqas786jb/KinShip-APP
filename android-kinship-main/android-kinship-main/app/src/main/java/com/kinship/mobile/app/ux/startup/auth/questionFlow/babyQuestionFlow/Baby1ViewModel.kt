package com.kinship.mobile.app.ux.startup.auth.questionFlow.babyQuestionFlow

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class Baby1ViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getBaby1UiStateUseCase: GetBaby1UiStateUseCase
):ViewModel(),ViewModelNav by ViewModelNavImpl() {
    var uiState :Baby1UiState=getBaby1UiStateUseCase(context = context, coroutineScope = viewModelScope){navigate(it)}
}