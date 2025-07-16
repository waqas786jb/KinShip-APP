package com.kinship.mobile.app.ux.startup.onboarding.splash

import android.os.Bundle

class SplashUiState(
    val notificationKey: (Bundle) -> Unit = {},
    val restartAppKey:(String)->Unit={}
)



