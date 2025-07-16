package com.kinship.mobile.app

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import co.touchlab.kermit.Logger
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.utils.AppUtils.isScreenLocked
import com.kinship.mobile.app.utils.socket.SocketClass
import com.kinship.mobile.app.utils.socket.SocketClass.loggerE
import com.kinship.mobile.app.ux.startup.StartupActivity
import dagger.hilt.android.HiltAndroidApp
import io.socket.emitter.Emitter
import kotlinx.coroutines.runBlocking

@HiltAndroidApp
class App : Application(), Application.ActivityLifecycleCallbacks {

    private val appPreferenceDataStore: AppPreferenceDataStore? = null
    private var accessToken: String = ""
    override fun onCreate() {
        super.onCreate()
        getAccessToken()
        instance = this
    }
    fun restartApp() {
        val intent = Intent(this, StartupActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(Constants.BundleKey.RESET, Constants.BundleKey.RESTART_APP)
        startActivity(intent)
    }
    fun getAccessToken(): String {
        try {
            runBlocking {
                val token = appPreferenceDataStore?.getUserAuthData()?.accessToken ?: ""
                accessToken = token
            }
        } catch (e: Exception) {
            Logger.e("exception")
        }
        return accessToken
    }
    companion object {
        var instance: App? = null
            private set
    }
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {


    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {

    }

}
