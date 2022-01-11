package com.example.myapplication

import android.app.Application
import android.util.Log
import com.example.myapplication.datalayer.RetrofitClientInstance
import com.facebook.stetho.Stetho
import com.zinka.contactsdk.ContactSdk
import org.json.JSONObject

class MyApplication : Application() {


    override fun onCreate() {
        super.onCreate()

        initStetho()
        initContactSdk()

    }

    private val logCallerIdEvent: (String, JSONObject) -> Unit = { e: String, p: JSONObject ->
        Log.v(e, p.toString())
    }

    private fun initContactSdk() {
        ContactSdk.setRetrofit(RetrofitClientInstance.getRetrofitInstance())
            .setLogEventListener(logCallerIdEvent)
            .build(applicationContext)
    }

    private fun initStetho() {
        Stetho.initializeWithDefaults(this)
    }
}