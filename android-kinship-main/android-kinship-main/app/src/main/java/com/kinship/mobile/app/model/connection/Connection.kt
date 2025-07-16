package com.kinship.mobile.app.model.connection

data class Connection(
    val type: State,
    val isConnected: Boolean = true
) {
    enum class State {
        CELLULAR, WIFI, ETHERNET, VPN, NO_INTERNET
    }
}

//[https://blog.devgenius.io/exploring-clean-mvvm-architecture-in-android-using-kotlin-coroutines-room-hilt-retrofit-8656e0042b10]