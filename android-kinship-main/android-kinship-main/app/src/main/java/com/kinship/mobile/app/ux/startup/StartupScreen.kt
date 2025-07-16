package com.kinship.mobile.app.ux.startup

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.navigation.graph.AppStartUpGraph
import com.kinship.mobile.app.ui.theme.White
import com.kinship.mobile.app.utils.ext.requireActivity

@Composable
fun StartupScreen(bundle: Bundle?,viewModel: StartupViewModel = hiltViewModel(LocalContext.current.requireActivity()),restartApp:String) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val systemUi= rememberSystemUiController()
    AppStartUpGraph(navController = navController, startDestination = viewModel.startDestination,bundle, restartApp = restartApp)
    HandleNavigation(viewModelNav = viewModel, navController = navController)

    /*DisposableEffect(key1 = context) {
        systemUi.setNavigationBarColor(color = White)
        systemUi.setStatusBarColor(color = White)
        onDispose {
            systemUi.setNavigationBarColor(color = White)
            systemUi.setStatusBarColor(color = White)
        }
    }*/
}
