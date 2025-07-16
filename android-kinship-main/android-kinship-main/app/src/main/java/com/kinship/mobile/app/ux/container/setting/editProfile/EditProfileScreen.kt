@file:Suppress("NAME_SHADOWING", "DEPRECATION")

package com.kinship.mobile.app.ux.container.setting.editProfile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import co.touchlab.kermit.Logger
import coil.compose.AsyncImage
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.kinship.mobile.app.BuildConfig
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.model.domain.tabSelector.Tab
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.BottomButtonComponent
import com.kinship.mobile.app.ui.compose.common.CameraGalleryDialog
import com.kinship.mobile.app.ui.compose.common.CustomTabs
import com.kinship.mobile.app.ui.compose.common.DatePickerWithDialog
import com.kinship.mobile.app.ui.compose.common.OutlineTextFieldMultipleLine
import com.kinship.mobile.app.ui.compose.common.OutlineTextFieldWithOutTrailingIcon
import com.kinship.mobile.app.ui.compose.common.OutlineTextFiledDateSelectComponent
import com.kinship.mobile.app.ui.compose.common.PermissionDialog
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.networkresult.NetworkResultHandler
import com.kinship.mobile.app.ui.theme.Black23
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.White
import com.kinship.mobile.app.utils.AppUtils
import es.dmoral.toasty.Toasty
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

