@file:Suppress("NAME_SHADOWING")

package com.kinship.mobile.app.ux.main.events.createEvent

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
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
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.compose.common.BottomButtonComponent
import com.kinship.mobile.app.ui.compose.common.CameraGalleryDialog
import com.kinship.mobile.app.ui.compose.common.CustomLoader
import com.kinship.mobile.app.ui.compose.common.CustomSwitch
import com.kinship.mobile.app.ui.compose.common.DatePickerWithDialogFuture
import com.kinship.mobile.app.ui.compose.common.OutlineTextFieldComponent
import com.kinship.mobile.app.ui.compose.common.OutlineTextFieldMultipleLine
import com.kinship.mobile.app.ui.compose.common.OutlineTextFieldWithOutTrailingIcon
import com.kinship.mobile.app.ui.compose.common.OutlineTextFiledDateSelectComponent
import com.kinship.mobile.app.ui.compose.common.OutlineTextFiledWithOutLeadingIcon
import com.kinship.mobile.app.ui.compose.common.PermissionDialog
import com.kinship.mobile.app.ui.compose.common.TimePickerWithDialog
import com.kinship.mobile.app.ui.compose.common.TopBarComponent
import com.kinship.mobile.app.ui.theme.Black
import com.kinship.mobile.app.ui.theme.Black50
import com.kinship.mobile.app.ui.theme.Black70
import com.kinship.mobile.app.ui.theme.OpenSans
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.HoneyFlower50
import com.kinship.mobile.app.ui.theme.White
import com.kinship.mobile.app.utils.AppUtils
import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

@Composable
fun CreateEventScreen(
    navController: NavController = rememberNavController(),
    viewModel: CreateEventViewModel = hiltViewModel(),
    myEventResponse: String,
    screen: String,

    ) {
    val uiState = viewModel.uiState
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                header = stringResource(R.string.new_event),
                isBackVisible = true,
                isLineVisible = true,
                onClick = {
                    uiState.onBackClick()
                }
            )
        },
    )
    {
        uiState.sendScreenName(screen)
        uiState.myEventData(myEventResponse)
        CreateEventContent(uiState, screen)
        val showLoader by uiState.showLoader.collectAsStateWithLifecycle()
        if (showLoader) {
            CustomLoader()
        }
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun CreateEventContent(uiState: CreateEventUiState, screen: String) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val eventDescription by uiState.eventDescriptionFlow.collectAsStateWithLifecycle()
    val eventBioError by uiState.eventBioErrorFlow.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() })
            {
                keyboardController?.hide()
            }
    ) {
        OutlineTextFiledCreateEvent(uiState, screen)
        EventPhoto(uiState)
        Spacer(modifier = Modifier.padding(18.dp))
        OutlineTextFieldMultipleLine(
            value = eventDescription,
            onValueChange = { uiState.onEventDescription(it) },
            isHeaderVisible = true,
            errorMessage = eventBioError,
            header = stringResource(id = R.string.type_here),
            title = stringResource(R.string.event_description)
        )
        Spacer(modifier = Modifier.padding(27.dp))
        BottomButtonComponent(text = if (screen == Constants.ContainerScreens.CREATE_EVENT_EDIT) "Update" else stringResource(
            id = R.string.submit
        ), onClick = {
            uiState.createEventAPICall()
        })
        Spacer(modifier = Modifier.padding(10.dp))
    }
}

