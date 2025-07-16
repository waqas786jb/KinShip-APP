package com.kinship.mobile.app.navigation.graph
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.ux.container.memeberSingleChat.MemberSingleChatRoute
import com.kinship.mobile.app.ux.container.memeberSingleChat.MemberSingleChatScreen
import com.kinship.mobile.app.ux.container.rsvp.RsvpRoute
import com.kinship.mobile.app.ux.container.rsvp.RsvpScreen

@Composable
fun AppMemberChatGraph(
    navController: NavHostController,
    screen: String,
    memberData:String,
    chatId:String,
) {
    val appStartDestination = when (screen) {
        Constants.ContainerScreens.MEMBER_CHAT_SCREEN -> {
            MemberSingleChatRoute.routeDefinition.value
        }
        else -> ""
    }
    NavHost(navController = navController, startDestination = appStartDestination) {
        MemberSingleChatRoute.addNavigationRoute(this) { MemberSingleChatScreen (navController,memberData, chatId = chatId) }
    }
}
