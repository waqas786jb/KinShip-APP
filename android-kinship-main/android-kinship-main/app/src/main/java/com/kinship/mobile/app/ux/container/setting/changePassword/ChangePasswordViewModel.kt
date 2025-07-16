package com.kinship.mobile.app.ux.container.setting.changePassword

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getChangePasswordUiStateUseCase: ChangePasswordUiStateUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    private var newPassword: String =
        savedStateHandle.get<String>(ChangePasswordRoute.Arg.NEW_PASSWORD_KEY)?:""
    val uiState: ChangePasswordUiState = if (newPassword.isEmpty()) {
        getChangePasswordUiStateUseCase(
            context = context,
            coroutineScope = viewModelScope,
            isBoolean = ""
        ) { navigate(it) }
    } else {
        getChangePasswordUiStateUseCase(
            context = context,
            coroutineScope = viewModelScope,
            isBoolean = newPassword
        ) { navigate(it) }
    }

}