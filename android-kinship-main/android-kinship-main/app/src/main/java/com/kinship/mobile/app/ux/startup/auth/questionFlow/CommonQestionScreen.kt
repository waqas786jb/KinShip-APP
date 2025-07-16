package com.kinship.mobile.app.ux.startup.auth.questionFlow
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.local.TempData.commonQuestionList
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

@Composable
fun CommonQuestionScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: CommonQuestionViewModel = hiltViewModel(),


) {
    var selectedQuestionIndex by rememberSaveable { mutableIntStateOf(-1) }
    val context = LocalContext.current.applicationContext
    val uiState = viewModel.uiState
    BackHandler(
        enabled = true, onBack = {}
    )
    AppScaffold(
        containerColor = White,
        modifier = Modifier.safeDrawingPadding(),
        topAppBar = {
            TopBarComponent(
                header = "",
                isBackVisible = false,
                )
        },
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {
                BottomButtonComponent(
                    text = stringResource(id = R.string.Continue),
                    onClick = {
                        when (selectedQuestionIndex) {
                            0 -> {
                                uiState.onConceive1Click()
                                uiState.conceiveValueSave()
                            }
                            1 -> {
                                uiState.onPregnantClick()
                                uiState.conceiveValueSave()
                            }
                            2 -> {
                                uiState.onBaby1Click()
                                uiState.babyValueSave()
                            }
                            3 -> {
                                uiState.onAdoptedClick()
                                uiState.adoptedValueSave()
                            }
                            else -> Toasty.warning(context,
                                context.getString(R.string.you_must_have_select_any_one_option), Toast.LENGTH_SHORT, false).show()
                        }

                    },
                    modifier = Modifier.padding(vertical = 20.dp, horizontal = 20.dp)
                )
            }
        )
    ) {
        CommonQuestionContent(
            onItemSelected = { index ->
                selectedQuestionIndex = index
            },
        )
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}
@Composable
fun CommonQuestionContent(
    modifier: Modifier = Modifier,
    onItemSelected: (Int) -> Unit
) {
    var selectedQuestionIndex by rememberSaveable { mutableIntStateOf(-1) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,

        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
    ) {
        Spacer(modifier = Modifier.padding(20.dp))
        QuestionTitle(
            header = stringResource(id = R.string.what_brings_you_to_kinship),
        )
        Spacer(modifier = modifier.padding(40.dp))
        QuestionSubTitle(
            header = stringResource(id = R.string.select_one),
            modifier = Modifier.padding(start = 20.dp)
        )
        QuestionList(
            options = commonQuestionList,
            selectedQuestionIndex = selectedQuestionIndex,
            onQuestionSelected = { index ->
                selectedQuestionIndex = index
                onItemSelected(index) // Notify the parent about item selection
            },
        )
        Log.d("TAG", "CommonQuestionContent: $onItemSelected")
    }
}
@Composable
fun QuestionList(
    options: List<String>,
    selectedQuestionIndex: Int,
    onQuestionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
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
                    onQuestionSelected(index) // Notify the parent about item selection
                    Log.d("TAG", "QuestionList: $index,$selectedQuestionIndex")

                },
            )
        }


    }
}



