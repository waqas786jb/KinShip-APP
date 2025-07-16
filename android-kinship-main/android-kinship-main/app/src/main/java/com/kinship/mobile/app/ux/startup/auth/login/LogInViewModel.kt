package com.kinship.mobile.app.ux.startup.auth.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getLogInUiStateUseCase: GetLogInUiStateUseCase
):ViewModel(),ViewModelNav by ViewModelNavImpl() {
    var uiState : LogInUiState = getLogInUiStateUseCase(context = context, coroutineScope = viewModelScope){navigate(it)}
}
