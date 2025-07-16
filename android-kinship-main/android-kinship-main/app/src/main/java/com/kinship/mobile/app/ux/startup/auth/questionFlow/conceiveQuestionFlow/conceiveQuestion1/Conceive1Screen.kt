package com.kinship.mobile.app.ux.startup.auth.questionFlow.conceiveQuestionFlow.conceiveQuestion1
import android.widget.Toast
import androidx.compose.foundation.background
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
import com.kinship.mobile.app.ui.theme.White
import es.dmoral.toasty.Toasty

@Preview
@Composable
fun Conceive1Screen(
    navController: NavHostController = rememberNavController(),
    viewModel: Conceive1ViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current.applicationContext
    var selectedQuestionIndex by rememberSaveable { mutableIntStateOf(-1) }
    AppScaffold(
        modifier = Modifier.safeDrawingPadding(),
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = "",
                isBackVisible = true,
                onClick = {
                    uiState.onBackClick()
                },

            )
        },
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {
                BottomButtonComponent(
                    text = stringResource(id = R.string.Continue),
                    onClick = {
                        if (selectedQuestionIndex != -1) {
                            uiState.onConceive2Click()

                        } else {
                            Toasty.warning(context, context.getString(R.string.you_must_have_select_any_one_option), Toast.LENGTH_SHORT, false).show()

                        }
                    },
                    modifier = Modifier.padding(vertical = 20.dp, horizontal = 20.dp)
                )
            }
        )
    ) {
        Conceive1Content(
            modifier = Modifier,

            onItemSelected = { index ->
                selectedQuestionIndex = index

            },
            uiState
            )
    }

   HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
fun Conceive1Content(
    modifier: Modifier = Modifier,
    onItemSelected: (Int) -> Unit,
    uiState: Conceive1UiState
) {
    var selectedQuestionIndex by rememberSaveable { mutableIntStateOf(-1) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 20.dp)
            .background(color = White)
            .fillMaxWidth(),
    ) {
        Spacer(modifier = Modifier.padding(20.dp))
        QuestionTitle(header = stringResource(id = R.string.tell_us_about_your_fertility_journey))
        Spacer(modifier = modifier.padding(40.dp))
        QuestionSubTitle(
            header = stringResource(id = R.string.how_long_have_you_been_trying_to_conceive),
            modifier = Modifier.padding(start = 20.dp)
        )
        QuestionList(
            options = TempData.conceive1List,
            selectedQuestionIndex = selectedQuestionIndex,
            onQuestionSelected = { index ->
                selectedQuestionIndex = index
                onItemSelected(index) 

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
    uiState: Conceive1UiState,
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
                            uiState.onFirstValueSave()
                        }
                        1 -> {
                            uiState.onSecondValueValueSave()
                        }
                        2 -> {
                            uiState.onThirdValueValueSave()

                        }
                    }
                    onQuestionSelected(index) //parent about the selection of the item.
                },
            )
        }

    }
}

