package com.kinship.mobile.app.ux.startup.auth.questionFlow.pregnantQuestionFlow.pregnantQuestion1


import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.local.TempData
import com.kinship.mobile.app.model.domain.tabSelector.Tab
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.auth.QuestionSelector
import com.kinship.mobile.app.ui.compose.auth.QuestionTitle
import com.kinship.mobile.app.ui.compose.common.BottomButtonComponent
import com.kinship.mobile.app.ui.compose.common.CustomTabs
import com.kinship.mobile.app.ui.compose.common.CustomTabsDefaultYes
import com.kinship.mobile.app.ui.compose.common.DatePickerWithDialogFuture

import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.compose.common.OutlineTextFiledDateSelectComponent
import com.kinship.mobile.app.ui.networkresult.NetworkResultHandler
import com.kinship.mobile.app.ui.theme.Black23
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White
import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.Date

@Preview
@Composable
fun Pregnant1Screen(
    navController: NavHostController = rememberNavController(),
    viewModel: Pregnant1ViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
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
                modifier = Modifier.padding(start = 4.dp),

                )
        }
    ) {
        val context = LocalContext.current.applicationContext
        val apiResultFlow by uiState.apiResultFlow.collectAsStateWithLifecycle()
        apiResultFlow?.let {it1->
            NetworkResultHandler(
                networkResult = it1,
                onSuccess = {
                    uiState.onUserProfileClick()
                    uiState.clearAllApiResultFlow()
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
        Pregnant1ScreenContent(modifier = Modifier, uiState)
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
fun Pregnant1ScreenContent(modifier: Modifier = Modifier, uiState: Pregnant1UiState) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() })
            {
                keyboardController?.hide()
            }
            .fillMaxSize()
    ) {
        var optionSelected by rememberSaveable { mutableIntStateOf(0) }
        Spacer(modifier = Modifier.padding(20.dp))
        QuestionTitle(
            header = stringResource(R.string.tell_us_about_your_pregnancy),
        )
        Spacer(modifier = modifier.padding(40.dp))
        OutLineTextFiled(uiState)
        Spacer(modifier = Modifier.padding(17.dp))
        val singleMultiple = listOf(
            Tab("Single", painterResource(id = R.drawable.ic_single_person)),
            Tab("Multiple", painterResource(id = R.drawable.ic_multiple_person))
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            CustomTabs(
                tab = singleMultiple,
                isTitleShow = true,
                title = stringResource(id = R.string.did_you_have_a_single_or_multiple_pregnancy),
            ) { index ->
                optionSelected = index // // Update optionSelected based on tab selection
                when (index) {
                    0 -> {
                        uiState.onSingleClick()
                    }

                    1 -> {
                        uiState.onMultipleClick()
                    }
                }

            }
        }
        Spacer(modifier = Modifier.padding(17.dp))
        if (optionSelected == 0) {
            SinglePregnancy(uiState)
        } else {
            MultiplePregnancy(uiState)
        }
        Spacer(modifier = Modifier.padding(8.dp))
        val tabs = listOf(
            Tab(stringResource(id = R.string.no)),
            Tab(stringResource(id = R.string.yes)),

            )
        Row(modifier = Modifier.fillMaxWidth()) {
            CustomTabsDefaultYes(
                tab = tabs,
                title = stringResource(id = R.string.are_you_a_first_time_mom),

                ) { index ->
                when (index) {
                    0 -> {
                        uiState.onYesClick()
                    }

                    1 -> {
                        uiState.onNoClick()
                    }
                }


            }
        }
        Spacer(modifier = Modifier.padding(40.dp))
        BottomButtonComponent(
            text = stringResource(id = R.string.Continue),
            onClick = {
                uiState.onUserProfileAPICall()
            },
            modifier = Modifier.padding()
        )
        Spacer(modifier = Modifier.padding(10.dp))
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun OutLineTextFiled(uiState: Pregnant1UiState) {
    var selectedDate by rememberSaveable { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var temp by rememberSaveable { mutableStateOf<Long?>(null) }
    val dobError by uiState.dobErrorFlow.collectAsStateWithLifecycle()
    OutlineTextFiledDateSelectComponent(
        value = selectedDate,
        header = stringResource(id = R.string.select_your_date),
        showLeadingIcon = false,
        title = stringResource(id = R.string.when_is_your_due_date),
        errorMessage = dobError,
        leadingIcon = R.drawable.ic_calendar,
        onClick = {
            showDialog = true
        },
    )
    if (showDialog) {
        DatePickerWithDialogFuture(
            onSelectedDate = temp,
            onDateSelected = { dateString ->
                val initDate: Date? = SimpleDateFormat("dd/MM/yyyy").parse(dateString)
                val formatter = SimpleDateFormat("MM/dd/yyyy")
                selectedDate = initDate?.let { formatter.format(it) }.toString()
                uiState.onSelectedDateChange(dateString)
            },
            onDateSelectedLong = {
                temp = it
            },

            onDismiss = {
                showDialog = false
            }
        )
    }
}

@Composable
fun SinglePregnancy(uiState: Pregnant1UiState) {
    Column {
        var selectedQuestionIndex by rememberSaveable { mutableIntStateOf(-1) }
        Text(
            text = stringResource(R.string.are_you_having_a_boy_or_girl),
            modifier = Modifier
                .padding(start = 18.dp)
                .align(alignment = Alignment.Start),
            fontFamily = OpenSans,
            fontSize = 14.sp,
            color = Black23,
            fontWeight = FontWeight.W400,
        )
        PregnantSingleQuestionList(
            options = TempData.SingleList,
            selectedQuestionIndex = selectedQuestionIndex,
            onQuestionSelected = { index ->
                selectedQuestionIndex = index
            },
            modifier = Modifier,
            uiState,

            )

    }
}

@Composable
fun MultiplePregnancy(uiState: Pregnant1UiState) {
    Column {
        var selectedQuestionIndex by rememberSaveable { mutableIntStateOf(-1) }
        Text(
            text = stringResource(R.string.are_you_having_boys_or_girls_or_both),
            modifier = Modifier
                .padding(start = 18.dp)
                .align(alignment = Alignment.Start),
            fontFamily = OpenSans,
            fontSize = 14.sp,
            color = Black23,
            fontWeight = FontWeight.W400,
        )
        PregnantMultipleQuestionList(
            options = TempData.multiplePregnancyList,
            selectedQuestionIndex = selectedQuestionIndex,
            onQuestionSelected = { index ->
                selectedQuestionIndex = index
                selectedQuestionIndex = index
            },
            modifier = Modifier,
            uiState
        )
    }

}

@Composable
fun PregnantMultipleQuestionList(
    options: List<String>,
    selectedQuestionIndex: Int,
    onQuestionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    uiState: Pregnant1UiState

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
                    when (index) {
                        0 -> {
                            uiState.onMultipleGirlValue()
                        }

                        1 -> {
                            uiState.onMultipleBoyValue()
                        }

                        2 -> {
                            uiState.onMultipleBothValue()
                        }

                        3 -> {
                            uiState.onMultipleItSurpriseValue()
                        }
                    }
                    uiState.onMultipleGenderList(index.toString())
                    onQuestionSelected(index) // Notify the parent about item selection
                },

                )
        }
    }
}

@Composable
fun PregnantSingleQuestionList(
    options: List<String>,
    selectedQuestionIndex: Int,
    onQuestionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    uiState: Pregnant1UiState,
) {
    val questionError by uiState.questionErrorFlow.collectAsStateWithLifecycle()
    Column(
        modifier = modifier.fillMaxWidth(),
        //verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Spacer(modifier = Modifier.padding(3.dp))
        options.forEachIndexed { index, item ->
            QuestionSelector(
                header = item,
                isSelected = index == selectedQuestionIndex,

                onClick = {
                    when (index) {
                        0 -> {
                            uiState.onGirlValue()
                        }

                        1 -> {
                            uiState.onBoyValue()
                        }

                        2 -> {
                            uiState.onItSurpriseValue()

                        }
                    }
                    uiState.onSingleGenderList(index.toString())
                    onQuestionSelected(index) // Notify the parent about item selection
                },
                errorMessage = questionError
            )
        }
    }
}

@Preview
@Composable
fun Preview() {
    val uiState = Pregnant1UiState()
    Pregnant1ScreenContent(modifier = Modifier.background(color = White), uiState)

}







