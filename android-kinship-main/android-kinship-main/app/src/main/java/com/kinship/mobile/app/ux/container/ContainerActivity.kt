package com.kinship.mobile.app.ux.container

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
import androidx.core.view.WindowCompat
import com.kinship.mobile.app.AppActivity
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.ui.theme.White
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContainerActivity : AppActivity() {
    private val viewModel: ContainerViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = Color.White.toArgb()
        window.navigationBarColor = White.toArgb()
        val bundle = intent.getBundleExtra(Constants.BundleKey.EXTRA_BUNDLE)
        var messageData = ""
        var memberData = ""
        var eventId = ""
        var myEventData =""

        var chatId = ""
        if (bundle != null) {
            messageData = bundle.getString(Constants.BundleKey.MESSAGE_RESPONSE, "")
            myEventData = bundle.getString(Constants.BundleKey.MY_EVENT_RESPONSE, "")
            memberData = bundle.getString(Constants.BundleKey.MEMBER_RESPONSE, "")
            chatId = bundle.getString(Constants.BundleKey.CHAT_ID, "")
            eventId = bundle.getString(Constants.BundleKey.EVENT_ID, "")
        }

        setContent {
            Surface(
                modifier = Modifier
                    .navigationBarsPadding(),
            ) {
                ContainerScreen(
                    screen = intent.getStringExtra(Constants.IntentData.SCREEN_NAME).toString(),
                    messageData = messageData,
                    myEventData = myEventData,
                    memberData = memberData,
                    chatId = chatId,
                    eventId = eventId

                )
                Log.d("TAG", "onCreate: $memberData")
                //ChatScreen()
            }
        }
    }
}