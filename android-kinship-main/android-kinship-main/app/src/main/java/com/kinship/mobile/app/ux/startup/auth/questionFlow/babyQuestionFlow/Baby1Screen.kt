package com.kinship.mobile.app.ux.startup.auth.questionFlow.babyQuestionFlow

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
import com.kinship.mobile.app.navigation.scaffold.AppNavBarData
import com.kinship.mobile.app.navigation.scaffold.AppNavBarType
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.auth.QuestionSelector
import com.kinship.mobile.app.ui.compose.auth.QuestionTitle
import com.kinship.mobile.app.ui.compose.common.BottomButtonComponent
import com.kinship.mobile.app.ui.compose.common.CustomTabs
import com.kinship.mobile.app.ui.compose.common.CustomTabsDefaultYes
import com.kinship.mobile.app.ui.compose.common.DatePickerWithDialog
import com.kinship.mobile.app.ui.compose.common.OutlineTextFiledDateSelectComponent
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.networkresult.NetworkResultHandler
import com.kinship.mobile.app.ui.theme.Black23
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White
import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.Date

@Preview
@Composable
fun Baby1Screen(
    navController: NavHostController = rememberNavController(),
    viewModel: Baby1ViewModel = hiltViewModel()
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
                modifier = Modifier.padding(start = 4.dp)
            )
        },
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {

            }
        )
    ) {
        Baby1ScreenContent(modifier = Modifier, uiState)
        val context = LocalContext.current.applicationContext
        val apiResultFlow by uiState.apiResultFlow.collectAsStateWithLifecycle()
        apiResultFlow?.let { it1 ->
            NetworkResultHandler(
                networkResult = it1,
                onSuccess = {
                    uiState.onUserDetailsCLick()
                    uiState.clearAllApiResultFlow()
                    //  uiState.onSignInSuccessfully(apiResponse.data)
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
fun Baby1ScreenContent(
    modifier: Modifier = Modifier,
    uiState: Baby1UiState,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var optionSelected by rememberSaveable { mutableIntStateOf(0) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 20.dp)
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() })
            {
                keyboardController?.hide()
            }
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.padding(20.dp))
        if (optionSelected == 0) {
            QuestionTitle(
                header = stringResource(R.string.tell_us_about_your_baby),
            )
        } else {
            QuestionTitle(
                header = stringResource(R.string.tell_us_about_your_baby_s),
            )
        }

        Spacer(modifier = modifier.padding(40.dp))
        val singleMultiple = listOf(
            Tab(stringResource(R.string.single), painterResource(id = R.drawable.ic_single_person)),
            Tab(
                stringResource(R.string.multiple),
                painterResource(id = R.drawable.ic_multiple_person)
            )
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            CustomTabs(
                tab = singleMultiple,
                isTitleShow = true,
                title = stringResource(R.string.did_you_have_a_single_or_multiple_birth)
            ) { index ->
                optionSelected = index
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
            BabySinglePregnancy(uiState)
        } else {
            BabyMultiplePregnancy(uiState)
        }
        BottomButtonComponent(
            text = stringResource(id = R.string.Continue),
            onClick = {
                uiState.onUserDetailsAPICall()
            },
        )
        Spacer(modifier = Modifier.padding(10.dp))
    }
}

@Composable
fun BabySinglePregnancy(uiState: Baby1UiState) {
    Column {
        var optionSelected by rememberSaveable { mutableIntStateOf(0) }
        var selectedQuestionIndex by rememberSaveable { mutableIntStateOf(-1) }
        Baby1OutLineTextFiledSingle(uiState)
        Spacer(modifier = Modifier.padding(17.dp))
        Text(
            text = stringResource(R.string.what_is_your_baby_s_sex),
            modifier = Modifier
                .padding(start = 18.dp)
                .align(alignment = Alignment.Start),
            fontFamily = OpenSans,
            fontSize = 14.sp,
            color = Black23,
            fontWeight = FontWeight.W400,
        )
        SingleBabyQuestionList(
            options = TempData.adoptList,
            selectedQuestionIndex = selectedQuestionIndex,
            onQuestionSelected = { index ->
                selectedQuestionIndex = index
                selectedQuestionIndex = index
            },
            modifier = Modifier,
            uiState
        )
        Spacer(modifier = Modifier.padding(8.dp))
        val babyList = listOf(
            Tab(stringResource(R.string.no)),
            Tab(stringResource(R.string.yes))
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            CustomTabs(
                tab = babyList,
                isTitleShow = true,
                title = stringResource(id = R.string.does_your_child_have_special_needs)
            ) { index ->
                optionSelected = index
                when (index) {
                    0 -> {
                        uiState.onChildNoClick()
                    }

                    1 -> {
                        uiState.onChildYesClick()
                    }
                }
                optionSelected = index
            }
        }
        Spacer(modifier = Modifier.padding(17.dp))
        val momList = listOf(
            Tab(stringResource(R.string.no)),
            Tab(stringResource(R.string.yes)),

            )
        Row(modifier = Modifier.fillMaxWidth()) {
            CustomTabsDefaultYes(
                tab = momList,
                title = stringResource(id = R.string.are_you_a_first_time_mom)
            ) { index ->
                optionSelected = index
                when (selectedQuestionIndex) {
                    0 -> {
                        uiState.onFirstTimeMomYesClick()
                    }

                    1 -> {

                        uiState.onFirstTimeMomNoClick()
                    }
                }
                optionSelected = index
            }
        }
        Spacer(modifier = Modifier.padding(40.dp))
    }
}

@Composable
fun SingleBabyQuestionList(
    options: List<String>,
    selectedQuestionIndex: Int,
    onQuestionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    uiState: Baby1UiState,
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
                            uiState.onSingleGirlValue()
                        }

                        1 -> {
                            uiState.onSingleBoyValue()

                        }
                    }
                    uiState.onSingleGenderList(index.toString())
                    onQuestionSelected(index)
                },
            )
        }
    }
}