@Composable
fun EventPhoto(uiState: CreateEventUiState) {
    val pickedImg by uiState.profilePicFlow.collectAsStateWithLifecycle()
    val showCameraGalleryDialog by uiState.openCameraGalleryDialog.collectAsStateWithLifecycle()
    val showPermissionDialog by uiState.openPermissionDialog.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val startForCameraPermissionResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            Log.e(ContentValues.TAG, "Camera Permission ${result.resultCode}")
        }
    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }
    val uri by uiState.captureUri.collectAsStateWithLifecycle()
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
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
            uiState.onImageFlag(true)
            uiState.onClickOfCamera(context)
        } else {
            uiState.onPermissionDialog(true)
        }
    }
    val photoOptional = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Black, fontSize = 14.sp)) {
            append("Photo")
        }
        append(" ")
        withStyle(style = SpanStyle(color = Black70, fontSize = 12.sp)) {
            append("(Optional)")
        }
    }
    Spacer(modifier = Modifier.padding(15.dp))
    Column {
        Text(
            text = photoOptional,
            fontFamily = OpenSans,
            color = Black70,
            fontWeight = FontWeight.W400,
            modifier = Modifier
                .padding(start = 18.dp),
        )
        Spacer(modifier = Modifier.padding(3.dp))
        Column {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(15.dp),
                onClick = {
                    uiState.onCameraGalleryDismissDialog(true)
                },
                border = BorderStroke(1.dp, color = AppThemeColor),
                modifier = Modifier
                    .size(120.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (pickedImg.isEmpty()) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_add_photo),
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .align(alignment = Alignment.Center)
                        )
                    } else {
                        AsyncImage(
                            //replace with image from server
                            model = pickedImg,
                            contentDescription = stringResource(R.string.profile),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                }
            }
        }
    }
    if (showPermissionDialog) {
        PermissionDialog(
            onDismissRequest = {
                uiState.onPermissionDialog(false)
                uiState.onCameraGalleryDismissDialog(false)
            },
            title = stringResource(R.string.kinship_app),
            description = stringResource(R.string.allow_kinship_app_to_access_your_storage_and_camera_while_you_are_using_the_app),
            negativeText = stringResource(id = R.string.cancel),
            positiveText = stringResource(R.string.open_setting),
            onPositiveClick = {
                uiState.onPermissionDialog(false)
                uiState.onCameraGalleryDismissDialog(false)
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts(
                    "package", context.packageName, null
                )
                intent.data = uri
                startForCameraPermissionResult.launch(intent)
            },
        )
    }
    if (showCameraGalleryDialog) {
        CameraGalleryDialog(
            onDismissRequest = { uiState.onCameraGalleryDismissDialog(false) },
            topText = stringResource(id = R.string.gallery),
            bottomText = stringResource(id = R.string.camera),
            onTopClick = {
                uiState.onCameraGalleryDismissDialog(false)
                uiState.onImageFlag(true)
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            onBottomClick = {
                uiState.onCameraGalleryDismissDialog(false)
                val permissionCheckResult =
                    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                    uiState.onClickOfCamera(context)
                    uiState.onImageFlag(true)
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                    uiState.onImageFlag(true)

                }
            }
        )
    }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val launchCamera by uiState.launchCamera.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = launchCamera, key2 = isLandscape) {
        if (launchCamera && !isLandscape) { // Skip launching in landscape
            uri?.let { validUri ->
                Logger.e("capture uri: $validUri")
                cameraLauncher.launch(validUri)
            } ?: Logger.e("URI is null, cannot launch camera")
        } else if (isLandscape) {
            Logger.e("Skipping camera launch in landscape mode")
        }
    }
}

