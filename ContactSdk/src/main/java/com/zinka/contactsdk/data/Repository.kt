package com.zinka.contactsdk.data

import com.zinka.contactsdk.data.local.ILocalDataSource
import com.zinka.contactsdk.data.local.LocalDataSource
import com.zinka.contactsdk.data.local.entities.ContactInfo
import com.zinka.contactsdk.data.remote.IRemoteDataSource
import com.zinka.contactsdk.data.remote.RemoteDataSource
import com.zinka.contactsdk.data.remote.model.*
import com.zinka.contactsdk.data.remote.retrofit.Result


/**
 * Created by abhayPratap on 01,January,2022
 */
internal class Repository(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : IRemoteDataSource, ILocalDataSource {


    override suspend fun getLastSyncedContact(requestLastSyncedTime: RequestLastSyncedTime): Result<ResponseLastSyncedTime> {
        return remoteDataSource.getLastSyncedContact(requestLastSyncedTime)
    }

    override suspend fun syncContact(requestSyncContact: RequestSyncContactData): Result<ResponseSyncContactData> {
        return remoteDataSource.syncContact(requestSyncContact)
    }


    override fun insertContactInfo(contactInfo: List<Contact>) {
        localDataSource.insertContactInfo(contactInfo)
    }

    override fun deleteAllContactInfo() {
        localDataSource.deleteAllContactInfo()
    }

    override fun updateSyncedContactInfo(contactId: String, syncedStatus: Boolean) {
        localDataSource.updateSyncedContactInfo(contactId, syncedStatus)
    }

    override fun getAllContactInfo(isSynced: Boolean): List<ContactInfo>? {
        return localDataSource.getAllContactInfo(isSynced)
    }

    override fun updateContactSyncStatus(isSynced: Boolean) {
        localDataSource.updateContactSyncStatus(isSynced)
    }

}