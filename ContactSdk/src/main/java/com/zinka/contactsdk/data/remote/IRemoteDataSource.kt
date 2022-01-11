package com.zinka.contactsdk.data.remote

import com.zinka.contactsdk.data.remote.model.RequestLastSyncedTime
import com.zinka.contactsdk.data.remote.model.RequestSyncContactData
import com.zinka.contactsdk.data.remote.model.ResponseLastSyncedTime
import com.zinka.contactsdk.data.remote.model.ResponseSyncContactData
import com.zinka.contactsdk.data.remote.retrofit.Result


/**
 * Created by AbhayPratap on 01,January,2022
 */
internal interface IRemoteDataSource {

    suspend fun getLastSyncedContact(
        requestLastSyncedTime: RequestLastSyncedTime
    ): Result<ResponseLastSyncedTime>

    suspend fun syncContact(
        requestSyncContact: RequestSyncContactData
    ): Result<ResponseSyncContactData>


}