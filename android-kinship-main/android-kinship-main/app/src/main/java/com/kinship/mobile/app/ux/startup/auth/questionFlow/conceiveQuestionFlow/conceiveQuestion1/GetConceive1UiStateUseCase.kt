package com.kinship.mobile.app.ux.startup.auth.questionFlow.conceiveQuestionFlow.conceiveQuestion1
import android.content.Context
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.response.QuesRequest
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ux.startup.auth.questionFlow.conceiveQuestionFlow.conceiveQuestion2.Conceive2Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetConceive1UiStateUseCase @Inject constructor(
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore
) {

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,

        ): Conceive1UiState {
        return Conceive1UiState(
            onConceive2Click = {
                navigate(NavigationAction.Navigate(Conceive2Route.createRoute()))

                               },
            onFirstValueSave = {
                coroutineScope.launch {
                    appPreferenceDataStore.saveQuestionData(QuesRequest(howLongYouAreTrying = 1))
                }
            },
            onSecondValueValueSave = {
                coroutineScope.launch {
                    appPreferenceDataStore.saveQuestionData(QuesRequest(howLongYouAreTrying = 2))
                }
            },
            onThirdValueValueSave = {
                coroutineScope.launch {
                    appPreferenceDataStore.saveQuestionData(QuesRequest(howLongYouAreTrying = 3))
                }
            },
            onBackClick = {
                navigate(NavigationAction.Pop())


            },



        )
    }


}