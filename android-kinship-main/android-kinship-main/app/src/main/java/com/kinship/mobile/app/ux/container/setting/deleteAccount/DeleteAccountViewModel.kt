package com.kinship.mobile.app.ux.container.setting.deleteAccount
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class DeleteAccountViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getDeleteAccountUiStateUseCase: DeleteAccountUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: DeleteAccountUiState = getDeleteAccountUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}