@SuppressLint("DefaultLocale", "SimpleDateFormat")
@Composable
fun OutlineTextFiledCreateEvent(uiState: CreateEventUiState, screen: String) {
    val showEventDateDialog by uiState.openEventDialog.collectAsStateWithLifecycle()
    val showStarAndTime by uiState.showStartAndTime.collectAsStateWithLifecycle()
    val eventName by uiState.eventNameFlow.collectAsStateWithLifecycle()
    val location by uiState.locationFlow.collectAsStateWithLifecycle()
    val eventLink by uiState.eventLinkFlow.collectAsStateWithLifecycle()
    val eventDate by uiState.eventDateFlow.collectAsStateWithLifecycle()
    val eventNameError by uiState.eventNameErrorFlow.collectAsStateWithLifecycle()
    val eventDateError by uiState.eventDateErrorFlow.collectAsStateWithLifecycle()
    val eventLinkError by uiState.eventLinkErrorFlow.collectAsStateWithLifecycle()
    var selectedLocation by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    val currentDate = SimpleDateFormat("MM/dd/yyyy").format(Date())
    val isSwitchEnabled = remember { mutableStateOf(false) }
    var temp by rememberSaveable { mutableStateOf<Long?>(null) }
    LaunchedEffect(eventDate) {
        val eventDateString = try {
            val timestampLong = eventDate.toLong()
            SimpleDateFormat("MM/dd/yyyy").format(Date(timestampLong))
        } catch (e: Exception) {
            "" // Handle parsing errors
        }
        if (eventDateString == currentDate) {
            isSwitchEnabled.value = false
            uiState.onShowStartAndTime(true)
            uiState.onSwitchOffClick()
        } else {
            isSwitchEnabled.value = eventDateString.isNotBlank()
        }
    }
    val context = LocalContext.current
    val field = listOf(Place.Field.NAME, Place.Field.LAT_LNG)
    val intent =
        Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, field).build(context)
    Places.initialize(context, BuildConfig.MAP_API_KEY)
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.let {
                    val place = Autocomplete.getPlaceFromIntent(it)
                    place.name?.let { it1 ->
                        selectedLocation = it1
                        uiState.onLocationValueChange(it1)
                    }
                    place.latLng?.let { latLng ->
                        uiState.onLatLong(latLng.latitude, latLng.longitude)
                        Log.d("TAG", "latLong:${latLng.latitude} ${latLng.longitude} ")
                    }
                }
            } else if (result.resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = result.data?.let { Autocomplete.getStatusFromIntent(it) }
                Log.e("Autocomplete Error", status?.statusMessage ?: "Error")
            }
        }
    val linkOptional = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Black, fontSize = 14.sp)) {
            append(stringResource(id = R.string.link))

        }
        append(" ")
        withStyle(style = SpanStyle(color = Black70, fontSize = 12.sp)) {
            append(stringResource(R.string.optional))
        }
    }
    val locationOptional = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Black, fontSize = 14.sp)) {
            append(stringResource(id = R.string.location))
        }
        append(" ")
        withStyle(style = SpanStyle(color = Black70, fontSize = 12.sp)) {
            append(stringResource(id = R.string.optional))
        }
    }
    Spacer(modifier = Modifier.padding(7.dp))
    Column(
        verticalArrangement = Arrangement.spacedBy(27.dp)
    ) {
        OutlineTextFieldComponent(
            value = eventName,
            onValueChange = {
                uiState.onEventValueChange(it)
            },
            errorMessage = eventNameError,
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = true,
                keyboardType = KeyboardType.Password
            ),
            isTitleVisible = true,
            title = stringResource(R.string.event_name),
            header = stringResource(R.string.event_name),
        )
        val eventDateTimestamp = eventDate
        if (eventDateTimestamp.isNotBlank()) {
            try {
                val timestampLong = eventDateTimestamp.toLong()
                selectedDate = AppUtils.convertTimestampToDate(timestampLong)
            } catch (e: NumberFormatException) {
                Log.e("TAG", "Error parsing timestamp: ${e.message}")
            }
        }
        OutlineTextFiledDateSelectComponent(
            value = selectedDate,
            title = stringResource(R.string.event_date),
            header = stringResource(R.string.event_date),
            showLeadingIcon = false,
            errorMessage = eventDateError,
            leadingIcon = R.drawable.ic_calendar,
            onClick = {
                uiState.onEventDateDismissDialog(true)
            }
        )
        if (showStarAndTime) {
            OutlineTextFiledStartAndEnd(uiState, eventDate, screen)
        }
    }
    Column {
        OutlineTextFiledSwitch(uiState, isSwitchEnabled.value)
        Spacer(modifier = Modifier.padding(15.dp))
        Text(
            text = linkOptional,
            fontFamily = OpenSans,
            color = Black70,
            fontWeight = FontWeight.W400,
            modifier = Modifier
                .padding(start = 18.dp),
        )
        Spacer(modifier = Modifier.padding(3.dp))
        OutlineTextFieldWithOutTrailingIcon(
            value = eventLink,
            onValueChange = {
                uiState.onEventLinkValueChange(it)
            },
            errorMessage = eventLinkError,
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = true,
                keyboardType = KeyboardType.Password
            ),
            isTitleVisible = true,
            isLeadingIconVisible = true,
            leadingIcon = R.drawable.ic_link,
            header = stringResource(R.string.link),
        )
        Spacer(modifier = Modifier.padding(15.dp))
        Text(
            text = locationOptional,
            fontFamily = OpenSans,
            color = Black70,
            fontWeight = FontWeight.W400,
            modifier = Modifier
                .padding(start = 18.dp),
        )
        Spacer(modifier = Modifier.padding(3.dp))
        selectedLocation = location
        OutlineTextFiledDateSelectComponent(
            value = selectedLocation,
            header = stringResource(R.string.location),
            showLeadingIcon = true,
            leadingIcon = R.drawable.ic_location,
            onClick = {
                launcher.launch(intent)
            }
        )
    }
    if (showEventDateDialog) {
        DatePickerWithDialogFuture(
            onSelectedDate = temp,
            onDateSelected = { dateString ->
                val formatter = SimpleDateFormat("dd/MM/yyyy")
                // Parse the date using the correct time zone (UTC)
                formatter.timeZone = TimeZone.getDefault()
                val date: Date = formatter.parse(dateString) as Date

                // Format the selected date for display (still in UTC to avoid shifts)
                val formatterDisplay = SimpleDateFormat("MM/dd/yyyy")
                formatterDisplay.timeZone = TimeZone.getDefault()
                selectedDate = formatterDisplay.format(date)
                uiState.onEventDateValueChange(selectedDate)

                val calendar = Calendar.getInstance()
                calendar.time = date
                //  uiState.onEventStartTimeValueChange(0,0)
                //  uiState.onEventEndTimeValueChange(0,0)
                // Set the start time at 12:00 AM UTC
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.timeZone = TimeZone.getDefault()
                val startTimeMillis = calendar.timeInMillis

                // Set the end time at 11:59 PM UTC
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MILLISECOND, 999)
                val endTimeMillis = calendar.timeInMillis
                // Notify UI state with start and end time
                uiState.onDateStarTime(startTimeMillis.toString())
                uiState.onDateEndTime(endTimeMillis.toString())
            },
            onDateSelectedLong = {
                temp = it
            },
            onDismiss = {
                uiState.onEventDateDismissDialog(false)
            }
        )
    }
}

