package com.kinship.mobile.app.ux.container.communityFeature.suggestion

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class NewSuggestionUiState(
    val suggestionState: StateFlow<SuggestionDataState?> = MutableStateFlow(null),
    val showLoader: StateFlow<Boolean> = MutableStateFlow(false),

    val onBackClick: () -> Unit = {},
    val onCommunityValueChange:(String) -> Unit = {},
    val onCityValueChange:(String) -> Unit = {},
    val onIdeaValueChange:(String) -> Unit = {},
    val onClickOfSend:() -> Unit = {}
)

data class SuggestionDataState(
    val communityName: String = "",
    val communityNameErrorMsg: String? = null,
    val city: String = "",
    val cityErrorMsg: String? = null,
    val newIdea: String = "",
    val newIdeaErrorMsg: String? = null
)