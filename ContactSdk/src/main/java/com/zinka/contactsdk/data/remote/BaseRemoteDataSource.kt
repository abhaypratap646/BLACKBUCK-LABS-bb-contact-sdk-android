package com.zinka.contactsdk.data.remote

import com.zinka.contactsdk.ContactSdk
import com.zinka.contactsdk.data.remote.retrofit.DataService

/**
 * Created by AbhayPratap on 01,Jan,2022
 */
internal open class BaseRemoteDataSource {
    fun getDataService(): DataService? {
        return ContactSdk.clientRetrofit.create(DataService::class.java)
    }

}