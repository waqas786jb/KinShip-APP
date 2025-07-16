package com.kinship.mobile.app.ux.container.setting.eventDetails

import com.kinship.mobile.app.model.domain.response.events.MyEventsData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EventDetailsUiState(
    //event
    val clearAllApiResultFlow: () -> Unit = {},
    val onApiSuccess: () -> Unit = {},
    val onBackClick: () -> Unit = {},
    //Api response
    val navigateToRsvpScreen: (String) -> Unit = {},
    val eventId: (String) -> Unit = {},
    val showLoader: StateFlow<Boolean> = MutableStateFlow(false),
    val showUpComingLoader: StateFlow<Boolean> = MutableStateFlow(false),
    val apiEventList: MutableStateFlow<List<MyEventsData>> = MutableStateFlow(emptyList()),
    val apiUpComingList: MutableStateFlow<List<MyEventsData>> = MutableStateFlow(emptyList()),
    val showNoDataFoundText: StateFlow<Boolean> = MutableStateFlow(false),
    val showUpComingNoFoundText: StateFlow<Boolean> = MutableStateFlow(false),
    val myEventClick: () -> Unit = {},
    val upComingClick: () -> Unit = {},
    val eventDeleteAPICall: (String) -> Unit = {},
    val navigateToCreateEvent: (MyEventsData) -> Unit = {},
    val sendScreenName: (String) -> Unit = {},
    val myEventAndEventApiCall:() -> Unit = {},
    val isAllEventsRefreshing: StateFlow<Boolean> = MutableStateFlow(false)
)
