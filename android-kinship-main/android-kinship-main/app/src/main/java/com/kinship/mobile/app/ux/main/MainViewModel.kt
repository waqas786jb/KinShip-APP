package com.kinship.mobile.app.ux.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.data.source.remote.helper.NetworkResult
import com.kinship.mobile.app.data.source.remote.repository.ApiRepository
import com.kinship.mobile.app.model.domain.request.resendOtp.RegisterForPushRequest
import com.kinship.mobile.app.navigation.DefaultNavBarConfig
import com.kinship.mobile.app.navigation.ViewModelNavBar
import com.kinship.mobile.app.navigation.ViewModelNavBarImpl
import com.kinship.mobile.app.utils.AppUtils
import com.kinship.mobile.app.ux.main.bottombar.NavBarItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val apiRepository: ApiRepository?,
) : ViewModel(), ViewModelNavBar<NavBarItem> by ViewModelNavBarImpl(NavBarItem.HOME, DefaultNavBarConfig(NavBarItem.getNavBarItemRouteMap())) {

    fun registerForPushAPI(token: String, context: Context) {
        val req = RegisterForPushRequest(
            token = token,
            deviceId = AppUtils.getDeviceId(context),
            platform = Constants.ANDROID
        )
        Log.d("TAG", "registerForPushAPI: ${AppUtils.getDeviceId(context)}")
        viewModelScope.launch {
            apiRepository?.registerForPush(req)?.collect {
                when (it) {
                    is NetworkResult.Error -> {}
                    is NetworkResult.Loading -> {}
                    is NetworkResult.Success -> {}
                    is NetworkResult.UnAuthenticated -> {}
                }
            }
        }
    }

}