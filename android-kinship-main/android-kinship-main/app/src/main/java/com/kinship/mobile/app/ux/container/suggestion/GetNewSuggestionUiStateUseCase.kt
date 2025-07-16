package com.kinship.mobile.app.ux.container.suggestion

import android.content.Context
import android.widget.Toast
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.request.community.NewSuggestionReq
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ux.container.communityFeature.suggestion.NewSuggestionUiState
import com.kinship.mobile.app.ux.container.communityFeature.suggestion.SuggestionDataState
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetNewSuggestionUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
) {
    private val suggestionState = MutableStateFlow(SuggestionDataState())
    private val showLoader = MutableStateFlow(false)

    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): NewSuggestionUiState {
        return com.kinship.mobile.app.ux.container.communityFeature.suggestion.NewSuggestionUiState(
            showLoader = showLoader,
            suggestionState = suggestionState,
            onCommunityValueChange = {
                suggestionState.update { state ->
                    state.copy(
                        communityName = it,
                        communityNameErrorMsg = if (it.isEmpty()) {
                            "Please provide community name!"
                        } else {
                            null
                        }
                    )
                }
            },
            onCityValueChange = {
                suggestionState.update { state ->
                    state.copy(
                        city = it,
                        cityErrorMsg = if (it.isEmpty()) {
                            "Please provide city!"
                        } else {
                            null
                        }
                    )
                }
            },
            onIdeaValueChange = {
                suggestionState.update { state ->
                    state.copy(
                        newIdea = it,
                        newIdeaErrorMsg = if (it.isEmpty()) {
                            "Please provide idea for new community!"
                        } else {
                            null
                        }
                    )
                }
            },
            onClickOfSend = {
                sendSuggestion(
                    coroutineScope = coroutineScope,
                    context = context,
                    navigate = navigate
                )
            },
            onBackClick = { navigate(NavigationAction.Pop()) }
        )
    }

    private fun sendSuggestion(coroutineScope: CoroutineScope, context: Context, navigate: (NavigationAction) -> Unit) {
        val suggestionStateVal = suggestionState.value
        val communityName = suggestionStateVal.communityName
        val city = suggestionStateVal.city
        val newIdea = suggestionStateVal.newIdea

        var hasAnyError = false
        if (communityName.isEmpty() || city.isEmpty() || newIdea.isEmpty()) {
            hasAnyError = true
        }

        if (hasAnyError) {
            suggestionState.update { newState ->
                newState.copy(
                    communityNameErrorMsg = if (communityName.isEmpty()) {
                        "Please provide community name!"
                    } else {
                        null
                    },
                    cityErrorMsg = if (city.isEmpty()) {
                        "Please provide city!"
                    } else {
                        null
                    },
                    newIdeaErrorMsg = if (newIdea.isEmpty()) {
                        "Please provide idea for new community!"
                    } else {
                        null
                    }
                )
            }

            return
        }

        coroutineScope.launch {
            val newSuggestionReq = NewSuggestionReq(
                name = suggestionStateVal.communityName,
                city = suggestionStateVal.city,
                idea = suggestionStateVal.newIdea
            )

            apiRepository.submitNewSuggestion(newSuggestionReq).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showLoader.value = false
                        showErrorMessage(context = context, errorMsg = it.message ?: "Something went wrong!")
                    }

                    is NetworkResult.Loading -> {
                        showLoader.value = true
                    }

                    is NetworkResult.Success -> {
                        showLoader.value = false
                        showSuccessMessage(context = context,
                            message = it.data?.message ?: "Suggestion sent successfully!")
                        navigate(NavigationAction.Pop())
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showLoader.value = false
                        showErrorMessage(
                            context = context,
                            errorMsg = it.message ?: "UnAuthenticated"
                        )
                    }

                    else -> {
                        showLoader.value = false
                    }
                }
            }
        }
    }

    private fun showErrorMessage(context: Context, errorMsg: String) {
        Toasty.error(context, errorMsg, Toast.LENGTH_SHORT, false).show()
    }

    private fun showSuccessMessage(context: Context, message: String) {
        Toasty.success(context, message, Toast.LENGTH_SHORT, false).show()
    }
}