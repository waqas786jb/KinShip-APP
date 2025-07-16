package com.kinship.mobile.app.ux.main.home.profile

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileUiState(
    //data

    //Click
    val kinshipName: String = "",
    val onBackClick: () -> Unit = {},
    val memberName:String="",
    var  userId:String="",
    val memberUserId:String="",
    val memberProfile:String="",
    var memberCite:String="",
    val memberBio:String="",
    val memberBirthDay: String = "",
    val firstname: String = "",
    val navigateToSingleGroupChat:() -> Unit = {},
    val showLoader: StateFlow<Boolean> = MutableStateFlow(false),
)