@Preview
@Composable
fun EditProfileScreen(
    navController: NavController = rememberNavController(),
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = stringResource(id = R.string.edit_profile),
                isLineVisible = true, isBackVisible = true,
                onClick = {
                    uiState.onBackClick()
                }
            )
        },
        navBarData = null
    ) {
        val apiResultFlow by uiState.apiResultFlow.collectAsStateWithLifecycle()
        val context = LocalContext.current
        apiResultFlow?.let { it1 ->
            NetworkResultHandler(
                networkResult = it1,
                onSuccess = { apiResponse ->
                    Toasty.success(context, apiResponse.message, Toast.LENGTH_SHORT, false).show()
                    apiResponse.data?.let { it1 -> uiState.onEditProfileClick(it1) }
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
        EditProfileScreenContent(modifier = Modifier, uiState)
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun EditProfileScreenContent(modifier: Modifier, uiState: EditProfileUiState) {
    var isImageAvailable by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val showDialog by uiState.openPickImgDialog.collectAsStateWithLifecycle()
    val startForCameraPermissionResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            Log.e(ContentValues.TAG, "Camera Permission ${result.resultCode}")
        }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }
    val uri by uiState.captureUri.collectAsStateWithLifecycle()
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                val selectedProfileImagePath = AppUtils.getFileFromContentUri(
                    context, uri,
                    Constants.AppInfo.DIR_NAME.plus(System.currentTimeMillis())
                )?.absolutePath ?: ""
                uiState.onProfileImgPick(selectedProfileImagePath)
            } else {
                Log.e("Profile", "OpenPhotoPicker: No media selected")
            }
        }
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { captured ->
            if (captured) {
                capturedImageUri = uri ?: Uri.EMPTY
                val selectedProfileImagePath = AppUtils.getFileFromContentUri(
                    context, capturedImageUri,
                    Constants.AppInfo.DIR_NAME.plus(System.currentTimeMillis())
                )?.absolutePath ?: ""
                uiState.onProfileImgPick(selectedProfileImagePath)
            } else {
                uiState.onClearUnUsedUseState()
            }
        }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            uiState.onClickOfCamera(context)
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.padding(15.dp))
        isImageAvailable = true
        UserImageContent(onClick = {
            uiState.onOpenORDismissDialog(true)
        }, uiState)
        Spacer(modifier = Modifier.padding(20.dp))
        Row(modifier = modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.first_name),
                fontFamily = OpenSans,
                color = Black23,
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                modifier = Modifier
                    .padding(start = 18.dp)
                    .weight(1f),
            )
            Text(
                text = stringResource(id = R.string.last_name),
                fontFamily = OpenSans,
                color = Black23,
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                modifier = Modifier
                    .padding(start = 18.dp)
                    .weight(1f),
            )
        }
        OutLineTextFiled(uiState)
        Spacer(modifier = Modifier.padding(30.dp))
        BottomButtonComponent(text = stringResource(id = R.string.update), onClick = {
            uiState.onEditProfileAPICall()
        })
        Spacer(modifier = Modifier.padding(10.dp))
    }
    if (showPermissionDialog) {
        PermissionDialog(
            onDismissRequest = {
                showPermissionDialog = false
                uiState.onOpenORDismissDialog(false)
            },
            title = stringResource(R.string.kinship_app),
            description = stringResource(R.string.allow_kinship_app_to_access_your_storage_and_camera_while_you_are_using_the_app),
            negativeText = stringResource(id = R.string.cancel),
            positiveText = stringResource(R.string.open_setting),
            onPositiveClick = {
                showPermissionDialog = false
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts(
                    "package", context.packageName, null
                )
                intent.data = uri
                startForCameraPermissionResult.launch(intent)
            },
        )
    }
    if (showDialog) {
        CameraGalleryDialog(
            onDismissRequest = { uiState.onOpenORDismissDialog(false) },
            topText = stringResource(id = R.string.gallery),
            bottomText = stringResource(id = R.string.camera),
            onTopClick = {
                uiState.onOpenORDismissDialog(false)
                uiState.onImageFlag(true)
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            onBottomClick = {
                uiState.onOpenORDismissDialog(false)
                uiState.onImageFlag(true)
                val permissionCheckResult =
                    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                    uiState.onClickOfCamera(context)

                } else {
                    // Request a permission
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        )
    }
    val launchCamera by uiState.launchCamera.collectAsStateWithLifecycle()
    if (launchCamera) {
        uri?.let {
            Logger.e("capture uri: $it")
            cameraLauncher.launch(it)
        } ?: Logger.e("URI is null, cannot launch camera")
    }
}
@Composable
fun OutLineTextFiled(uiState: EditProfileUiState) {
    val context = LocalContext.current
    val singleMultipleOption by uiState.singleMultipleOption.collectAsStateWithLifecycle()
    var optionSelected by remember { mutableIntStateOf(singleMultipleOption) }
    val firstName by uiState.firstNameFlow.collectAsStateWithLifecycle()
    val firstNameError by uiState.firstNameErrorFlow.collectAsStateWithLifecycle()
    val lastName by uiState.lastNameFlow.collectAsStateWithLifecycle()
    val lastNameError by uiState.lastNameErrorFlow.collectAsStateWithLifecycle()
    val city by uiState.cityFlow.collectAsStateWithLifecycle()
    val bio by uiState.bioFlow.collectAsStateWithLifecycle()
    val bioError by uiState.bioErrorFlow.collectAsStateWithLifecycle()
    var selectedLocation by remember { mutableStateOf("") }

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
                    selectedLocation = place.name
                    uiState.onCityValueChange(place.name)
                }
            } else if (result.resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = result.data?.let { Autocomplete.getStatusFromIntent(it) }
                Log.e("Autocomplete Error", status?.statusMessage ?: "Error")
            }
        }
    Spacer(modifier = Modifier.padding(4.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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
                    keyboardType = KeyboardType.Password,  // Use KeyboardType.Text to show text-only keyboard
                    capitalization = KeyboardCapitalization.Words,

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
                    capitalization = KeyboardCapitalization.Words
                ),
            )
        }
    }
    Spacer(modifier = Modifier.padding(15.dp))
    selectedLocation = city
    OutlineTextFiledDateSelectComponent(
        value = selectedLocation,
        header = stringResource(R.string.city),
        title = stringResource(id = R.string.city),
        leadingIcon = R.drawable.ic_location,
        onClick = {
            launcher.launch(intent)
        }
    )
    Spacer(modifier = Modifier.padding(15.dp))
    val singleMultiple = listOf(
        Tab(
            stringResource(id = R.string.single),
            painterResource(id = R.drawable.ic_single_person)
        ),
        Tab(
            stringResource(id = R.string.multiple),
            painterResource(id = R.drawable.ic_multiple_person)
        )
    )
    Row(modifier = Modifier.fillMaxWidth()) {
        CustomTabs(
            tab = singleMultiple,
            isTitleShow = true,
            title = stringResource(id = R.string.children_detail),
            selectedPosition = optionSelected,
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
    Spacer(modifier = Modifier.padding(18.dp))
    if (optionSelected == 0) {
        SingleBaby(uiState)
    } else {
        MultipleBaby(uiState)
    }
    Spacer(modifier = Modifier.padding(17.dp))
    OutlineTextFieldMultipleLine(
        value = bio,
        errorMessage = bioError,
        onValueChange = { uiState.onBioValueChange(it) },
        isHeaderVisible = true,
        header = stringResource(id = R.string.type_here),
        title = stringResource(id = R.string.bio)
    )
}

@Composable
private fun UserImageContent(onClick: () -> Unit, uiState: EditProfileUiState) {
    val pickedImg by uiState.profilePicFlow.collectAsStateWithLifecycle()
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = CircleShape,
        border = BorderStroke(1.dp, color = AppThemeColor),
        onClick = onClick,
        modifier = Modifier.size(110.dp)

    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (pickedImg.isEmpty()) {
                Image(
                    painter = painterResource(id = R.drawable.ic_add_image),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            } else {
                AsyncImage(
                    //replace with image from server
                    model = pickedImg, contentDescription = stringResource(id = R.string.profile),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .fillMaxSize()
                )
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
private fun SingleBaby(uiState: EditProfileUiState) {
    val selectedDateIndex = 0 // Replace this with the index of the selected date
    val dateOfBirth = uiState.tempSingleDateFlow.collectAsStateWithLifecycle()
    var selectedDate by rememberSaveable { mutableStateOf("") }
    var temp by rememberSaveable { mutableStateOf<Long?>(null) }
    var isDateSelectedFromPicker by rememberSaveable { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    if (selectedDateIndex in dateOfBirth.value.indices) {
        selectedDate = dateOfBirth.value[selectedDateIndex].toString()
    }
    val multipleFirstDateTimestamp = selectedDate
    if (multipleFirstDateTimestamp.isNotBlank()) {
        try {
            val timestampLong = multipleFirstDateTimestamp.toLong()
            selectedDate = AppUtils.convertTimestampToDate(timestampLong)
        } catch (e: NumberFormatException) {
            Log.e("TAG", "Error parsing timestamp: ${e.message}")
        }
    }
    Column {
        OutlineTextFiledDateSelectComponent(
            value = selectedDate,
            header = stringResource(id = R.string.birthdate),
            showLeadingIcon = false,
            title = stringResource(id = R.string.children_detail),
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
                    isDateSelectedFromPicker = true
                    val formatter1: DateFormat = SimpleDateFormat("dd/MM/yyyy")
                    val date: Date = formatter1.parse(dateString) as Date
                    uiState.onSingleBabyBornDate(date.time)
                },
                onDismiss = {
                    showDialog = false
                },
                onDateSelectedLong = {
                    temp=it
                }

            )
        }
    }
}

@Composable
fun MultipleBaby(uiState: EditProfileUiState) {
    val emptyDate: Long = -1L
    Column(modifier = Modifier.background(color = Color.White)) {
        val dateOfBirth = uiState.tempMultipleDateFlow.collectAsStateWithLifecycle()
        var showThirdItem by remember { mutableStateOf(false) }
        var temp by rememberSaveable { mutableStateOf<Long?>(null) }
        var addNew by remember {
            mutableStateOf(true)
        }
        if (dateOfBirth.value.size == 3) {
            addNew = false
            showThirdItem = true
        }
        repeat(if (showThirdItem) 3 else 2) { index ->
            var showDialog by remember { mutableStateOf(false) }
            var selectedDate by remember { mutableStateOf("") }
            // Get the selected date if it exists
            if (index in dateOfBirth.value.indices) {
                selectedDate = dateOfBirth.value[index].toString()
            }
            val multipleFirstDateTimestamp = selectedDate
            if (multipleFirstDateTimestamp.isNotBlank()) {
                try {
                    val timestampLong = multipleFirstDateTimestamp.toLong()
                    selectedDate = AppUtils.convertTimestampToDate(timestampLong)
                } catch (e: NumberFormatException) {
                    Log.e("TAG", "Error parsing timestamp: ${e.message}")
                }
            }
            fun showDatePickerDialog() {
                showDialog = true
            }

            @SuppressLint("SimpleDateFormat")
            fun handleDateSelection(dateString: String) {
                val date = SimpleDateFormat("dd/MM/yyyy").parse(dateString)

                date?.let {
                    val updatedDateList = dateOfBirth.value.toMutableList()
                    while (updatedDateList.size <= index) {
                        updatedDateList.add(emptyDate)
                    }
                    updatedDateList[index] = it.time
                    when (index) {
                        0 -> {
                            uiState.onMultipleFirstBabyBornDate(it.time)
                        }

                        1 -> {
                            uiState.onMultipleSecondBabyBornDate(it.time)
                        }

                        2 -> {
                            uiState.onMultipleThirdBabyBornDate(it.time)
                        }
                    }
                    uiState.tempMultipleDateFlow.value = updatedDateList.map { it }.toList()
                }
                showDialog = false
            }
            val title = when (index) {
                0 -> stringResource(R.string._1st_child_s_birthdate)
                1 -> stringResource(R.string._2st_child_s_birthdate)
                2 -> stringResource(R.string._3st_child_s_birthdate)
                else -> ""
            }
            OutlineTextFiledDateSelectComponent(
                value = selectedDate,
                title = title,
                header = stringResource(id = R.string.birthdate),
                showLeadingIcon = false,
                leadingIcon = R.drawable.ic_calendar,
                onClick = ::showDatePickerDialog
            )

            if (showDialog) {
                DatePickerWithDialog(
                    onSelectedDate = temp,
                    onDateSelected = ::handleDateSelection,
                    onDismiss = { showDialog = false },
                    onDateSelectedLong = {
                        temp=it
                    }
                )
            }
            if (index == 0) {
                Spacer(modifier = Modifier.padding(18.dp))
            }
            if (index == 1 && showThirdItem) {
                Spacer(modifier = Modifier.padding(18.dp))
            }
        }
        if (addNew) {
            Spacer(modifier = Modifier.padding(7.dp))
            Button(
                onClick =
                {
                    showThirdItem = true
                    addNew = false
                },
                modifier = Modifier
                    .align(alignment = Alignment.End)
                    .heightIn(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppThemeColor)
            ) {
                Text(
                    text = stringResource(R.string.add_new),
                    fontFamily = OpenSans,
                    fontWeight = FontWeight.W400,
                    fontSize = 17.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun EditProfilePreview() {
    val uiState = EditProfileUiState()
    EditProfileScreenContent(
        modifier = Modifier
            .background(color = White), uiState
    )
}