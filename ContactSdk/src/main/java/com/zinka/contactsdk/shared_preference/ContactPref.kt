package com.zinka.contactsdk.shared_preference

import android.content.Context

/**
 * Created by AbhayPratap on 02,January,2022
 */

internal object ContactPrefHelper {

    private const val pref_name = "contact_preference"

    private const val SYNC_CONTACT_JOB_INTERVAL = "sync_contact_job_interval"
    private const val LAST_SYNC_TIME = "last_sync_time"
    private const val IS_USER_LOGGED_IN = "is_user_logged_in"
    private const val USER_ID = "user_id"
    private const val TENANT = "tenant"


    fun setTenant(context: Context, userId: String) {
        val pref = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE)
        pref.edit().putString(TENANT, userId).apply()
    }

    fun getTenant(context: Context): String? {
        val pref = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE)
        return pref.getString(TENANT, null)
    }

    fun setUserId(context: Context, userId: String?) {
        val pref = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE)
        pref.edit().putString(USER_ID, userId).apply()
    }

    fun getUserId(context: Context): String? {
        val pref = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE)
        return pref.getString(USER_ID, null)
    }

    fun setSyncContactJobInterval(context: Context, interval: Long) {
        val pref = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE)
        pref.edit().putLong(SYNC_CONTACT_JOB_INTERVAL, interval).apply()
    }

    fun getSyncContactJobInterval(context: Context): Long {
        val pref = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE)
        return pref.getLong(SYNC_CONTACT_JOB_INTERVAL, 6)
    }

    fun setIsUserLoggedIn(context: Context, isUserLoggedIn: Boolean) {
        val pref = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE)
        pref.edit().putBoolean(IS_USER_LOGGED_IN, isUserLoggedIn).apply()
    }

    fun getIsUserLoggedIn(context: Context): Boolean {
        val pref = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE)
        return pref.getBoolean(IS_USER_LOGGED_IN, false)
    }

    fun setLastSyncTime(context: Context, lastSyncTime: Long) {
        val pref = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE)
        pref.edit().putLong(LAST_SYNC_TIME, lastSyncTime).apply()
    }

    fun getLastSyncTime(context: Context): Long {
        val pref = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE)
        return pref.getLong(LAST_SYNC_TIME, 0)
    }
}