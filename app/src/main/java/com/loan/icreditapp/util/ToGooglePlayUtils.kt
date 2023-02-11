package com.loan.icreditapp.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.loan.icreditapp.BuildConfig

class ToGooglePlayUtils {

    companion object {
        const val googlePlay = "com.android.vending"

        fun transferToGooglePlay(context: Context) {
            try {
                val uri = Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage(googlePlay)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

}