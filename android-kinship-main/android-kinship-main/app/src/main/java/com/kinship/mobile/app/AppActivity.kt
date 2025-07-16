package com.kinship.mobile.app

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.utils.AppUtils.isScreenLocked
import com.kinship.mobile.app.utils.socket.OnSocketEventsListener
import com.kinship.mobile.app.utils.socket.SocketClass
import com.kinship.mobile.app.utils.socket.SocketClass.loggerE
import com.kinship.mobile.app.ux.startup.StartupActivity
import io.socket.emitter.Emitter
import org.json.JSONObject


open class AppActivity : ComponentActivity() {
    private var onSocketEventsListener: OnSocketEventsListener? = null
    private var accessToken: String = ""

    @SuppressLint("ObsoleteSdkInt", "RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // initSocketListener(this)
        //initSocketListener()
        setTheme(R.style.Theme_Kinship)
        accessToken = App.instance?.getAccessToken() ?: ""
        Log.d("TAG", "Application accessToken: $accessToken")
    }
    override fun onResume() {
       super.onResume()
        SocketClass.connectSocket(accessToken)
        if (!isScreenLocked()) {
            initSocketListener()
        }
    }
    override fun onPause() {
        super.onPause()
        if (!isScreenLocked()) {
          //  destroySocketListeners()
            //initSocketListener()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        destroySocketListeners()
    }
  fun initSocketListener() {
        SocketClass.getSocket(accessToken)?.let {
            Log.e("TAG", "initSocketListener: connect socket listener ")
            it.on(io.socket.client.Socket.EVENT_CONNECT, onConnected)
            it.on(io.socket.client.Socket.EVENT_CONNECT_ERROR, onConnectError)
            it.on(io.socket.client.Socket.EVENT_DISCONNECT, onDisconnected)
            it.on(Constants.Socket.ROOM_CONNECTED, onRoomConnected) //get callback from server
            it.on(Constants.Socket.ROOM_DISCONNECTED, onRoomDisconnected) //get callback from server
           // it.on(Constants.Socket.STATUS_ONLINE, onUserStatus) //get callback from server
            it.on(Constants.Socket.NEW_MESSAGE, onNewMessage)
        }
    }
     private fun destroySocketListeners() {
        SocketClass.getSocket(accessToken)?.let {
            loggerE("destroy socket listener")
            it.off(io.socket.client.Socket.EVENT_CONNECT, onConnected)
            it.off(io.socket.client.Socket.EVENT_CONNECT_ERROR, onConnectError)
            it.off(io.socket.client.Socket.EVENT_DISCONNECT, onDisconnected)
            it.off(Constants.Socket.ROOM_CONNECTED, onRoomConnected)
            it.off(Constants.Socket.ROOM_DISCONNECTED, onRoomDisconnected)
           // it.off(Constants.Socket.STATUS_ONLINE, onUserStatus)
            it.off(Constants.Socket.NEW_MESSAGE, onNewMessage)
        }
    }

    fun initSocketListener(socketEventsListener: OnSocketEventsListener) {
        this.onSocketEventsListener = socketEventsListener
    }

    //listener object
    private val onConnected = Emitter.Listener {
        SocketClass.getSocket(accessToken)?.let { it1 ->
            Log.e("TAG", "initSocketListener>>>>>: ${it1.connected()}")
//            if (it1.connected()) {
//                it1.emit(Constants.Socket.UPDATE_STATUS_TO_ONLINE, updateStatusToOnline())
//            }
        }
    }

    private val onConnectError = Emitter.Listener { args ->
        loggerE("onConnectError: ${args[0]}")
    }
    private val onDisconnected = Emitter.Listener {args->
        Log.e("TAG", "disConnectSocket: disconnect")
        Log.d("TAG", "socketDisconnect${args[0]}: ")
        SocketClass.getSocket(accessToken)?.let { it1 ->
            Log.e("TAG", "disConnectSocket: disconnect: ${it1.connected()}")

            it1.disconnect()
        }
    }
    private val onRoomConnected = Emitter.Listener { args ->
        loggerE("Socket onRoomCreated")

        val roomId = when (val arg = args[0]) {
            is String -> arg
            is Int -> arg.toString()  // Convert Integer to String if needed
            else -> null  // Handle unexpected types
        }
        roomId?.let {
            Log.e(ContentValues.TAG, "RoomId: $roomId")
            onSocketEventsListener?.onRoomConnected(roomId)
        } ?: Log.e(ContentValues.TAG, "Invalid RoomId type")
    }
    private val onRoomDisconnected = Emitter.Listener { args ->
        runOnUiThread {
            loggerE("Socket room disconnected")
        }
    }
  /*  private val onUserStatus = Emitter.Listener { args ->
        runOnUiThread {
            val status = args[0] as JSONObject
            if (status != null) {
                loggerE("user status data: $status")
                val isUserOnline = status.getBoolean(Constants.Socket.IS_ONLINE)
                onSocketEventsListener?.onGetUserStatus(isUserOnline = isUserOnline)
            }
        }
    }*/

    private var onNewMessage = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as JSONObject
            loggerE("Socket onNewMessage run: $data")
            onSocketEventsListener?.onNewMessageReceived(data)
        }
    }

    private fun updateStatusToOnline(): JSONObject {
        val statusObject = JSONObject()
//        statusObject.put("senderId", userId)
        Log.e("updateStatusToOnline ", statusObject.toString())
        return statusObject
    }

    /* override fun onResume() {
         super.onResume()
         SocketClass.connectSocket(accessToken)
         if (!isScreenLocked()) {
             initSocketListener()
         }
     }

     override fun onPause() {
         super.onPause()
         if (!isScreenLocked()) destroySocketListeners()
     }

     fun initSocketListener() {
         SocketClass.getSocket(accessToken)?.let {
             Log.e("TAG", "initSocketListener: connect socket listener ")
             it.on(io.socket.client.Socket.EVENT_CONNECT, onConnected)
             it.on(io.socket.client.Socket.EVENT_CONNECT_ERROR, onConnectError)
             it.on(io.socket.client.Socket.EVENT_DISCONNECT, onDisconnected)
             it.on(Constants.Socket.ROOM_CONNECTED, onRoomConnected) //get callback from server
             it.on(Constants.Socket.STATUS_ONLINE, onUserStatus) //get callback from server
             it.on(Constants.Socket.NEW_MESSAGE, onNewMessage)
         }
     }

     private fun destroySocketListeners() {
         SocketClass.getSocket(accessToken)?.let {
             loggerE("destroy socket listener")
             it.off(io.socket.client.Socket.EVENT_CONNECT, onConnected)
             it.off(io.socket.client.Socket.EVENT_CONNECT_ERROR, onConnectError)
             it.off(io.socket.client.Socket.EVENT_DISCONNECT, onDisconnected)
             it.off(Constants.Socket.ROOM_CONNECTED, onRoomConnected)
             it.off(Constants.Socket.STATUS_ONLINE, onUserStatus)
             it.off(Constants.Socket.NEW_MESSAGE, onNewMessage)
         }
     }

     fun initSocketListener(socketEventsListener: OnSocketEventsListener) {
         this.onSocketEventsListener = socketEventsListener
     }

     //listener object
     private val onConnected = Emitter.Listener {
         SocketClass.getSocket(accessToken)?.let { it1 ->
             Log.e("TAG", "initSocketListener>>>>>: ${it1.connected()}")
             if (it1.connected()) {
                 it1.emit(Constants.Socket.UPDATE_STATUS_TO_ONLINE, updateStatusToOnline())
             }
         }
     }

     private val onConnectError = Emitter.Listener { args ->
         loggerE("onConnectError: ${args[0]}")
     }

     private val onDisconnected = Emitter.Listener {
         Log.e("TAG", "disConnectSocket: disconnect")
 //        SocketClass.getSocket(accessToken)?.disconnect()
     }

     private val onRoomConnected = Emitter.Listener { args ->
         runOnUiThread {
             loggerE("Socket onRoomCreated")
             val roomId = args[0] as String
             Log.e(ContentValues.TAG, "RoomId: $roomId")
             onSocketEventsListener?.onRoomConnected(roomId)
         }
     }

     private val onUserStatus = Emitter.Listener { args ->
         runOnUiThread {
             val status = args[0] as JSONObject
             if (status != null) {
                 loggerE("user status data: $status")
                 val isUserOnline = status.getBoolean(Constants.Socket.IS_ONLINE)
                 onSocketEventsListener?.onGetUserStatus(isUserOnline = isUserOnline)
             }
         }
     }

     private var onNewMessage = Emitter.Listener { args ->
         runOnUiThread {
             val data = args[0] as JSONObject
             loggerE("Socket onNewMessage run: $data")
             onSocketEventsListener?.onNewMessageReceived(data)
         }
     }

     private fun updateStatusToOnline(): JSONObject {
         val statusObject = JSONObject()
         //val userId = getUserData().id
         //statusObject.put("senderId", userId)
         //statusObject.put("isOnline", 1)
         Log.e("updateStatusToOnline ", statusObject.toString())
         return statusObject
     }*/

}