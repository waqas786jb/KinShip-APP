package com.kinship.mobile.app.ux.container.setting.help
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ContactSupportViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getContactSupportUiStateUseCase: ContactSupportUiStateUseCase
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: ContactSupportUiState = getContactSupportUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}