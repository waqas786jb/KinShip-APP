package com.kinship.mobile.app.utils.socket

import org.json.JSONObject

interface OnSocketEventsListener {
//    fun onRoomConnected():String
    fun onRoomConnected(roomID: String){}

    fun onGetUserStatus(isUserOnline: Boolean){}

    fun onNewMessageReceived(data: JSONObject){}
}