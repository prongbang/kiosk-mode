package com.prongbang.kiosk

import android.app.admin.DeviceAdminReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log

class AndroidDeviceAdminReceiver : DeviceAdminReceiver() {

    companion object {
        private val TAG = AndroidDeviceAdminReceiver::class.java.simpleName

        fun getComponentName(context: Context): ComponentName {
            return ComponentName(context.applicationContext, AndroidDeviceAdminReceiver::class.java)
        }
    }

    override fun onLockTaskModeEntering(context: Context, intent: Intent, pkg: String) {
        super.onLockTaskModeEntering(context, intent, pkg)
        Log.d(TAG, "onLockTaskModeEntering")
    }

    override fun onLockTaskModeExiting(context: Context, intent: Intent) {
        super.onLockTaskModeExiting(context, intent)
        Log.d(TAG, "onLockTaskModeExiting")
    }
}