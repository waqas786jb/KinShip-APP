package com.kinship.mobile.app.utils.services
import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kinship.mobile.app.data.source.Constants
import com.kinship.mobile.app.utils.notification.NotificationUtils
import com.kinship.mobile.app.ux.container.ContainerActivity
import com.kinship.mobile.app.ux.main.MainActivity
import kotlin.random.Random

class FireBaseMessageService : FirebaseMessagingService() {
    private val tag = "Messages"
    override fun onNewToken(s: String) {
        //Prefs.putString(Constants.PrefsKeys.FIREBASE_MESSAGE_TOKEN, s)
        Log.e("FirebaseToken", "The token refreshed $s")
        super.onNewToken(s)
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val gcmRec = Intent("NotificationReceived")
        gcmRec.putExtra("chatId", remoteMessage.data["groupId"])
        gcmRec.putExtra("count", remoteMessage.data["count"])
        Log.e(tag, "notificationType ${remoteMessage.data}")
        selectPushType(remoteMessage)

    }
    private fun selectPushType(remoteMessage: RemoteMessage) {
        var title = "" //baseContext.getString(R.string.app_name)
        var body = ""// baseContext.getString(R.string.app_name)
        if (remoteMessage.data["title"] != null)
            title = remoteMessage.data["title"].toString()

        if (remoteMessage.data["body"] != null)
            body = remoteMessage.data["body"].toString()

        NotificationUtils().sendNotification(
            baseContext,
            title,
            body,
            getNotificationIntent(remoteMessage),
            Random.nextInt(5000)
        )
    }
    private fun getNotificationIntent(remoteMessage: RemoteMessage): Intent {
        try {
            return if (!remoteMessage.data["type"].isNullOrEmpty()) {
                when (remoteMessage.data["type"]?.toInt()) {
                    Constants.NotificationPush.PUSH_KINSHIP_GROUP_TYPE -> {
                        Log.d(
                            "TAG", "getNotificationIntent:${remoteMessage.data["type"]?.toInt()}"
                        )
                        Log.e("TYPE_OF", "PUSH_KINSHIP_GROUP_TYPE")
                        val intent = Intent(this, ContainerActivity::class.java)
                        intent.putExtra(
                            Constants.IntentData.SCREEN_NAME,
                            Constants.ContainerScreens.CHAT_SCREEN
                        )
                    }
                    Constants.NotificationPush.PUSH_EVENT_CREATE -> {
                        Log.d(
                            "TAG",
                            "getNotificationIntent:${remoteMessage.data["type"]?.toInt()} "
                        )
                        Log.e("TYPE_OF", "PUSH_EVENT_CREATE")
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra(
                            Constants.IntentData.SCREEN_NAME,
                            Constants.AppScreen.EVENT_SCREEN
                        )
                    }
                    Constants.NotificationPush.PUSH_SINGLE_SUBGROUP_CHAT -> {
                        Log.d("TAG", "getNotificationIntent:${remoteMessage.data["type"]?.toInt()}")
                        Log.e("TYPE_OF", "PUSH_EVENT_CREATE")
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra(
                            Constants.IntentData.SCREEN_NAME,
                            Constants.AppScreen.MESSAGE_SCREEN
                        )
                    }
                    Constants.NotificationPush.COMMUNITY_POST_TYPE -> {
                        Log.d("TAG", "getNotificationIntent:${remoteMessage.data["type"]?.toInt()}")
                        Log.e("TYPE_OF", "PUSH_EVENT_CREATE")
                        val intent = Intent(this, ContainerActivity::class.java)
                        intent.putExtra(
                            Constants.IntentData.SCREEN_NAME,
                            Constants.ContainerScreens.MY_COMMUNITIES_SCREEN
                        )
                    }
                    else -> {
                        MainActivity.newIntent(baseContext)
                    }
                }
            } else {
                MainActivity.newIntent(baseContext)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return MainActivity.newIntent(baseContext)
        }
    }
}