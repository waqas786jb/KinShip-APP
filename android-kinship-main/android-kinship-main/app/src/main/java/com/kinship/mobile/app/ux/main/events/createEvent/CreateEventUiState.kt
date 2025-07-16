package com.kinship.mobile.app.ux.main.events.createEvent

import android.content.Context
import android.net.Uri
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.createEvent.CreateEventResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class CreateEventUiState(
    //data
    val eventNameFlow: StateFlow<String> = MutableStateFlow(""),
    val onEventValueChange: (String) -> Unit = {},

    val locationFlow: StateFlow<String> = MutableStateFlow(""),
    val onLocationValueChange: (String) -> Unit = {},
    val onLatLong: (Double,  Double) -> Unit = { lat: Double, long: Double -> },

    val eventDateFlow: StateFlow<String> = MutableStateFlow(""),
    val onEventDateValueChange: (String) -> Unit = {},
    val eventStartDateFlow: StateFlow<String> = MutableStateFlow(""),
    val onDateStarTime: (String) -> Unit = {},
    val eventEndDateFlow: StateFlow<String> = MutableStateFlow(""),
    val onDateEndTime: (String) -> Unit = {},

    val onBackClick: () -> Unit = {},

    val eventStartTimeFlowTempShow: StateFlow<String> = MutableStateFlow(""),
    val eventStartTimeFlow: StateFlow<String> = MutableStateFlow(""),
    val onEventStartTimeValueChange: (Int, Int) -> Unit = { i: Int, i1: Int -> },

    val eventEndTimeFlowTempShow: StateFlow<String> = MutableStateFlow(""),
    val eventEndTimeFlow: StateFlow<String> = MutableStateFlow(""),
    val onEventEndTimeValueChange: (Int, Int) -> Unit = { i: Int, i1: Int -> },




    val eventLinkFlow: StateFlow<String> = MutableStateFlow(""),
    val onEventLinkValueChange: (String) -> Unit = {},

    val eventDescriptionFlow: StateFlow<String> = MutableStateFlow(""),
    val onPlaceApiKey: () -> Unit = {},
    val onEventDescription: (String) -> Unit = {},


    val onProfileImgPick: (String) -> Unit = {},
    val onClearUnUsedUseState: () -> Unit = {},
    val onClickOfCamera: (Context) -> Unit = {},
    val captureUri: StateFlow<Uri?> = MutableStateFlow(null),

    val profilePicFlow: StateFlow<String> = MutableStateFlow(""),

    val onImageFlag: (Boolean) -> Unit = {},
    val launchCamera: StateFlow<Boolean> = MutableStateFlow(false),

    var createEventAPICall: () -> Unit = {},


    val openEventDialog: StateFlow<Boolean> = MutableStateFlow(false),
    val onEventDateDismissDialog: (Boolean) -> Unit = {},
    val onLocationClick: () -> Unit = {},

    val showStartAndTime: StateFlow<Boolean> = MutableStateFlow(true),
    val onShowStartAndTime: (Boolean) -> Unit = {},

    val openEventFirstTimeDialog: StateFlow<Boolean> = MutableStateFlow(false),
    val onEventFirstTimeDismissDialog: (Boolean) -> Unit = {},

    val openEventSecondTimeDialog: StateFlow<Boolean> = MutableStateFlow(false),
    val onEventSecondTimeDismissDialog: (Boolean) -> Unit = {},

    val openCameraGalleryDialog: StateFlow<Boolean> = MutableStateFlow(false),
    val onCameraGalleryDismissDialog: (Boolean) -> Unit = {},

    val openPermissionDialog: StateFlow<Boolean> = MutableStateFlow(false),
    val onPermissionDialog: (Boolean) -> Unit = {},
    val isAllDay: StateFlow<Int> = MutableStateFlow(0),
    val onSwitchOnClick: () -> Unit = {},
    val onSwitchOffClick: () -> Unit = {},


    val showLoader: StateFlow<Boolean> = MutableStateFlow(false),

    val navigateToEventDetailsScreen: () -> Unit = {},
    val navigateToMyEventScreen: () -> Unit = {},

    //fun handleDateSelection(dateString: String, onDateSelected: (String, Long, Long) -> Unit) {
    var onDateSelected:(String,String,Long,Long) ->Unit ={_:String,_:String,_:Long,_:Long->},


    val onMessageSend: (String,  Int) -> Unit = { _: String, _: Int -> },



    // errorFlow
    val eventNameErrorFlow: StateFlow<String?> = MutableStateFlow(null),
    val eventDateErrorFlow: StateFlow<String?> = MutableStateFlow(null),
    val eventLinkErrorFlow: StateFlow<String?> = MutableStateFlow(null),
    val eventStartTimeErrorFlow: StateFlow<String?> = MutableStateFlow(null),
    val eventEndTimeErrorFlow: StateFlow<String?> = MutableStateFlow(null),
    val eventBioErrorFlow: StateFlow<String?> = MutableStateFlow(null),

    val apiResultFlow: StateFlow<NetworkResult<ApiResponse<CreateEventResponse>>?> = MutableStateFlow(null),

    //response clear
    val clearAllApiResultFlow: () -> Unit = {},
    val myEventData:(String) -> Unit ={},
    val sendScreenName:(String)-> Unit= {},




    )