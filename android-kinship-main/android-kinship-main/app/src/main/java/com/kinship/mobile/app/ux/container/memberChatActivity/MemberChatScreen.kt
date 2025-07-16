package com.kinship.mobile.app.ux.container.memberChatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.graph.AppMemberChatGraph
import com.kinship.mobile.app.utils.ext.requireActivity

@Composable
fun MemberChatScreen(
    viewModel: MemberChatViewModel = hiltViewModel(LocalContext.current.requireActivity()),
    screen: String,
    memberData: String,
    chatId: String,
) {
    val navController = rememberNavController()
    AppMemberChatGraph(
        navController = navController, memberData = memberData,
        chatId = chatId, screen = screen
    )
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}