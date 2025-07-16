@file:Suppress("DEPRECATION")

package com.kinship.mobile.app.ux.startup.auth.questionFlow.userDetails

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.kinship.mobile.app.BuildConfig
import com.kinship.mobile.app.R
import com.kinship.mobile.app.model.domain.response.hobbiesData.HobbiesData
import com.kinship.mobile.app.model.domain.response.userStaticData.TempHobbyData
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.auth.QuestionTitle
import com.kinship.mobile.app.ui.compose.common.BottomButtonComponent
import com.kinship.mobile.app.ui.compose.common.CountryCodePickerComponent
import com.kinship.mobile.app.ui.compose.common.DatePickerWithDialog
import com.kinship.mobile.app.ui.compose.common.OutlineTextFieldWithLeadingAndTrailingComponent
import com.kinship.mobile.app.ui.compose.common.OutlineTextFieldWithOutTrailingIcon
import com.kinship.mobile.app.ui.compose.common.OutlineTextFiledDateSelectComponent
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.networkresult.NetworkResultHandler
import com.kinship.mobile.app.ui.theme.Alto
import com.kinship.mobile.app.ui.theme.Black23
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.White
import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.Date

@Preview
@Composable
fun UserProfileScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: UserProfileViewModel = hiltViewModel(),
) {
    var selectedQuestionIndex by remember { mutableIntStateOf(-1) }
    val uiState = viewModel.uiState
    val hobbyList: ArrayList<HobbiesData> = arrayListOf()
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
        }
    ) {
        val context = LocalContext.current.applicationContext
        val apiResultFlow by uiState.apiResultFlow.collectAsStateWithLifecycle()
        apiResultFlow?.let { it1 ->
            NetworkResultHandler(
                networkResult = it1,
                onSuccess = { apiResponse ->
                    uiState.onNavigateToHome(apiResponse.data)
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
        val apiHobbiesListResultFlow by uiState.apiHobbiesListResultFlow.collectAsStateWithLifecycle()
        apiHobbiesListResultFlow?.let { it1 ->
            NetworkResultHandler(
                networkResult = it1,
                onSuccess = { apiResponse ->
                    if (hobbyList.size == 0) {
                        hobbyList.addAll(apiResponse.data)
                    }
                },
                onError = {
                    uiState.clearAllApiResultFlow()
                },
                onUnAuthenticated = {
                    Toasty.warning(context, it, Toast.LENGTH_SHORT, true).show()
                    uiState.clearAllApiResultFlow()
                }
            )
        }
        UserProfileContent(
            onItemSelected = { index ->
                selectedQuestionIndex = index
            },
            hobbyList,
            modifier = Modifier,
            uiState
        )
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
fun UserProfileContent(
    onItemSelected: (Int) -> Unit,
    hobbyList: ArrayList<HobbiesData>,
    modifier: Modifier = Modifier,
    uiState: UserProfileUiState,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
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
        QuestionTitle(
            header = stringResource(id = R.string.let_s_get_to_know_each_other),
        )
        Spacer(modifier = Modifier.padding(35.dp))
        Text(
            text = stringResource(id = R.string.what_should_we_call_you),
            fontFamily = OpenSans,
            color = Black23,
            fontWeight = FontWeight.W400,
            modifier = Modifier
                .align(alignment = Alignment.Start)
                .padding(start = 15.dp),
        )
        OutLineTextFiled(uiState)
        Spacer(modifier = Modifier.padding(15.dp))
        Text(
            text = stringResource(R.string.what_are_you_obsessed_with_select_3_5),
            fontSize = 14.sp,
            fontFamily = OpenSans,
            color = Black23,
            fontWeight = FontWeight.W400,
            modifier = Modifier
                .align(alignment = Alignment.Start)
                .padding(start = 15.dp),
        )
        Spacer(modifier = Modifier.padding(10.dp))
        ObsessedList(uiState, list = hobbyList)
        Spacer(modifier = Modifier.padding(25.dp))
        BottomButtonComponent(text = stringResource(id = R.string.Continue), onClick = {
            uiState.onFindingKinshipClick()
        })
        Spacer(modifier = Modifier.padding(10.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ObsessedList(
    uiState: UserProfileUiState,
    list: ArrayList<HobbiesData>
) {
    val selectedItems = remember { mutableStateListOf<String>() }
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        list.forEach {
            val isSelected = selectedItems.contains(it._id)
            Box(
                modifier = Modifier
                    .clickable {
                        if (isSelected) {
                            selectedItems.remove(it._id)
                            uiState.onHobbyList(TempHobbyData(id = it._id, isAdd = false))
                        } else {
                            if (selectedItems.size < 5) {
                                if (!selectedItems.contains(it._id)) {
                                    selectedItems.add(it._id)
                                    uiState.onHobbyList(TempHobbyData(id = it._id, isAdd = true))
                                }
                            }
                        }
                    }
                    .border(
                        width = 1.dp,
                        color = Alto,
                        shape = RoundedCornerShape(56.dp)
                    )
                    .background(if (isSelected) Alto else White, shape = CircleShape)

            ) {
                Text(
                    text = it.name,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Default,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 7.dp, vertical = 7.dp)
                )
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun OutLineTextFiled(uiState: UserProfileUiState) {
    val firstName by uiState.firstNameFlow.collectAsStateWithLifecycle()
    val firstNameError by uiState.firstNameErrorFlow.collectAsStateWithLifecycle()
    val lastName by uiState.lastNameFlow.collectAsStateWithLifecycle()
    val lastNameError by uiState.lastNameErrorFlow.collectAsStateWithLifecycle()
    val phoneNumber by uiState.phoneNumberFlow.collectAsStateWithLifecycle()
    val phoneNumberError by uiState.phoneNumberErrorFlow.collectAsStateWithLifecycle()
    val dobError by uiState.dobErrorFlow.collectAsStateWithLifecycle()
    val zipCode by uiState.zipCodeFlow.collectAsStateWithLifecycle()
    val cityFlow by uiState.cityFlow.collectAsStateWithLifecycle()
    val cityFlowErrorFlow by uiState.cityErrorFlow.collectAsStateWithLifecycle()
    val zipCodeError by uiState.zipCodeErrorFlow.collectAsStateWithLifecycle()
    var selectedDate by remember { mutableStateOf("") }
    var temp by rememberSaveable { mutableStateOf<Long?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val field = listOf(Place.Field.NAME, Place.Field.LAT_LNG)
    LaunchedEffect(Unit) {
        Places.initialize(context, BuildConfig.MAP_API_KEY)
    }
    val intent =
        Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, field).build(context)
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.let {
                    val place = Autocomplete.getPlaceFromIntent(it)
                    place.name?.let { it1 -> uiState.onCityValueChanges(it1) }
                }
            } else if (result.resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = result.data?.let { Autocomplete.getStatusFromIntent(it) }
                Log.e("Autocomplete Error", status?.statusMessage ?: "Error")
            }
        }

    Spacer(modifier = Modifier.padding(4.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    )
    {
        Column(
            modifier = Modifier
                .weight(1f)
                .background(color = White)
        ) {
            OutlineTextFieldWithOutTrailingIcon(
                value = firstName,
                onValueChange = {
                    val filteredValue = it.filter { char -> char.isLetter() || char.isWhitespace() }
                    uiState.onFirstNameValueChange(filteredValue)
                },
                isLeadingIconVisible = true,
                errorMessage = firstNameError,
                header = stringResource(id = R.string.first_name),
                leadingIcon = R.drawable.ic_person,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Password,
                    capitalization = KeyboardCapitalization.Sentences,

                )
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .background(color = White)
        ) {
            OutlineTextFieldWithOutTrailingIcon(
                value = lastName,
                onValueChange = {
                    val filteredValue = it.filter { char -> char.isLetter() || char.isWhitespace() }
                    uiState.onLastNameValueChange(filteredValue)
                },
                isLeadingIconVisible = true,
                errorMessage = lastNameError,
                header = stringResource(id = R.string.last_name),
                leadingIcon = R.drawable.ic_person,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Password,
                    capitalization = KeyboardCapitalization.Sentences
                ),
            )
        }
    }
    Spacer(modifier = Modifier.padding(15.dp))
    CountryCodePickerComponent(
        value = phoneNumber,
        onValueChange = { newValue ->
            val filteredValue = newValue.filter { it.isDigit() }
            uiState.onPhoneNumberValueChange(filteredValue)
        },
        isLeadingIconVisible = true,
        isHeaderVisible = true,
        errorMessage = phoneNumberError,
        title = stringResource(id = R.string.phone_number),
        isTrailingIconVisible = true,
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = true,
            keyboardType = KeyboardType.Number
        ),
        header = stringResource(id = R.string.phone_number),
        leadingIcon = R.drawable.ic_call,
        setCountryCode = uiState.countryCode
    )
    Spacer(modifier = Modifier.padding(15.dp))
    OutlineTextFiledDateSelectComponent(
        value = selectedDate,
        header = "Date of Birth",
        showLeadingIcon = false,
        title = "Date of Birth",
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
                uiState.onSelectedDateChange(selectedDate)
            },
            onDismiss = {
                showDialog = false
            },
            onDateSelectedLong = {
                temp = it
            }
        )
    }
    Spacer(modifier = Modifier.padding(15.dp))
    OutlineTextFieldWithLeadingAndTrailingComponent(
        value = zipCode,
        errorMessage = zipCodeError,
        onValueChange = { newValue ->
            val filteredValue = newValue.filter { it.isDigit() }
            uiState.onZipCodeValueChange(filteredValue)
        },
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = true,
            keyboardType = KeyboardType.Number
        ),
        isTitleVisible = true,
        isLeadingIconVisible = true,
        title = stringResource(R.string.what_is_your_zip_code),
        header = stringResource(R.string.zip_code),
        leadingIcon = R.drawable.ic_location
    )
    Spacer(modifier = Modifier.padding(15.dp))
    OutlineTextFiledDateSelectComponent(
        value = cityFlow,
        header = stringResource(R.string.city),
        title = stringResource(id = R.string.city),
        leadingIcon = R.drawable.ic_location,
        errorMessage = cityFlowErrorFlow,
        onClick = {
            launcher.launch(intent)
        }
    )
}








