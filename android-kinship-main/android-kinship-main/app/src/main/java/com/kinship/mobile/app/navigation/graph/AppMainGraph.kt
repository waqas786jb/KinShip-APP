package com.kinship.mobile.app.navigation.graph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.navigation.SimpleNavComposeRoute
import com.kinship.mobile.app.ux.main.events.EventScreen
import com.kinship.mobile.app.ux.main.events.EventsRoute
import com.kinship.mobile.app.ux.main.home.HomeRoute
import com.kinship.mobile.app.ux.main.home.HomeScreen
import com.kinship.mobile.app.ux.main.home.link.LinkScreen
import com.kinship.mobile.app.ux.main.home.link.LinksRoute
import com.kinship.mobile.app.ux.main.home.member.MemberRoute
import com.kinship.mobile.app.ux.main.home.member.MemberScreen
import com.kinship.mobile.app.ux.main.home.profile.ProfileRoute
import com.kinship.mobile.app.ux.main.home.profile.ProfileScreen
import com.kinship.mobile.app.ux.main.message.MessageRoute
import com.kinship.mobile.app.ux.main.message.MessageScreen
import com.kinship.mobile.app.ux.container.setting.SettingRoute
import com.kinship.mobile.app.ux.container.setting.SettingScreen

@Composable
fun AppMainGraph(
    navController: NavHostController,
    screen: String,
    mainId:String,
    type:Int

) {
    val appStartDestination = when (screen) {
        Constants.ContainerScreens.MEMBER_SCREEN -> {
            MemberRoute.routeDefinition.value
        }
        Constants.ContainerScreens.LINK_SCREEN -> {
            LinksRoute.routeDefinition.value
        }
        Constants.ContainerScreens.MESSAGE_SCREEN -> {
            MessageRoute.routeDefinition.value
        }
        Constants.AppScreen.EVENT_SCREEN -> {
            EventsRoute.routeDefinition.value
        }
        Constants.ContainerScreens.SETTING_SCREEN -> {
            SettingRoute.routeDefinition.value
        }
        else -> {
            HomeRoute.routeDefinition.value
        }
    }
    NavHost(navController = navController, startDestination = appStartDestination) {
        (HomeRoute as SimpleNavComposeRoute).addNavigationRoute(this) { HomeScreen(navController) }
        (EventsRoute as SimpleNavComposeRoute).addNavigationRoute(this) { EventScreen(navController, mainId = mainId, type = type) }
        (MessageRoute as SimpleNavComposeRoute).addNavigationRoute(this) { MessageScreen(navController) }
        (SettingRoute as SimpleNavComposeRoute).addNavigationRoute(this) { SettingScreen(navController) }
       // MemberRoute.addNavigationRoute(this) { MemberScreen(navController) }
        (MemberRoute as SimpleNavComposeRoute).addNavigationRoute(this) { MemberScreen(navController) }
        ProfileRoute.addNavigationRoute(this) { ProfileScreen(navController) }
        LinksRoute.addNavigationRoute(this) { LinkScreen(navController) }
    }
}