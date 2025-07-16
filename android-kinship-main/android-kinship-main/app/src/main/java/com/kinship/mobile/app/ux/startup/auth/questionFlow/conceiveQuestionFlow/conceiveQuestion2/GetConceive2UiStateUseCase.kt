package com.kinship.mobile.app.ux.startup.auth.questionFlow.conceiveQuestionFlow.conceiveQuestion2
import android.content.Context
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.request.conceiveRequest.ConceiveRequest
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.signUp.UserAuthResponseData
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ux.startup.auth.questionFlow.userDetails.UserProfileRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetConceive2UiStateUseCase @Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore
) {
    private val apiResultFlow =
        MutableStateFlow<NetworkResult<ApiResponse<UserAuthResponseData>>?>(null)
    private val kinshipReason = MutableStateFlow(1)
    private var howAreYouTrying = MutableStateFlow(0)

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): Conceive2UiState {
        return Conceive2UiState(
            onUserProfileApiCall = {
                makeSignInReq(coroutineScope)
            },
            apiResultFlow = apiResultFlow,
            onUserProfileClick =
            {
                navigate(NavigationAction.Navigate(UserProfileRoute.createRoute()))
            },
            onBackClick = {
                navigate(NavigationAction.Pop())
            },

            howAreYouTryingFirst = {
                howAreYouTrying.value = 1
            },
            howAreYouTryingSecond = {
                howAreYouTrying.value = 2
            },
            howAreYouTryingThird = {
                howAreYouTrying.value = 3
            },
            clearAllApiResultFlow = {
                clearAllAPIResultFlow()
            }

        )
    }
    private fun makeSignInReq(coroutineScope: CoroutineScope) {
        coroutineScope.launch {

            val howLongYouAreTrying = appPreferenceDataStore.getQuestionData()?.howLongYouAreTrying
            val signInRequest = ConceiveRequest(
                step = 1,
                kinshipReason = kinshipReason.value,
                howLongYouAreTrying = howLongYouAreTrying ?: 0,
                howYouTrying = howAreYouTrying.value,
            )
            callSignInAPI(signInRequest, coroutineScope)
        }

    }
    private fun callSignInAPI(conceiveRequest: ConceiveRequest, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            apiRepository.conceiveRequest(conceiveRequest).collect {
                apiResultFlow.value = it
            }
        }
    }

    private fun clearAllAPIResultFlow() {
        apiResultFlow.value = null
    }
}