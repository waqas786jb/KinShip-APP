package com.kinship.mobile.app.ux.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kinship.mobile.app.navigation.HandleNavBarNavigation
import com.kinship.mobile.app.navigation.graph.AppMainGraph
import com.kinship.mobile.app.navigation.scaffold.AppNavBarData
import com.kinship.mobile.app.navigation.scaffold.AppNavBarType
import com.kinship.mobile.app.navigation.scaffold.AppScaffold
import com.kinship.mobile.app.ui.theme.White
import com.kinship.mobile.app.utils.ext.requireActivity
import com.kinship.mobile.app.ux.main.bottombar.AppNavigationBar

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(LocalContext.current.requireActivity()),
    screen: String,
    mainId:String,
    type:Int
) {
    val navController = rememberNavController()
    val currBackStackState by navController.currentBackStackEntryAsState()
    val currDestination = currBackStackState?.destination
    AppScaffold(
        containerColor = White,
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {
                AppNavigationBar(
                    currDestination = currDestination,
                    onNavItemClicked = { viewModel.onNavBarItemSelected(it) },
                )
            }
        )
    ) {
        AppMainGraph(navController = navController, screen, mainId = mainId, type = type)
    }
    HandleNavBarNavigation(viewModelNavBar = viewModel, navController = navController)
}