package com.kinship.mobile.app.ux.container.suggestion
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import com.kinship.mobile.app.ux.container.communityFeature.suggestion.GetNewSuggestionUiStateUseCase
import com.kinship.mobile.app.ux.container.communityFeature.suggestion.NewSuggestionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class NewSuggestionViewModel
@Inject constructor(
    @ApplicationContext context: Context,
    getNewSuggestionUiStateUseCase: GetNewSuggestionUiStateUseCase,
    ) : ViewModel(), ViewModelNav by ViewModelNavImpl() {
        val uiState: NewSuggestionUiState = getNewSuggestionUiStateUseCase(context = context, coroutineScope = viewModelScope) { navigate(it) }
}