package com.kinship.mobile.app.ux.container

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.graph.AppContainerGraph

import com.kinship.mobile.app.utils.ext.requireActivity

@Composable
fun ContainerScreen(
    viewModel: ContainerViewModel = hiltViewModel(LocalContext.current.requireActivity()),
    screen : String,
    messageData: String,
    myEventData:String,
    memberData:String,
    chatId:String,
    eventId:String,

) {
    val navController = rememberNavController()
    AppContainerGraph(navController = navController, screen = screen, messageData = messageData, myEventData = myEventData,memberData = memberData, chatId = chatId, eventId = eventId)
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}