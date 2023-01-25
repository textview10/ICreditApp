package com.loan.icreditapp.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.blankj.utilcode.util.NotificationUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.loan.icreditapp.R
import com.loan.icreditapp.global.Constant
import com.loan.icreditapp.ui.launcher.LauncherActivity

class FcmService : FirebaseMessagingService() {


    private val TAG = "FcmService"

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, " create .")
    }

    override fun handleIntent(intent: Intent) {
        super.handleIntent(intent)
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Constant.mFirebaseToken = s
        Constant.isNewToken = true
        Log.e(TAG, " on new token = " + Constant.mFirebaseToken)
    }

    private var id = 0

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.size > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            id++
            sendNotifyMessage(id, this@FcmService)
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body)
        }
    }

    private fun sendNotifyMessage(id: Int, context: Context) {
        NotificationUtils.notify(
            id
        ) { builder ->
            val intent = Intent(context, LauncherActivity::class.java)
            builder.setSmallIcon(R.mipmap.ic_logo)
                .setContentTitle("title")
                .setContentText("content text: ")
                .setContentIntent(
                    PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
                .setAutoCancel(true)
        }
    }
}