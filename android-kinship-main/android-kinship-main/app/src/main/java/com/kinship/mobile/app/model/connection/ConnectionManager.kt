package com.kinship.mobile.app.model.connection

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


class ConnectionManager @Inject constructor(@ApplicationContext context: Context) {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isConnected = MutableStateFlow(Connection(type = Connection.State.NO_INTERNET, isConnected = false))
    val isConnected: Flow<Connection> = _isConnected

    private val networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addCapability(NET_CAPABILITY_VALIDATED)
        .addCapability(NET_CAPABILITY_INTERNET)
        .build()

    init {
        // Observe network connectivity changes
        connectivityManager.registerNetworkCallback(networkRequest, object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                updateConnectionState(network)
            }

            override fun onLost(network: Network) {
                _isConnected.value = Connection(type = Connection.State.NO_INTERNET, isConnected = false)
            }

        })
    }

    private fun updateConnectionState(network: Network) {
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        val connectionType = when {
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> Connection.State.WIFI
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> Connection.State.CELLULAR
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> Connection.State.ETHERNET
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true -> Connection.State.VPN
            else -> Connection.State.NO_INTERNET
        }
        _isConnected.value = Connection(type = connectionType, isConnected = true)
    }
}


