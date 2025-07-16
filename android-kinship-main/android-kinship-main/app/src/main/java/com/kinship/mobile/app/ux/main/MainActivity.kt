package com.kinship.mobile.app.ux.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.kinship.mobile.app.AppActivity
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.ui.theme.White
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppActivity() {
    companion object {
        fun newIntent(c: Context): Intent {
            val intent = Intent(c, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            return intent
        }
    }
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = White.toArgb()
        window.navigationBarColor = White.toArgb()

        val bundle = intent.getBundleExtra(Constants.BundleKey.EXTRA_BUNDLE)
        var mainId = ""
        var type = 0

        if (bundle != null) {
            mainId = bundle.getString(Constants.BundleKey.MAIN_ID, "")
            type = bundle.getInt(Constants.BundleKey.TYPE, 0)
        }
        Log.d("TAG", "mainId: $type")
        setContent {
            Surface(
                modifier = Modifier
                    .navigationBarsPadding(),
            ) {
                MainScreen(
                    screen = intent.getStringExtra(Constants.IntentData.SCREEN_NAME).toString(),
                    mainId = mainId,
                    type = type
                )
            }
        }
        generateFCMToken()
    }
    private fun generateFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.e("TAG", "FirebaseToken $token")
                viewModel.registerForPushAPI(token, this)
            } else {
                Log.e(
                    "TAG",
                    "Fetching FCM registration token failed ${task.exception?.localizedMessage}"
                )
                return@OnCompleteListener
            }
        })
    }
}