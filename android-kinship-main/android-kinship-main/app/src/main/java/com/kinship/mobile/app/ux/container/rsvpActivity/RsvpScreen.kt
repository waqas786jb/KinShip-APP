package com.kinship.mobile.app.ux.container.rsvpActivity

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.graph.AppRsvpGraph
import com.kinship.mobile.app.utils.ext.requireActivity

@Composable
fun RsvpScreen(
    viewModel: RsvpViewModel = hiltViewModel(LocalContext.current.requireActivity()),
    screen : String,
    eventId:String,
    ) {
    val navController = rememberNavController()
    AppRsvpGraph(navController = navController, screen = screen, eventId = eventId)
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}