@Composable
fun BabyMultiplePregnancy(uiState: Baby1UiState) {
    Column {
        var optionSelected by rememberSaveable { mutableIntStateOf(0) }
        var selectedQuestionIndex by rememberSaveable { mutableIntStateOf(-1) }
        Baby1OutLineTextFiledMultiple(uiState)
        Spacer(modifier = Modifier.padding(17.dp))
        Text(
            text = stringResource(id = R.string.did_you_have_boys_or_girls_or_both),
            modifier = Modifier
                .padding(start = 18.dp)
                .align(alignment = Alignment.Start),
            fontFamily = OpenSans,
            fontSize = 14.sp,
            color = Black23,
            fontWeight = FontWeight.W400,
        )
        MultipleBabyQuestionList(
            options = TempData.BabyMultiple,
            selectedQuestionIndex = selectedQuestionIndex,
            onQuestionSelected = { index ->
                selectedQuestionIndex = index
                selectedQuestionIndex = index
            },
            modifier = Modifier,
            uiState

        )

        Spacer(modifier = Modifier.padding(8.dp))
        val babyList = listOf(
            Tab(stringResource(id = R.string.no)),
            Tab(stringResource(id = R.string.yes))
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            CustomTabs(
                tab = babyList,
                isTitleShow = true,
                title = stringResource(R.string.do_any_of_your_children_have_special_needs)
            ) { index ->
                optionSelected = index
                when (index) {
                    0 -> {
                        uiState.onChildNoClick()
                    }

                    1 -> {
                        uiState.onChildYesClick()
                    }
                }
            }
        }
        Spacer(modifier = Modifier.padding(17.dp))
        val momList = listOf(
            Tab(stringResource(id = R.string.no)),
            Tab(stringResource(id = R.string.yes))
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            CustomTabsDefaultYes(
                tab = momList,
                title = stringResource(id = R.string.are_you_a_first_time_mom)
            ) { index ->
                optionSelected = index
                when (index) {
                    0 -> {
                        uiState.onFirstTimeMomNoClick()
                    }

                    1 -> {

                        uiState.onFirstTimeMomYesClick()
                    }
                }
            }
        }
        Spacer(modifier = Modifier.padding(40.dp))
    }
}

