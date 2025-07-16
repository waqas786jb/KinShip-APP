package com.kinship.mobile.app.ux.startup.auth.questionFlow.adopted


import android.annotation.SuppressLint
import android.widget.Toast
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
import com.kinship.mobile.app.data.source.local.TempData.adoptList
import com.kinship.mobile.app.model.domain.tabSelector.Tab
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppNavBarData
import com.kinship.mobile.app.navigation.scaffold.AppNavBarType
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.auth.QuestionSelector
import com.kinship.mobile.app.ui.compose.auth.QuestionTitle
import com.kinship.mobile.app.ui.compose.common.BottomButtonComponent
import com.kinship.mobile.app.ui.compose.common.CustomTabs
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
fun Adopted1Screen(
    navController: NavHostController = rememberNavController(),
    viewModel: Adopted1ViewModel = hiltViewModel(),
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
                BottomButtonComponent(
                    text = stringResource(id = R.string.Continue), onClick = {

                        uiState.onUserDetailsAPICall()
                    },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
                )
            }
        )
    ) {
        Adopted1ScreenContent(modifier = Modifier, uiState)
        val apiResultFlow by uiState.apiResultFlow.collectAsStateWithLifecycle()
        val context = LocalContext.current.applicationContext
        apiResultFlow?.let {it1->
            NetworkResultHandler(
                networkResult = it1,
                onSuccess = {
                    uiState.onUserDetailsAPICLick()
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
fun Adopted1ScreenContent(
    modifier: Modifier = Modifier,
    uiState: Adopted1UiState,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var selectedQuestionIndex by rememberSaveable { mutableIntStateOf(-1) }
    var optionSelected by rememberSaveable { mutableIntStateOf(0) }
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
        Spacer(modifier = Modifier.padding(20.dp))
        QuestionTitle(
            header = stringResource(id = R.string.tell_us_about_your_baby),
        )
        Spacer(modifier = modifier.padding(40.dp))
        AdoptedOutLineTextFiled(uiState)
        Spacer(modifier = Modifier.padding(17.dp))
        Text(
            text = stringResource(R.string.did_you_adopt_a_boy_or_girl),
            modifier = Modifier
                .padding(start = 18.dp)
                .align(alignment = Alignment.Start),
            fontFamily = OpenSans,
            fontSize = 14.sp,
            color = Black23,
            fontWeight = FontWeight.W400,
        )
        AdoptedQuestionList(
            options = adoptList,
            selectedQuestionIndex = selectedQuestionIndex,
            onQuestionSelected = { index ->
                selectedQuestionIndex = index
                selectedQuestionIndex = index
            },
            modifier = Modifier,
            uiState
        )
        val babyList = listOf(
            Tab(stringResource(id = R.string.no)),
            Tab(stringResource(id = R.string.yes))
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            CustomTabs(
                tab = babyList,
                isTitleShow = true,
                title = stringResource(id = R.string.does_your_child_have_special_needs)
            ) { index ->
                optionSelected = index
                when (optionSelected) {
                    0 -> {
                        uiState.onChildNoClick()
                    }
                    1 -> {
                        uiState.onChildYesClick()
                    }
                }
            }
        }
    }
}

@Composable
fun AdoptedQuestionList(
    options: List<String>,
    selectedQuestionIndex: Int,
    onQuestionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    uiState: Adopted1UiState,
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
                            uiState.onGirlValue()
                        }

                        1 -> {
                            uiState.onBoyValue()
                        }

                    }
                    uiState.onSingleGenderList(index.toString())
                    onQuestionSelected(index)

                },
            )
        }
    }
}


@SuppressLint("SimpleDateFormat")
@Composable
fun AdoptedOutLineTextFiled(uiState: Adopted1UiState) {
    var selectedDate by rememberSaveable { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    val dobError by uiState.dobErrorFlow.collectAsStateWithLifecycle()
    var temp by rememberSaveable { mutableStateOf<Long?>(null) }

    OutlineTextFiledDateSelectComponent(
        value = selectedDate,
        header = stringResource(id = R.string.select_your_date),
        showLeadingIcon = false,
        title = stringResource(id = R.string.what_is_your_child_s_date_of_birth),
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
                selectedDate= initDate?.let { formatter.format(it) }.toString()
                uiState.onSelectedDate(dateString)

            },
            onDateSelectedLong = {
                temp=it
            },

            onDismiss = {
                showDialog = false
            }
        )
    }
}
