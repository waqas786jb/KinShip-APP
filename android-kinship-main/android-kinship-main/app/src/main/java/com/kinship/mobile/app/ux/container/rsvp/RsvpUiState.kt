package com.kinship.mobile.app.ux.container.rsvp

import com.kinship.mobile.app.model.domain.response.evenrName.EventNameData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class RsvpUiState(
    val onYesClick: () -> Unit = {},
    val onNoClick: () -> Unit = {},
    val onBackClick: () -> Unit = {},
    val onMaybeClick: () -> Unit = {},
    val apiEventNameYesList: MutableStateFlow<List<EventNameData>> = MutableStateFlow(emptyList()),
    val apiEventNameNoList: MutableStateFlow<List<EventNameData>> = MutableStateFlow(emptyList()),
    val apiEventNameMaybeList: MutableStateFlow<List<EventNameData>> = MutableStateFlow(emptyList()),
    val showLoader: StateFlow<Boolean> = MutableStateFlow(false),
    val showRSVPNoFoundText: StateFlow<Boolean> = MutableStateFlow(false),
    val showRSVPYesFoundText: StateFlow<Boolean> = MutableStateFlow(false),
    val showRSVPMaybeFoundText: StateFlow<Boolean> = MutableStateFlow(false),
    val eventId:(String) -> Unit = {},
    val screen:(String) -> Unit = {}

)