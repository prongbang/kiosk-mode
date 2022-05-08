package com.prongbang.kiosk

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.app.admin.SystemUpdatePolicy
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.UserManager
import android.provider.Settings
import android.view.View.*
import androidx.annotation.RequiresApi

open class AndroidDevicePolicyManager constructor(
    private val activity: Activity,
    private val componentName: ComponentName,
) : IAndroidDevicePolicyManager {

    private val devicePolicyManager =
        activity.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

    override fun isDeviceOwnerApp(): Boolean =
        devicePolicyManager.isDeviceOwnerApp(activity.packageName)

    @RequiresApi(Build.VERSION_CODES.S)
    override fun isPreferentialNetworkServiceEnabled(): Boolean =
        devicePolicyManager.isPreferentialNetworkServiceEnabled

    override fun isProfileOwnerApp(): Boolean =
        devicePolicyManager.isProfileOwnerApp(activity.packageName)

    override fun startLockTask() {
        setKioskPolicies(enable = true, isAdmin = isDeviceOwnerApp())
    }

    override fun stopLockTask() {
        setKioskPolicies(enable = false, isAdmin = isDeviceOwnerApp())
    }

    override fun <T> stopLockTaskAndStartActivity(cls: Class<T>) {
        stopLockTask()
        startActivityClearTop(cls)
    }

    override fun <T> startActivityClearTop(cls: Class<T>) {
        val intent = Intent(activity, cls).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        intent.putExtra(activity::class.java.name, false)
        activity.startActivity(intent)
    }

    override fun setKioskPolicies(enable: Boolean, isAdmin: Boolean) {
        if (isAdmin) {
            setRestrictions(enable)
            enableStayOnWhilePluggedIn(enable)
            setUpdatePolicy(enable)
            setAsHomeApp(enable)
            setKeyGuardEnabled(enable)
        }
        setLockTask(enable, isAdmin)
        setImmersiveMode(enable)
    }

    override fun enableStayOnWhilePluggedIn(active: Boolean) = if (active) {
        devicePolicyManager.setGlobalSetting(
            componentName,
            Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
            (BatteryManager.BATTERY_PLUGGED_AC or BatteryManager.BATTERY_PLUGGED_USB or BatteryManager.BATTERY_PLUGGED_WIRELESS).toString()
        )
    } else {
        devicePolicyManager.setGlobalSetting(
            componentName,
            Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
            "0"
        )
    }

    override fun setUserRestriction(restriction: String, disallow: Boolean) = if (disallow) {
        devicePolicyManager.addUserRestriction(componentName, restriction)
    } else {
        devicePolicyManager.clearUserRestriction(componentName, restriction)
    }

    override fun setLockTask(start: Boolean, isAdmin: Boolean) {
        if (isAdmin) {
            devicePolicyManager.setLockTaskPackages(
                componentName,
                if (start) arrayOf(activity.packageName) else arrayOf()
            )
        }
        if (start) {
            activity.startLockTask()
        } else {
            activity.stopLockTask()
        }
    }

    override fun setKeyGuardEnabled(enable: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            devicePolicyManager.setKeyguardDisabled(componentName, !enable)
        }
    }

    @Suppress("DEPRECATION")
    override fun setImmersiveMode(enable: Boolean) {
        if (enable) {
            val flags = (SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or SYSTEM_UI_FLAG_FULLSCREEN
                    or SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            activity.window.decorView.systemUiVisibility = flags
        } else {
            val flags = (SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            activity.window.decorView.systemUiVisibility = flags
        }
    }

    override fun setUpdatePolicy(enable: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (enable) {
                devicePolicyManager.setSystemUpdatePolicy(
                    componentName,
                    SystemUpdatePolicy.createWindowedInstallPolicy(60, 120)
                )
            } else {
                devicePolicyManager.setSystemUpdatePolicy(componentName, null)
            }
        }
    }

    override fun setRestrictions(disallow: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, disallow)
        }
        setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, disallow)
        setUserRestriction(UserManager.DISALLOW_ADD_USER, disallow)
        setUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, disallow)
        setUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, disallow)
    }

    override fun setAsHomeApp(enable: Boolean) {
        if (enable) {
            val intentFilter = IntentFilter(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                addCategory(Intent.CATEGORY_DEFAULT)
            }
            devicePolicyManager.addPersistentPreferredActivity(
                componentName,
                intentFilter,
                ComponentName(activity.packageName, activity::class.java.name)
            )
        } else {
            devicePolicyManager.clearPackagePersistentPreferredActivities(
                componentName,
                activity.packageName
            )
        }
    }
}