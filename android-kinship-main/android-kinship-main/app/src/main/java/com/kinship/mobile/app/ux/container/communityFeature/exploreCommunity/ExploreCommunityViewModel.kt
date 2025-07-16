package com.kinship.mobile.app.ux.container.communityFeature.exploreCommunity
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
class ExploreCommunityViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getExploreCommunityUiStateUseCase: GetExploreCommunityUiStateUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    val uiState: ExploreCommunityUiState =  getExploreCommunityUiStateUseCase( context = context, coroutineScope = viewModelScope) { navigate(it) }


}