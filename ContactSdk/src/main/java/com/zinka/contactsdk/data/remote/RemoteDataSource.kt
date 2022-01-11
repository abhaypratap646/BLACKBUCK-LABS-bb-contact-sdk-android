package com.zinka.contactsdk.data.remote

import android.content.Context
import com.zinka.contactsdk.data.remote.model.*
import com.zinka.contactsdk.data.remote.retrofit.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by AbhayPratap on 03,January,2022
 */
internal class RemoteDataSource private constructor(var context: Context) :
    IRemoteDataSource,
    BaseRemoteDataSource() {

    companion object {
        @Volatile
        private var INSTANCE: RemoteDataSource? = null

        fun getInstance(context: Context): RemoteDataSource {
            return INSTANCE ?: synchronized(this) {
                val instance = RemoteDataSource(context)
                INSTANCE = instance
                instance
            }
        }
    }

    override suspend fun getLastSyncedContact(
        requestLastSyncedTime: RequestLastSyncedTime
    ): Result<ResponseLastSyncedTime> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                Result.Success(getDataService()?.getLastSyncedTime(requestLastSyncedTime)!!)
            } catch (e: Exception) {
                Result.Error(e.localizedMessage)
            }
        }

    override suspend fun syncContact(requestSyncContact: RequestSyncContactData): Result<ResponseSyncContactData> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                Result.Success(getDataService()?.syncContact(requestSyncContact)!!)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Error(e.localizedMessage)
            }
        }


}