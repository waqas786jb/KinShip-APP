package com.kinship.mobile.app.ux.startup.auth.otpVerfication

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class OtpVerificationViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getOtpVerificationUiStateUseCase:GetOtpVerificationUiStateUseCase
):ViewModel(),ViewModelNav by ViewModelNavImpl() {
    var uiState :OtpVerificationUiState=getOtpVerificationUiStateUseCase(context =context, coroutine = viewModelScope ){navigate(it)}


}