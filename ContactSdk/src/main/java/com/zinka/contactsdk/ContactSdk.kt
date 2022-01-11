package com.zinka.contactsdk

import android.annotation.SuppressLint
import android.content.Context
import com.zinka.contactsdk.di.applicationModule
import com.zinka.contactsdk.shared_preference.ContactPrefHelper
import com.zinka.contactsdk.worker.SyncContactWorker
import com.zinka.contactsdk.worker.TaskScheduler
import org.json.JSONObject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import retrofit2.Retrofit

@SuppressLint("StaticFieldLeak")
object ContactSdk {


    internal lateinit var clientRetrofit: Retrofit
    internal lateinit var clientContext: Context


    internal var logEventListener: (event: String, properties: JSONObject) -> Unit =
        { _: String, _: JSONObject -> }

    fun setIsUserLoggedIn(isLoggedIn: Boolean) = apply {
        ContactPrefHelper.setIsUserLoggedIn(clientContext, isLoggedIn)
    }

    fun setTenant(tenant: String) = apply {
        ContactPrefHelper.setTenant(clientContext, tenant)
    }

    fun setUserId(userId: String?) = apply {
        ContactPrefHelper.setUserId(clientContext, userId)
    }

    fun setSyncContactJobInterval(syncContactInterval: Long) = apply {
        ContactPrefHelper.setSyncContactJobInterval(clientContext, syncContactInterval)
    }


    fun setRetrofit(retrofit: Retrofit) = apply {
        clientRetrofit = retrofit

    }


    private fun start() {
        // Start Services here
       TaskScheduler.scheduleTasks(clientContext)
        //ContactContentObserver.registerContactObserver(clientContext)
    }

    fun build(context: Context): ContactSdk {
        clientContext = context
        /*loadKoinModules(
            (applicationModule)
        )*/
        startKoin {
            androidContext(clientContext)
            modules(applicationModule)
        }
        return ContactSdk
    }

    fun setContactIdData(
        userId: String,
        isUserLoggedIn: Boolean,
        tenant: String,
        syncContactInterval: Long
    ) {
        this.setUserId(userId)
            .setTenant(tenant)
            .setIsUserLoggedIn(isUserLoggedIn)
            .setSyncContactJobInterval(syncContactInterval)
            .start()
    }

    fun setLogEventListener(action: (event: String, property: JSONObject) -> Unit) = apply {
        logEventListener = action
    }

    fun clearContactData() {
        this.setUserId(null)
            .setIsUserLoggedIn(false)

    }

    fun sendDataToServer(context: Context,userId: String) {
        setUserId(userId)
        SyncContactWorker.startOneTimeRequest(context)
    }


}