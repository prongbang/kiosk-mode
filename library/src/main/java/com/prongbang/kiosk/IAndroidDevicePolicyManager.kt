package com.prongbang.kiosk

interface IAndroidDevicePolicyManager {
    fun stopLockTask()
    fun startLockTask()
    fun isDeviceOwnerApp(): Boolean
    fun isProfileOwnerApp(): Boolean
    fun setAsHomeApp(enable: Boolean)
    fun setUpdatePolicy(enable: Boolean)
    fun setImmersiveMode(enable: Boolean)
    fun setRestrictions(disallow: Boolean)
    fun setKeyGuardEnabled(enable: Boolean)
    fun <T> startActivityClearTop(cls: Class<T>)
    fun enableStayOnWhilePluggedIn(active: Boolean)
    fun setLockTask(start: Boolean, isAdmin: Boolean)
    fun isPreferentialNetworkServiceEnabled(): Boolean
    fun <T> stopLockTaskAndStartActivity(cls: Class<T>)
    fun setKioskPolicies(enable: Boolean, isAdmin: Boolean)
    fun setUserRestriction(restriction: String, disallow: Boolean)
}