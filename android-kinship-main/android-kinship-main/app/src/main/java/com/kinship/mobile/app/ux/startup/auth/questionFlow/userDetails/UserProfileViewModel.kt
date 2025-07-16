package com.kinship.mobile.app.ux.startup.auth.questionFlow.userDetails

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getUserDetailsUiStateUseCase: GetUserProfileUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    var uiState: UserProfileUiState =
        getUserDetailsUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }


}