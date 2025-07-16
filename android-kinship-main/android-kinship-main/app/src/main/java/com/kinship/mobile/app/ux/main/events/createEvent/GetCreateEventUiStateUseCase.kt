package com.kinship.mobile.app.ux.main.events.createEvent

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import co.touchlab.kermit.Logger
import com.google.gson.Gson
import com.kinship.mobile.app.BuildConfig
import com.kinship.mobile.app.R
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.createEvent.CreateEventResponse
import com.kinship.mobile.app.model.domain.response.events.MyEventsData
import com.kinship.mobile.app.navigation.NavigationAction
import com.kinship.mobile.app.utils.AppUtils
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Date
import java.util.Objects
import java.util.TimeZone
import javax.inject.Inject

class GetCreateEventUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
) {
    private val eventNameFlow = MutableStateFlow("")
    private val eventDateFlow = MutableStateFlow("")
    private var dateStartTime = MutableStateFlow("")
    private val dateEndTime = MutableStateFlow("")
    private val eventStartTimeFlow = MutableStateFlow("")
    private val eventEndTimeFlow = MutableStateFlow("")
    private val locationFlow = MutableStateFlow("")
    private val eventLinkFlow = MutableStateFlow("")
    private val eventDescriptionFlow = MutableStateFlow("")
    private val profileImgFlow = MutableStateFlow("")
    private val isAllDay = MutableStateFlow(2)

    // private var latitude: Double = 0.0
    // private var longitude: Double = 0.0
    private var latitude = 0.0
    private var longitude = 0.0
    private val eventNameErrorFlow = MutableStateFlow<String?>(null)
    private val eventDateErrorFlow = MutableStateFlow<String?>(null)
    private val eventStartTimeErrorFlow = MutableStateFlow<String?>(null)
    private val eventEndTimeErrorFlow = MutableStateFlow<String?>(null)
    private val eventDescriptionErrorFlow = MutableStateFlow<String?>(null)
    private val eventLinkErrorFlow = MutableStateFlow<String?>(null)
    private val selectImage = MutableStateFlow(false)
    private val openEventDialog = MutableStateFlow(false)
    private val openEventStartTimeDialog = MutableStateFlow(false)
    private val openEventEndTimeDialog = MutableStateFlow(false)
    private val openCameraGalleryDialog = MutableStateFlow(false)
    private val showStartAndEndTime = MutableStateFlow(true)
    private val showPermissionDialog = MutableStateFlow(false)
    private val launchCamera = MutableStateFlow(false)
    private val captureUri = MutableStateFlow<Uri?>(null)
    private val apiResultFlow =
        MutableStateFlow<NetworkResult<ApiResponse<CreateEventResponse>>?>(null)
    private val showLoader = MutableStateFlow(false)
    private val eventId = MutableStateFlow("")
    private val screenName = MutableStateFlow("")

    @SuppressLint("SimpleDateFormat")
    operator fun invoke(
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        context: Context,
        navigate: (NavigationAction) -> Unit,
    ): CreateEventUiState {
        /*  if (myEventData.isNullOrEmpty()) {
              val data: MyEventsData = Gson().fromJson(myEventData, MyEventsData::class.java)
              Log.d("TAG", "myEventData: $data")
          }*/
        return CreateEventUiState(
            eventNameFlow = eventNameFlow,
            onEventValueChange = { eventNameFlow.value = it;eventNameErrorFlow.value = null },
            eventNameErrorFlow = eventNameErrorFlow,
            onLocationClick = {},
            locationFlow = locationFlow,
            onLocationValueChange = {
                locationFlow.value = it
            },
            onLatLong = { lat, long ->
                Log.d("TAG", "latLong:$lat $long")
                latitude = lat
                longitude = long
            },
            eventDateFlow = eventDateFlow,
            openPermissionDialog = showPermissionDialog,
            onPermissionDialog = { showPermissionDialog.value = it },
            onBackClick = {
                navigate(NavigationAction.PopIntent)
            },
            apiResultFlow = apiResultFlow,
            onEventDateValueChange = { selectedDate ->
                val formatter: DateFormat = SimpleDateFormat("MM/dd/yyyy")
                formatter.timeZone = TimeZone.getTimeZone("UTC")
                try {
                    // Parse the selected date
                    val date: Date = formatter.parse(selectedDate) as Date
                    // Store the date value in eventDateFlow
                    eventDateFlow.value = date.time.toString()
                    eventDateErrorFlow.value = null // Reset any previous error
                    Log.d("TAG", "selectDate: ${eventDateFlow.value}")
                    // Clear start and end times since a new date is selected
                    eventStartTimeFlow.value = "" // Assuming you're using a Pair for time
                    eventEndTimeFlow.value =
                        "" // Reset to default values (e.g., 0 for both hour and minute)

                } catch (e: ParseException) {
                    // Handle parsing exception if needed
                    eventDateErrorFlow.value = "Invalid date format"
                    Log.e("TAG", "Date parse error: ${e.message}")
                }
            },
            onDateStarTime = { dateStartTime.value = it },
            onDateEndTime = { dateEndTime.value = it },
            eventDateErrorFlow = eventDateErrorFlow,
            eventStartTimeFlow = eventStartTimeFlow,
            onEventStartTimeValueChange = { hour, min ->
                val timestamp =
                    AppUtils.convertHourMinToTimestamp(hour, min, eventDateFlow.value.toLong())
                eventStartTimeFlow.value = timestamp.toString()
                eventStartTimeErrorFlow.value = null
            },
            eventStartTimeErrorFlow = eventStartTimeErrorFlow,
            eventEndTimeFlow = eventEndTimeFlow,
            onEventEndTimeValueChange = { hour, min ->
                val timestamp =
                    AppUtils.convertHourMinToTimestamp(hour, min, eventDateFlow.value.toLong())
                eventEndTimeFlow.value = timestamp.toString()
                eventEndTimeErrorFlow.value = null
            },
            myEventData = { myEventsData ->
                setCreateEventDataToFlow(myEventsData)
            },
            showLoader = showLoader,
            eventEndTimeErrorFlow = eventEndTimeErrorFlow,
            eventLinkFlow = eventLinkFlow,
            onEventLinkValueChange = { eventLinkFlow.value = it;eventLinkErrorFlow.value = null },
            eventLinkErrorFlow = eventLinkErrorFlow,
            openEventDialog = openEventDialog,
            onEventDateDismissDialog = { openEventDialog.value = it },
            openEventFirstTimeDialog = openEventStartTimeDialog,
            onEventFirstTimeDismissDialog = { openEventStartTimeDialog.value = it },
            openEventSecondTimeDialog = openEventEndTimeDialog,
            onEventSecondTimeDismissDialog = { openEventEndTimeDialog.value = it },
            openCameraGalleryDialog = openCameraGalleryDialog,
            onCameraGalleryDismissDialog = { openCameraGalleryDialog.value = it },
            eventDescriptionFlow = eventDescriptionFlow,
            onEventDescription = {
                eventDescriptionFlow.value = it;eventDescriptionErrorFlow.value = null
            },
            sendScreenName = {
                screenName.value = it
            },
            onImageFlag = { selectImage.value = it },
            eventBioErrorFlow = eventDescriptionErrorFlow,
            onClearUnUsedUseState = { clearUnUsedUseState() },
            profilePicFlow = profileImgFlow,
            launchCamera = launchCamera,
            onClickOfCamera = { createUriForCaptureImg(it) },
            onProfileImgPick = { profileImgFlow.value = it; launchCamera.value = false },
            captureUri = captureUri,
            createEventAPICall = {
                if (screenName.value == Constants.ContainerScreens.CREATE_EVENT_EDIT) {
                    // Get current date and time in UTC
                    val currentDate = ZonedDateTime.now(ZoneOffset.UTC).toLocalDate()
                    val currentTime = ZonedDateTime.now(ZoneOffset.UTC).toLocalTime()
                    try {
                        // Convert eventDateFlow to LocalDate
                        val eventDateTimestamp = eventDateFlow.value.toLongOrNull()
                        if (eventDateTimestamp == null) {
                            Toast.makeText(context, "Invalid event date.", Toast.LENGTH_SHORT)
                                .show()
                            return@CreateEventUiState
                        }
                        // Convert eventDateFlow to UTC LocalDate
                        val eventDate = Instant.ofEpochMilli(eventDateTimestamp)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                        // Convert eventStartTimeFlow to LocalTime in UTC
                        val eventStartTimeTimestamp = eventStartTimeFlow.value.toLongOrNull()
                        val eventStartTime = if (eventStartTimeTimestamp != null) {
                            Instant.ofEpochMilli(eventStartTimeTimestamp)
                                .atZone(ZoneOffset.UTC)
                                .toLocalTime()
                        } else {
                            LocalTime.MIN
                        }
                        // Convert eventEndTimeFlow to LocalTime in UTC
                        val eventEndTimeTimestamp = eventEndTimeFlow.value.toLongOrNull()
                        val eventEndTime = if (eventEndTimeTimestamp != null) {
                            Instant.ofEpochMilli(eventEndTimeTimestamp)
                                .atZone(ZoneOffset.UTC)
                                .toLocalTime()
                        } else {
                            LocalTime.MAX
                        }
                        // Check if the event date is today and if the start time is before the current time
                        if (eventDate.isEqual(currentDate) && eventStartTime.isBefore(currentTime)) {
                            Toasty.warning(
                                context,
                                context.getString(R.string.selected_time_cannot_be_before_the_current_time),
                                Toast.LENGTH_SHORT,
                                false
                            ).show()
                        }
                        else if (eventEndTime.isBefore(eventStartTime)) {
                            Toasty.warning(
                                context,
                                context.getString(R.string.end_time_cannot_be_before_start_time),
                                Toast.LENGTH_SHORT,
                                false
                            ).show()
                        } else {
                            if (isInfoLinkValid(context = context)) {
                                callEditEventAPI(coroutineScope, context, navigate)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e(
                            "CreateEventError",
                            "Error occurred while processing date or time: ${e.localizedMessage}", e
                        )
                    }
                } else {
                    if (isInfoValid(context)) {
                        callCreateEventAPI(coroutineScope, context, navigate)
                        Log.e(TAG, "invoke: ${eventStartTimeFlow.value}  ${eventEndTimeFlow.value}")
                        Log.e(TAG, "invoke Date: ${eventDateFlow.value}")
                    }
                }
            },
            navigateToEventDetailsScreen = { navigate(NavigationAction.PopIntent) },
            navigateToMyEventScreen = {
            },
            showStartAndTime = showStartAndEndTime,
            onShowStartAndTime = {
                showStartAndEndTime.value = it
            },
            isAllDay = isAllDay,
            onSwitchOffClick = { isAllDay.value = 2 },
            onSwitchOnClick = { isAllDay.value = 1 },
            clearAllApiResultFlow = { clearAllAPIResultFlow() }
        )
    }

    private fun setCreateEventDataToFlow(myEventData: String) {
        if (myEventData.isNotEmpty()) {
            val data: MyEventsData = Gson().fromJson(myEventData, MyEventsData::class.java)
            Log.d("TAG", "myEventData: $myEventData")
            eventNameFlow.value = data.eventName ?: ""
            eventDateFlow.value = data.eventDate.toString()
            eventStartTimeFlow.value = data.startTime.toString()
            eventEndTimeFlow.value = data.endTime.toString()
            locationFlow.value = data.location ?: ""
            eventLinkFlow.value = data.link
            eventDescriptionFlow.value = data.eventDescription
            profileImgFlow.value = data.photo ?: ""
            isAllDay.value = data.isAllDay
            eventId.value = data.id
            Log.d("TAG", "isAllDay: ${data.isAllDay}")
        }
    }

    private fun clearAllAPIResultFlow() {
        apiResultFlow.value = null
    }

    private fun callCreateEventAPI(
        coroutineScope: CoroutineScope,
        context: Context,
        navigate: (NavigationAction) -> Unit
    ) {
        val requestBody: HashMap<String, RequestBody> = hashMapOf()
        requestBody[Constants.CreateEvent.EVENT_NAME] =
            eventNameFlow.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        if (isAllDay.value == 2) {
            requestBody[Constants.CreateEvent.START_TIME] =
                eventStartTimeFlow.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            requestBody[Constants.CreateEvent.END_TIME] =
                eventEndTimeFlow.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        } else {
            requestBody[Constants.CreateEvent.START_TIME] =
                dateStartTime.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            requestBody[Constants.CreateEvent.END_TIME] =
                dateEndTime.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        }
        requestBody[Constants.CreateEvent.EVENT_DATE] =
            eventDateFlow.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        requestBody[Constants.CreateEvent.IS_ALL_DAY] =
            isAllDay.value.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        if (eventLinkFlow.value.isNotBlank()) requestBody[Constants.CreateEvent.LINK] =
            eventLinkFlow.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        if (locationFlow.value.isNotBlank()) requestBody[Constants.CreateEvent.LOCATION] =
            locationFlow.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        if (latitude.toString().isNotBlank()) requestBody[Constants.CreateEvent.LAT] =
            latitude.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        if (longitude.toString().isNotBlank()) requestBody[Constants.CreateEvent.LONG] =
            longitude.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        requestBody[Constants.CreateEvent.EVENT_DESCRIPTION] =
            eventDescriptionFlow.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        if (selectImage.value) {
            val eventImageFile = File(profileImgFlow.value)
            val eventImage =
                AppUtils.createMultipartBody(eventImageFile, Constants.CreateEvent.PHOTO)
            coroutineScope.launch {
                apiRepository.createWithImageEvent(requestBody, eventImage).collect {
                    when (it) {
                        is NetworkResult.Error -> {
                            showLoader.value = false
                            Toasty.warning(
                                context,
                                it.message.toString(),
                                Toast.LENGTH_SHORT,
                                false
                            ).show()
                        }

                        is NetworkResult.Loading -> {
                            showLoader.value = true
                        }

                        is NetworkResult.Success -> {
                            showLoader.value = false
                            popBackToLoginScreen { }
                            Toasty.success(
                                context,
                                it.data?.message ?: "",
                                Toast.LENGTH_SHORT,
                                false
                            ).show()
                            popBackToLoginScreen(navigate)
                        }

                        is NetworkResult.UnAuthenticated -> {
                            showLoader.value = false
                            Toasty.success(
                                context,
                                it.message.toString(),
                                Toast.LENGTH_SHORT,
                                false
                            ).show()
                        }
                    }
                }
            }
        } else {
            coroutineScope.launch {
                apiRepository.createEvent(requestBody).collect {
                    when (it) {
                        is NetworkResult.Error -> {
                            showLoader.value = false
                            Toasty.success(
                                context,
                                it.message.toString(),
                                Toast.LENGTH_SHORT,
                                false
                            ).show()
                        }

                        is NetworkResult.Loading -> {
                            showLoader.value = true
                        }

                        is NetworkResult.Success -> {
                            showLoader.value = false
                            Toasty.success(
                                context,
                                it.data?.message ?: "",
                                Toast.LENGTH_SHORT,
                                false
                            ).show()
                            popBackToLoginScreen(navigate)
                        }

                        is NetworkResult.UnAuthenticated -> {
                            showLoader.value = false
                            Toasty.success(context, it.toString(), Toast.LENGTH_SHORT, false).show()
                        }
                    }
                }
            }
        }
    }

    private fun callEditEventAPI(
        coroutineScope: CoroutineScope,
        context: Context,
        navigate: (NavigationAction) -> Unit
    ) {
        val requestBody: HashMap<String, RequestBody> = hashMapOf()
        requestBody[Constants.CreateEvent.EVENT_ID] =
            eventId.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        if (eventNameFlow.value.isNotBlank()) requestBody[Constants.CreateEvent.EVENT_NAME] =
            eventNameFlow.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        if (isAllDay.value == 2) {
            if (eventStartTimeFlow.value.isNotBlank()) requestBody[Constants.CreateEvent.START_TIME] =
                eventStartTimeFlow.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            if (eventEndTimeFlow.value.isNotBlank()) requestBody[Constants.CreateEvent.END_TIME] =
                eventEndTimeFlow.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        } else {
            if (dateStartTime.value.isNotBlank()) requestBody[Constants.CreateEvent.START_TIME] =
                dateStartTime.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            if (dateEndTime.value.isNotBlank()) requestBody[Constants.CreateEvent.END_TIME] =
                dateEndTime.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        }
        if (eventDateFlow.value.isNotBlank()) requestBody[Constants.CreateEvent.EVENT_DATE] =
            eventDateFlow.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        requestBody[Constants.CreateEvent.IS_ALL_DAY] =
            isAllDay.value.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        if (eventLinkFlow.value.isNotBlank()) requestBody[Constants.CreateEvent.LINK] =
            eventLinkFlow.value.toRequestBody("multipart/form-data".toMediaTypeOrNull()) else requestBody[Constants.CreateEvent.LINK] =
            "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        if (locationFlow.value.isNotBlank()) requestBody[Constants.CreateEvent.LOCATION] =
            locationFlow.value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        if (latitude.toString().isNotBlank()) requestBody[Constants.CreateEvent.LAT] =
            latitude.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        if (longitude.toString().isNotBlank()) requestBody[Constants.CreateEvent.LONG] =
            longitude.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        if (eventDescriptionFlow.value.isNotBlank()) requestBody[Constants.CreateEvent.EVENT_DESCRIPTION] =
            eventDescriptionFlow.value.toRequestBody("multipart/form-data".toMediaTypeOrNull()) else requestBody[Constants.CreateEvent.EVENT_DESCRIPTION] =
            "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        Log.d(TAG, "callEditEventAPI: ${eventLinkFlow.value}")
        if (selectImage.value) {
            val eventImageFile = File(profileImgFlow.value)
            val eventImage =
                AppUtils.createMultipartBody(eventImageFile, Constants.CreateEvent.PHOTO)
            coroutineScope.launch {
                apiRepository.editEventWithImage(requestBody, eventImage).collect {
                    when (it) {
                        is NetworkResult.Error -> {
                            showLoader.value = false
                            Toasty.error(context, it.message.toString(), Toast.LENGTH_SHORT, false)
                                .show()
                        }

                        is NetworkResult.Loading -> {
                            showLoader.value = true
                        }

                        is NetworkResult.Success -> {
                            showLoader.value = false
                            Toasty.success(
                                context,
                                it.data?.message ?: "",
                                Toast.LENGTH_SHORT,
                                false
                            ).show()
                            // navigate(NavigationAction.Navigate(EventDetailsRoute.createRoute()))
                            navigate(NavigationAction.PopIntent)
                            /*  navigateToCreateEvent(
                                  context,
                                  navigate,
                                  Constants.ContainerScreens.EVENT_DETAILS
                              )*/
                        }

                        is NetworkResult.UnAuthenticated -> {
                            showLoader.value = false
                            Toasty.success(
                                context,
                                it.message.toString(),
                                Toast.LENGTH_SHORT,
                                false
                            ).show()
                        }
                    }
                }
            }
        } else {
            coroutineScope.launch {
                apiRepository.editEvent(requestBody).collect {
                    when (it) {
                        is NetworkResult.Error -> {
                            showLoader.value = false
                            Toasty.warning(
                                context,
                                it.message.toString(),
                                Toast.LENGTH_SHORT,
                                false
                            ).show()
                        }

                        is NetworkResult.Loading -> {
                            showLoader.value = true
                        }

                        is NetworkResult.Success -> {
                            showLoader.value = false
                            Toasty.success(
                                context,
                                it.data?.message ?: "",
                                Toast.LENGTH_SHORT,
                                false
                            ).show()

                            navigate(NavigationAction.PopIntent)
                            //  navigate(NavigationAction.Navigate(EventDetailsRoute.createRoute()))
                            /* navigateToCreateEvent(
                                 context,
                                 navigate,
                                 Constants.ContainerScreens.EVENT_DETAILS
                             )*/
                        }

                        is NetworkResult.UnAuthenticated -> {
                            showLoader.value = false
                            Toasty.success(
                                context,
                                it.message.toString(),
                                Toast.LENGTH_SHORT,
                                false
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun popBackToLoginScreen(navigate: (NavigationAction) -> Unit) {
        navigate(NavigationAction.PopIntent)
    }

    private fun isInfoValid(context: Context): Boolean {
        var validInfo = true
        if (eventNameFlow.value.isBlank()) {
            eventNameErrorFlow.value = context.getString(R.string.please_enter_event_name)
            validInfo = false
        }
        if (eventDateFlow.value.isBlank()) {
            eventDateErrorFlow.value = context.getString(R.string.please_enter_event_date)
            validInfo = false
        }
        if (isAllDay.value == 2) {
            if (eventStartTimeFlow.value.isBlank()) {
                eventStartTimeErrorFlow.value =
                    context.getString(R.string.please_enter_event_start_time)
                validInfo = false
            }
            if (eventEndTimeFlow.value.isBlank()) {
                eventEndTimeErrorFlow.value =
                    context.getString(R.string.please_enter_event_end_time)
                validInfo = false
            }
        }
        if (eventDescriptionFlow.value.isBlank()) {
            eventDescriptionErrorFlow.value =
                context.getString(R.string.please_enter_event_description)
            validInfo = false
        }
        if (eventLinkFlow.value.isNotEmpty()) {
            if (!android.util.Patterns.WEB_URL.matcher(eventLinkFlow.value).matches()) {
                eventLinkErrorFlow.value = context.getString(R.string.please_enter_the_valid_link)
                validInfo = false

            }
        }
        return validInfo
    }

    private fun isInfoLinkValid(context: Context): Boolean {
        var validInfo = true
        if (eventNameFlow.value.isBlank()) {
            eventNameErrorFlow.value = context.getString(R.string.please_enter_event_name)
            validInfo = false
        }
        if (eventLinkFlow.value.isNotEmpty()) {
            if (!android.util.Patterns.WEB_URL.matcher(eventLinkFlow.value).matches()) {
                eventLinkErrorFlow.value = context.getString(R.string.please_enter_the_valid_link)
                validInfo = false
            }
        }
        if (isAllDay.value == 2) {
            if (eventStartTimeFlow.value.isBlank()) {
                eventStartTimeErrorFlow.value =
                    context.getString(R.string.please_enter_event_start_time)
                validInfo = false
            }
            if (eventEndTimeFlow.value.isBlank()) {
                eventEndTimeErrorFlow.value =
                    context.getString(R.string.please_enter_event_end_time)
                validInfo = false
            }
        }
        return validInfo
    }

    private fun createUriForCaptureImg(context: Context) {
        val file = context.createImageFile()
        val uri = FileProvider.getUriForFile(
            Objects.requireNonNull(context),
            BuildConfig.APPLICATION_ID + ".provider", file
        )
        Logger.e("capture uri: $uri")
        captureUri.value = uri
        launchCamera.value = true
    }

    @SuppressLint("SimpleDateFormat")
    fun Context.createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            externalCacheDir      /* directory */
        )
        return image
    }

    private fun clearUnUsedUseState() {
        launchCamera.value = false
    }

}