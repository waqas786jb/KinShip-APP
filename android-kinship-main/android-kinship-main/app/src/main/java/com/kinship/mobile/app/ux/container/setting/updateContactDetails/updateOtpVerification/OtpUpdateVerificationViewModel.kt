package com.kinship.mobile.app.ux.container.setting.updateContactDetails.updateOtpVerification
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
class OtpUpdateVerificationViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getOtpVerificationUiStateUseCase: GetUpdateOtpVerificationUiStateUseCase,
    savedStateHandle: SavedStateHandle,
):ViewModel(),ViewModelNav by ViewModelNavImpl() {
   // private val email: String = savedStateHandle.requireString(OtpUpdateVerificationRoute.Arg.EMAIL)
   private val email: String = savedStateHandle.get<String>(OtpUpdateVerificationRoute.Arg.EMAIL) ?: ""
    val uiState : OtpUpdateVerificationUiState = if (email.isEmpty()){
        getOtpVerificationUiStateUseCase(email = "",context =context, coroutine = viewModelScope ){navigate(it)}
    }else{
        getOtpVerificationUiStateUseCase(email = email,context =context, coroutine = viewModelScope ){navigate(it)}
    }

}



