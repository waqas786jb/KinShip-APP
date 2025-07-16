package com.kinship.mobile.app.ux.container.communityFeature.searchCommunity
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
class SearchCommunityViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getSearchCommunityUiStateUseCase: GetSearchCommunityUiStateUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
    private val searchKey: String = savedStateHandle.get<String>(SearchCommunityRoute.Arg.SEARCH_KEY) ?: ""
    val uiState: SearchCommunityUiState =  getSearchCommunityUiStateUseCase( context = context, coroutineScope = viewModelScope, searchKey = searchKey) { navigate(it) }


}