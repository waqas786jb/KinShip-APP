package com.kinship.mobile.app.ux.startup.onboarding.splash
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kinship.mobile.app.R
import com.kinship.mobile.app.navigation.HandleNavigation
import com.kinship.mobile.app.ui.theme.Alto
import com.kinship.mobile.app.ui.theme.AppThemeColor
import com.kinship.mobile.app.ui.theme.HoneyFlower
import com.kinship.mobile.app.ui.theme.HoneyFlower44
import com.kinship.mobile.app.ui.theme.White

@Composable
fun SplashScreen(
    navController: NavController= rememberNavController(),
    bundle: Bundle?,
    viewModel: SplashViewModel = hiltViewModel(),
    restartApp:String

) {

    val uiState = viewModel.splashUiState
    val systemUiController = rememberSystemUiController()

    uiState.restartAppKey(restartApp)
    val defaultStatusBarColor = White
    val defaultSystemBarColor = White

    DisposableEffect(Unit) {
        systemUiController.setStatusBarColor(color = AppThemeColor)
        systemUiController.setNavigationBarColor(color = AppThemeColor)
        onDispose {
            systemUiController.setStatusBarColor(color = defaultStatusBarColor)
            systemUiController.setSystemBarsColor(color = defaultSystemBarColor)
        }
    }
    uiState.notificationKey(bundle?: Bundle.EMPTY)
    SplashScreenContent()
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}
@Composable
private fun SplashScreenContent() {
    Box(
        Modifier
            .background(
                Brush.linearGradient(
                    listOf(HoneyFlower, HoneyFlower44),
                )
            )
            .fillMaxSize()
    ) {
        Image(
            painterResource(id = R.drawable.ic_splash_kinship),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = Alto),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 20.dp),
        )
    }
}
@Preview
@Composable
private fun Preview() {

    Surface {
        SplashScreenContent()
    }
}