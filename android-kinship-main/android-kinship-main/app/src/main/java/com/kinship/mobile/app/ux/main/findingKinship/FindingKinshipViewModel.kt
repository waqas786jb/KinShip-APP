
package com.kinship.mobile.app.ux.main.findingKinship

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class FindingKinshipViewModel @Inject constructor(
    @ApplicationContext context: Context,
    getFindingKinshipUiStateUseCase: GetFindingKinshipUiStateUseCase
):ViewModel(),ViewModelNav by ViewModelNavImpl() {
    var uiState : FindingKinshipUiState = getFindingKinshipUiStateUseCase(context = context, coroutineScope = viewModelScope){navigate(it)}
}
