package com.loan.icreditapp.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri

class JumpUtils {

    companion object {
        fun chatInWhatsApp(mContext: Context?, mobileNum: String?) {
            try {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://api.whatsapp.com/send?phone=$mobileNum")
                )
                intent.setPackage("com.whatsapp")
                mContext!!.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun isAppInstall(context: Context?, appName: String?): Boolean {
            var packageInfo: PackageInfo? = null
            try {
                packageInfo = context!!.packageManager.getPackageInfo(appName!!, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return packageInfo != null
        }
    }


}