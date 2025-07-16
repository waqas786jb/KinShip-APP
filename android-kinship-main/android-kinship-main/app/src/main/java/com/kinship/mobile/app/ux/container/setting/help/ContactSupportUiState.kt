package com.kinship.mobile.app.ux.container.setting.help

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ContactSupportUiState(
    //reason flow
    val reasonFlow: StateFlow<String> = MutableStateFlow(""),
    val onReason: (String) -> Unit = {},
    val reasonErrorFlow: StateFlow<String?> = MutableStateFlow(null),

    // description flow
    val descriptionFlow: StateFlow<String> = MutableStateFlow(""),
    val ontDescription: (String) -> Unit = {},
    val descriptionErrorFlow: StateFlow<String?> = MutableStateFlow(null),

    val showLoader: StateFlow<Boolean> = MutableStateFlow(false),

    val showDialog: StateFlow<Boolean> = MutableStateFlow(false),
    val onShowDialog: (Boolean) -> Unit = {},
    val onBackClick:() -> Unit={},
    val onContactReportSendClick:() ->Unit ={}




)