@Composable
fun MultipleBabyQuestionList(
    options: List<String>,
    selectedQuestionIndex: Int,
    onQuestionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    uiState: Baby1UiState,
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
                    }
                    uiState.onMultipleGenderList(index.toString())
                    onQuestionSelected(index)
                },
            )
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun Baby1OutLineTextFiledSingle(uiState: Baby1UiState) {
    Column {
        var selectedDate by rememberSaveable { mutableStateOf("") }
        var showDialog by remember { mutableStateOf(false) }
        var temp by rememberSaveable { mutableStateOf<Long?>(null) }
        val dobError by uiState.singleBabyBornDateErrorFlow.collectAsStateWithLifecycle()
        OutlineTextFiledDateSelectComponent(
            value = selectedDate,
            header = stringResource(id = R.string.select_your_date),
            showLeadingIcon = false,
            title = stringResource(id = R.string.when_was_your_baby_born),
            errorMessage = dobError,
            leadingIcon = R.drawable.ic_calendar,
            onClick = {
                showDialog = true
            },
        )
        if (showDialog) {
            DatePickerWithDialog(
                onSelectedDate = temp,
                onDateSelected = { dateString ->
                    val initDate: Date? = SimpleDateFormat("dd/MM/yyyy").parse(dateString)
                    val formatter = SimpleDateFormat("MM/dd/yyyy")
                    selectedDate = initDate?.let { formatter.format(it) }.toString()
                    uiState.onSingleBabyBornDate(dateString)
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
}

@SuppressLint("SimpleDateFormat")
@Composable
fun Baby1OutLineTextFiledMultiple(uiState: Baby1UiState) {
    Column {
        val firstDobError by uiState.multipleFirstBabyBornDateErrorFlow.collectAsStateWithLifecycle()
        val secondDobError by uiState.multipleSecondBabyBornDateErrorFlow.collectAsStateWithLifecycle()
        var temp by rememberSaveable { mutableStateOf<Long?>(null) }
        Spacer(modifier = Modifier.padding(3.dp))
        var firstSelectedDate by rememberSaveable { mutableStateOf("") }
        var firstShowDialog by remember { mutableStateOf(false) }
        OutlineTextFiledDateSelectComponent(
            value = firstSelectedDate,
            header = stringResource(id = R.string.select_your_date),
            showLeadingIcon = false,
            title = stringResource(id = R.string.when_were_your_babies_born),
            errorMessage = firstDobError,
            leadingIcon = R.drawable.ic_calendar,
            onClick = {
                firstShowDialog = true
            },
        )
        if (firstShowDialog) {
            DatePickerWithDialog(
                onSelectedDate = temp,
                onDateSelected = { dateString ->
                    firstSelectedDate = dateString
                    val initDate: Date? = SimpleDateFormat("dd/MM/yyyy").parse(dateString)
                    val formatter = SimpleDateFormat("MM/dd/yyyy")
                    firstSelectedDate = initDate?.let { formatter.format(it) }.toString()
                    uiState.onMultipleFirstBabyBornDate(dateString)
                },
                onDateSelectedLong = {
                    temp = it
                },
                onDismiss = {
                    firstShowDialog = false
                }
            )
        }
        Spacer(modifier = Modifier.padding(10.dp))
        var secondSelectedDate by rememberSaveable { mutableStateOf("") }
        var secondShowDialog by remember { mutableStateOf(false) }
        OutlineTextFiledDateSelectComponent(
            value = secondSelectedDate,
            header = stringResource(id = R.string.select_your_date),
            showLeadingIcon = false,
            errorMessage = secondDobError,
            leadingIcon = R.drawable.ic_calendar,
            onClick = {
                secondShowDialog = true
            },
        )
        if (secondShowDialog) {
            DatePickerWithDialog(
                onSelectedDate = temp,
                onDateSelected = { dateString ->
                    secondSelectedDate = dateString
                    val initDate: Date? = SimpleDateFormat("dd/MM/yyyy").parse(dateString)
                    val formatter = SimpleDateFormat("MM/dd/yyyy")
                    secondSelectedDate = initDate?.let { formatter.format(it) }.toString()
                    uiState.onMultipleSecondBabyBornDate(dateString)
                },
                onDateSelectedLong = {
                    temp = it
                },
                onDismiss = {
                    secondShowDialog = false
                }
            )
        }
    }
}

@Preview
@Composable
fun Preview() {
    val uiState = Baby1UiState()
    Baby1ScreenContent(uiState = uiState, modifier = Modifier.background(color = White))

}

