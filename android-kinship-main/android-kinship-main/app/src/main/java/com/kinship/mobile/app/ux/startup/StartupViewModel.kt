package com.kinship.mobile.app.ux.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.kinship.mobile.app.navigation.ViewModelNav
import com.kinship.mobile.app.navigation.ViewModelNavImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartupViewModel
@Inject constructor(
    private val appPreferenceDataStore: AppPreferenceDataStore
): ViewModel(), ViewModelNav by ViewModelNavImpl() {
    var isReady: Boolean = false
        private set

    var startDestination = ""

     //**** Enable this function is you want to any operation on app start up... ****\\
    fun startup() = viewModelScope.launch {
        // run any startup/initialization code here (NOTE: these tasks should NOT exceed 1000ms (per Google Guidelines))
        Logger.i { "Startup task..." }
         startDestination = appPreferenceDataStore.getStatUpStartDestination() ?: Constants.AppScreen.START_UP
        // Startup finished
        isReady = true
        Logger.i { "Startup finished" }
    }
}