package com.kinship.mobile.app.ux.startup.auth.questionFlow

import android.content.Context
import android.util.Log
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.model.domain.response.QuesRequest
import com.kinship.mobile.app.model.domain.response.QuestionCreateRequest
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.ux.startup.auth.questionFlow.adopted.Adopted1Route
import com.kinship.mobile.app.ux.startup.auth.questionFlow.babyQuestionFlow.Baby1Route
import com.kinship.mobile.app.ux.startup.auth.questionFlow.conceiveQuestionFlow.conceiveQuestion1.Conceive1Route
import com.kinship.mobile.app.ux.startup.auth.questionFlow.pregnantQuestionFlow.pregnantQuestion1.Pregnant1Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetCommonQuestionUiStateUseCase @Inject constructor(
    private val appPreferenceDataStore: AppPreferenceDataStore
) {
    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): CommonQuestionUiState {
        return CommonQuestionUiState(
            onConceive1Click = {
                navigate(NavigationAction.Navigate(Conceive1Route.createRoute()))
            },
            onPregnantClick = {
                navigate(NavigationAction.Navigate(Pregnant1Route.createRoute()))
            },

            onBaby1Click = {
                navigate(NavigationAction.Navigate(Baby1Route.createRoute()))
            },
            onAdoptedClick = {
                navigate(NavigationAction.Navigate(Adopted1Route.createRoute()))
            },

        )

    }
}