package com.zinka.contactsdk.worker

import android.content.Context
import androidx.work.*
import com.zinka.contactsdk.ContactSdk
import com.zinka.contactsdk.shared_preference.ContactPrefHelper
import com.zinka.contactsdk.utils.ContactUtils
import com.zinka.contactsdk.utils.Event
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Created by AbhayPratap on 02,January,2022
 */

internal class SyncContactPeriodicWorker(
    private val context: Context,
    private val parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {

    companion object {
        private const val TAG = "SyncContactPeriodicWorker"

        fun startPeriodicRequest(context: Context) {
            if (ContactUtils.checkContactPermissions(context) && ContactPrefHelper.getIsUserLoggedIn(
                    context
                )
            ) {
                val workRequestBuilder: PeriodicWorkRequest.Builder = PeriodicWorkRequest.Builder(
                    SyncContactPeriodicWorker::class.java,
                    15,
                    TimeUnit.MINUTES
                )
                val constraints =
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                val periodicWorkRequest = workRequestBuilder.setConstraints(constraints).build()
                WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    TAG,
                    ExistingPeriodicWorkPolicy.KEEP,
                    periodicWorkRequest
                )
            } else {
                WorkManager.getInstance(context).cancelAllWorkByTag(TAG)
            }

        }
    }

    override suspend fun doWork(): Result {
        logSyncContactJobStarted()
        SyncContactWorker.startOneTimeRequest(context)
        logSyncContactInitiated()
        return Result.success()
    }

    private fun logSyncContactJobStarted() {
        val body = JSONObject()
        try {
            body.put(Event.EventDataField.SYNC_TIME, System.currentTimeMillis())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        ContactSdk.logEventListener.invoke(Event.EventType.EVENT_SYNC_CONTACT_JOB_STARTED, body)
    }

    private fun logSyncContactInitiated() {
        val body = JSONObject()
        ContactSdk.logEventListener.invoke(Event.EventType.EVENT_SYNC_CONTACT_JOB_INITIATED, body)
    }

}