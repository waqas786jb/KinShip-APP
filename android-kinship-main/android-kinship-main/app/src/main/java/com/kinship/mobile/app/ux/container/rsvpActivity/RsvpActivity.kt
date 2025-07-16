package com.kinship.mobile.app.ux.container.rsvpActivity

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.kinship.mobile.app.AppActivity
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.ui.theme.White
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RsvpActivity : AppActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = Color.White.toArgb()

        window.navigationBarColor = White.toArgb()
        val bundle = intent.getBundleExtra(Constants.BundleKey.EXTRA_BUNDLE)
        var eventId = ""
        if (bundle != null) {
            eventId = bundle.getString(Constants.BundleKey.EVENT_ID, "")

        }

        setContent {
            Surface(
                modifier = Modifier
                    .navigationBarsPadding(),
            ) {
                RsvpScreen(
                    screen = intent.getStringExtra(Constants.IntentData.SCREEN_NAME).toString(),
                    eventId = eventId
                    )
                Log.d("TAG", "eventId: $eventId")

            }
        }
    }
}