@Composable
fun OutlineTextFiledSwitch(uiState: CreateEventUiState, value: Boolean) {
    var allNewPostSwitch by remember { mutableStateOf(false) }
    val isAllDay by uiState.isAllDay.collectAsStateWithLifecycle()
    if (isAllDay == 1) {
        uiState.onShowStartAndTime(false)
        allNewPostSwitch = isAllDay == 1
    } else {
        uiState.onShowStartAndTime(true)
        allNewPostSwitch = isAllDay == 1
    }

    Spacer(modifier = Modifier.padding(15.dp))
    Surface(
        modifier = Modifier
            .heightIn(56.dp)
            .fillMaxWidth(),
        border = BorderStroke(1.dp, color = HoneyFlower50),
        shape = RoundedCornerShape(50),
        color = White,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                text = "All day",
                fontFamily = OpenSans,
                fontWeight = FontWeight.W600,
                fontSize = 14.sp,
                color = if (allNewPostSwitch) Black else Black50,
            )
            Spacer(modifier = Modifier.weight(1f))
            CustomSwitch(
                onClick = {
                    if (value) {
                        allNewPostSwitch = !allNewPostSwitch
                        if (allNewPostSwitch) {
                            uiState.onShowStartAndTime(false)
                            uiState.onSwitchOnClick()
                        } else {
                            uiState.onShowStartAndTime(true)
                            uiState.onSwitchOffClick()
                        }
                    }
                },
                switchState = allNewPostSwitch,
                enabled = value
            )
            Spacer(modifier = Modifier.padding(10.dp))
        }
    }
}

