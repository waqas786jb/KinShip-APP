package com.kinship.mobile.app.ux.startup

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.kinship.mobile.app.AppActivity
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.ui.theme.KinshipAppTheme
import com.kinship.mobile.app.ui.theme.White
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartupActivity : AppActivity() {
    private val viewModel: StartupViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        enableEdgeToEdge()
        val bundle = intent.extras.let {
            it
        }
        Log.d("TAG", "onCreate: $bundle")
          installSplashScreen()

        val reset = intent.getStringExtra(Constants.BundleKey.RESET)
        Log.d("TAG", "reset: $reset")

        window.statusBarColor = White.toArgb()
        window.navigationBarColor = White.toArgb()
        //if want to some start up operation then call below function
        viewModel.startup()
        setContent {
            KinshipAppTheme() {
                Surface(
                    modifier = Modifier
                        .navigationBarsPadding(),
                ) {
                    StartupScreen(bundle, restartApp = reset?:"")
                }
            }
        }
    }
}


