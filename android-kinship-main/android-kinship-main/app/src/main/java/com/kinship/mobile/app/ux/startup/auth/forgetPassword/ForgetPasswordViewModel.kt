package com.kinship.mobile.app.ux.startup.auth.forgetPassword
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ForgetPasswordViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getForgetPasswordUiStateUseCase: GetForgetPasswordUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    var uiState: ForgetPasswordUiState = getForgetPasswordUiStateUseCase(context = context, coroutineScope = viewModelScope){navigate(it)}


}