@Composable
fun OutlineTextFiledStartAndEnd(
    uiState: CreateEventUiState,
    eventDate: String,
    screen: String,
) {
    val eventStartTimeError by uiState.eventStartTimeErrorFlow.collectAsStateWithLifecycle()
    val showEventFirstTimeDialog by uiState.openEventFirstTimeDialog.collectAsStateWithLifecycle()
    val showEventSecondTimeDialog by uiState.openEventSecondTimeDialog.collectAsStateWithLifecycle()
    val eventEndTimeError by uiState.eventEndTimeErrorFlow.collectAsStateWithLifecycle()
    var selectStartTime by remember { mutableStateOf("") }
    var selectEndTime by remember { mutableStateOf("") }
    val context = LocalContext.current
    val eventStartDate by uiState.eventStartTimeFlow.collectAsStateWithLifecycle()
    val eventEndDate by uiState.eventEndTimeFlow.collectAsStateWithLifecycle()
    selectStartTime = if (eventStartDate.isNotEmpty()) {
        AppUtils.getTimeFromMillis(eventStartDate.toLong())
    } else {
        ""
    }
    selectEndTime = if (eventEndDate.isNotEmpty()) {
        AppUtils.getTimeFromMillis(eventEndDate.toLong())
    } else {
        ""
    }
    Column {
        Spacer(modifier = Modifier.padding(3.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(color = White)
            ) {
                OutlineTextFiledWithOutLeadingIcon(
                    value = selectStartTime,
                    title = stringResource(id = R.string.start),
                    isTrailingIconVisible = true,
                    isTitleVisible = true,
                    errorMessage = eventStartTimeError,
                    trailingIcon = R.drawable.ic_time,
                    header = stringResource(id = R.string.start),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Sentences,
                    ),
                    onClick = {
                        uiState.onEventFirstTimeDismissDialog(true)
                    }
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(color = White)
            ) {
                OutlineTextFiledWithOutLeadingIcon(
                    value = selectEndTime,
                    title = stringResource(id = R.string.end),
                    isTrailingIconVisible = true,
                    isTitleVisible = true,
                    errorMessage = eventEndTimeError,
                    trailingIcon = R.drawable.ic_time,
                    header = stringResource(id = R.string.end),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    onClick = {
                        uiState.onEventSecondTimeDismissDialog(true)
                    }
                )
            }
        }
    }
    // current time and hour minute
    val currentTime = Calendar.getInstance()
    val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
    val currentMinute = currentTime.get(Calendar.MINUTE)
    val newStartHour = (currentHour + 1) % 24
    if (showEventFirstTimeDialog) {
        TimePickerWithDialog(
            initialHour = newStartHour,
            initialMinute = currentMinute,
            onTimeSelected = { hour, min ->

                val currentTimeMillis = System.currentTimeMillis()

                val eventDateMillis: Long
                try {
                    val eventDate = Date(eventDate.toLong())
                    eventDateMillis = eventDate.time
                } catch (e: Exception) {
                    Toasty.warning(
                        context,
                        "Please choose event date",
                        Toast.LENGTH_SHORT,
                        false
                    ).show()
                    return@TimePickerWithDialog
                }
                // Construct the full event datetime in milliseconds
                val eventCalendar = Calendar.getInstance()
                eventCalendar.timeInMillis = eventDateMillis
                eventCalendar.set(Calendar.HOUR_OF_DAY, hour)
                eventCalendar.set(Calendar.MINUTE, min)
                eventCalendar.set(Calendar.SECOND, 0)
                eventCalendar.set(Calendar.MILLISECOND, 0)
                val eventDateTimeMillis = eventCalendar.timeInMillis
                if (eventDateTimeMillis < currentTimeMillis) {
                    Toasty.warning(
                        context,
                        context.getString(R.string.selected_time_cannot_be_before_the_current_time),
                        Toast.LENGTH_SHORT,
                        false
                    ).show()
                } else {
                    uiState.onEventStartTimeValueChange(hour, min)
                }
            },
            onDismiss = {
                uiState.onEventFirstTimeDismissDialog(false)
            }
        )
    }
    if (showEventSecondTimeDialog) {
        TimePickerWithDialog(
            initialHour = (newStartHour + 1) % 24,
            initialMinute = currentMinute,
            onTimeSelected = { hour, min ->
                // Check if the start time is selected before proceeding with the end time selection
                if (eventStartDate.isEmpty()) {
                    // Show toast message that start time must be selected first
                    Toasty.warning(
                        context,
                        context.getString(R.string.please_select_start_time_first),
                        Toast.LENGTH_SHORT,
                        false
                    ).show()
                } else {
                    val convertEndTime = AppUtils.convertHourMinToTime(hour, min)
                    val startTime = eventStartDate.toLong()
                    val endTime = AppUtils.convertHourMinToTimestamp(hour, min, eventDate.toLong())
                    // Check for various time constraints
                    when {
                        endTime < startTime -> {
                            Toasty.warning(
                                context,
                                context.getString(R.string.selected_time_cannot_be_before_the_current_time),
                                Toast.LENGTH_SHORT,
                                false
                            ).show()
                        }
                        // Event must be at least 60 minutes long
                        endTime < (startTime + 3600 * 1000) -> {
                            Toasty.warning(
                                context,
                                context.getString(R.string.event_must_be_at_least_60_minutes_long),
                                Toast.LENGTH_SHORT,
                                false
                            ).show()
                        }

                        else -> {
                            // All validations passed, update the end time
                            selectEndTime = convertEndTime
                            uiState.onEventEndTimeValueChange(hour, min)
                        }
                    }
                }
            },
            onDismiss = {
                uiState.onEventSecondTimeDismissDialog(false)
            }
        )
    }

}

@Preview
@Composable
fun CreateEventContentPreview() {
    val uiState = CreateEventUiState()
    val screen = Constants.ContainerScreens.CREATE_EVENT_SCREEN
    OutlineTextFiledCreateEvent(uiState, screen)
}

