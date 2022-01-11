package com.zinka.contactsdk.data.remote.retrofit

import com.zinka.contactsdk.data.remote.model.RequestLastSyncedTime
import com.zinka.contactsdk.data.remote.model.RequestSyncContactData
import com.zinka.contactsdk.data.remote.model.ResponseLastSyncedTime
import com.zinka.contactsdk.data.remote.model.ResponseSyncContactData
import com.zinka.contactsdk.utils.Constant
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * @author Abhay Pratap
 */
internal interface DataService {

    @POST(Constant.Api.GET_LAST_SYNC_TIME)
    suspend fun getLastSyncedTime(
        @Body requestLastSyncedTime: RequestLastSyncedTime
    ): ResponseLastSyncedTime

    @POST(Constant.Api.SYNC_CONTACT)
   suspend fun syncContact(
        @Body requestSyncContact: RequestSyncContactData
    ): ResponseSyncContactData

}