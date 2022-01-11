package com.zinka.contactsdk.worker

import android.content.Context
import com.zinka.contactsdk.shared_preference.ContactPrefHelper

object TaskScheduler {
    fun scheduleTasks(context: Context) {
        if (ContactPrefHelper.getIsUserLoggedIn(context)) {
            SyncContactPeriodicWorker.startPeriodicRequest(context)
        }
    }
}