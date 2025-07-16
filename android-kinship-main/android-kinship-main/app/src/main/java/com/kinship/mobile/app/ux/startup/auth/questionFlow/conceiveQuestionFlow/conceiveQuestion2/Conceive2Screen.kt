package com.kinship.mobile.app.ux.startup.auth.questionFlow.conceiveQuestionFlow.conceiveQuestion2

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.local.TempData
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppNavBarData
import com.kinship.mobile.app.navigation.scaffold.AppNavBarType
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.auth.QuestionSelector
import com.kinship.mobile.app.ui.compose.auth.QuestionSubTitle
import com.kinship.mobile.app.ui.compose.auth.QuestionTitle
import com.kinship.mobile.app.ui.compose.common.BottomButtonComponent
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.networkresult.NetworkResultHandler
import com.kinship.mobile.app.ui.theme.White
import es.dmoral.toasty.Toasty

@Preview
@Composable
fun Conceive2Screen(
    navController: NavHostController = rememberNavController(),
    viewModel: Conceive2ViewModel = hiltViewModel(),
) {
    var selectedQuestionIndex by rememberSaveable { mutableIntStateOf(-1) }
    val uiState = viewModel.uiState
    val context = LocalContext.current.applicationContext
    AppScaffold(
        modifier = Modifier.safeDrawingPadding(),
        containerColor = White,
        topAppBar = {

            TopBarComponent(
                header = "",
                onClick = {
                    uiState.onBackClick()

                },
                isBackVisible = true
            )
        },
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {
                BottomButtonComponent(
                    text = stringResource(id = R.string.Continue),
                    onClick = {
                        if (selectedQuestionIndex != -1) {
                            uiState.onUserProfileApiCall()

                        } else {
                            Toasty.warning(context, context.getString(R.string.you_must_have_select_any_one_option), Toast.LENGTH_SHORT, false).show()
                        }

                    },
                    modifier = Modifier.padding(vertical = 20.dp, horizontal = 20.dp)

                )
            }
        )
    ) {
        Conceive2Content(
            modifier = Modifier,
            onItemSelected = { index ->
                selectedQuestionIndex = index
            },
            uiState
        )
        val apiResultFlow by uiState.apiResultFlow.collectAsStateWithLifecycle()
        apiResultFlow?.let {it1->
            NetworkResultHandler(
                networkResult = it1,
                onSuccess = { apiResponse ->
                    apiResponse.data?.let { it1 -> uiState.onUserProfileClick(it1) }
                    uiState.clearAllApiResultFlow()
                    //  uiState.(apiResponse.data)
                }, onError = {
                    Toasty.error(context, it, Toast.LENGTH_SHORT, false).show()
                    uiState.clearAllApiResultFlow()
                },
                onUnAuthenticated = {
                    Toasty.warning(context, it, Toast.LENGTH_SHORT, false).show()
                    uiState.clearAllApiResultFlow()
                }
            )
        }

    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
fun Conceive2Content(
    modifier: Modifier = Modifier,
    onItemSelected: (Int) -> Unit,
    uiState: Conceive2UiState
) {
    var selectedQuestionIndex by rememberSaveable { mutableIntStateOf(-1) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
    ) {
        Spacer(modifier = Modifier.padding(20.dp))
        QuestionTitle(header = stringResource(id = R.string.tell_us_about_your_fertility_journey))
        Spacer(modifier = modifier.padding(40.dp))
        QuestionSubTitle(
            header = stringResource(id = R.string.how_are_you_trying),
            modifier = Modifier.padding(start = 20.dp)
        )
        QuestionList(
            options = TempData.conceive2List,
            selectedQuestionIndex = selectedQuestionIndex,
            onQuestionSelected = { index ->
                selectedQuestionIndex = index
                onItemSelected(index) // Notify the parent about item selection
            },

            modifier = modifier,
            uiState
        )
    }
}

@Composable
fun QuestionList(
    options: List<String>,
    selectedQuestionIndex: Int,
    onQuestionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    uiState: Conceive2UiState,
) {
    Column(
        modifier = modifier.fillMaxWidth(),

        ) {
        Spacer(modifier = Modifier.padding(3.dp))
        options.forEachIndexed { index, item ->
            QuestionSelector(
                header = item,
                isSelected = index == selectedQuestionIndex,
                onClick = {
                    when(index){
                        0 -> {
                            uiState.howAreYouTryingFirst()
                        }
                        1 -> {
                            uiState.howAreYouTryingSecond()
                        }
                        2 -> {
                            uiState.howAreYouTryingThird()

                        }
                    }
                    onQuestionSelected(index) // Notify the parent about item selection

                },
            )
        }
    }
}
