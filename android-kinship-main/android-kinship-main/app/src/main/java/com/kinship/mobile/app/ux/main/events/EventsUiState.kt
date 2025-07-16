package com.kinship.mobile.app.ux.main.events

import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.model.domain.response.ApiResponse
import com.kinship.mobile.app.model.domain.response.events.MyEventsData
import com.kinship.mobile.app.model.domain.response.message.MessageResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class EventsUiState(
    val clearAllApiResultFlow: () -> Unit = {},
    val onStatusClicked: (status: Int, id: String) -> Unit = { _, _ -> },
    val onStatusSuccess: () -> Unit = {},
    val navigateToCreateEvent: () -> Unit = {},
    val showLoader: StateFlow<Boolean> = MutableStateFlow(false),
    val apiEventList: MutableStateFlow<List<MyEventsData>> = MutableStateFlow(emptyList()),
    //
    val showEventNoFoundText: StateFlow<Boolean> = MutableStateFlow(false),

    val showDialog: StateFlow<Boolean> = MutableStateFlow(false),
    val onShowDialog: (Boolean) -> Unit = {},
    val noDataFoundMyEventText: StateFlow<Boolean> = MutableStateFlow(false),

    val apiMyEventList: MutableStateFlow<List<MyEventsData>> = MutableStateFlow(emptyList()),
    val navigateToMyEventEvent: (MyEventsData) -> Unit = {},

    val eventDeleteAPICall: (String) -> Unit = {},

    val apiUpComingList: MutableStateFlow<List<MyEventsData>> = MutableStateFlow(emptyList()),
    val showUpComingNoFoundText: StateFlow<Boolean> = MutableStateFlow(false),

    val navigateToRsvpScreen: (String) -> Unit = {},

    val onBackClick:()->Unit={},
    val myEventAndEventApiCall:() -> Unit = {},
    //API flow
  //  val apiEventsResultFlow: StateFlow<NetworkResult<ApiResponseNew<MyEventsData>>?> = MutableStateFlow(null),
    val apiEventStatusResultFlow: StateFlow<NetworkResult<ApiResponse<MessageResponse>>?> = MutableStateFlow(null),

    val isAllEventsRefreshing: StateFlow<Boolean> = MutableStateFlow(false)


)