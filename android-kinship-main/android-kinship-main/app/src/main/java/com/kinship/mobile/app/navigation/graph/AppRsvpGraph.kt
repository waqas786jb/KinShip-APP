package com.kinship.mobile.app.navigation.graph
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.ux.container.rsvp.RsvpRoute
import com.kinship.mobile.app.ux.container.rsvp.RsvpScreen

@Composable
fun AppRsvpGraph(
    navController: NavHostController,
    screen: String,
    eventId:String,

) {
    val appStartDestination = when (screen) {
        Constants.ContainerScreens.RSVP_SCREEN -> {
            RsvpRoute.routeDefinition.value
        }
        else -> ""
    }
    NavHost(navController = navController, startDestination = appStartDestination) {
        RsvpRoute.addNavigationRoute(this) { RsvpScreen (navController, eventId = eventId) }

    }
}
