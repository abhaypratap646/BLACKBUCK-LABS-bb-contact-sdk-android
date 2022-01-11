package com.zinka.contactsdk.data.local

import com.zinka.contactsdk.data.local.entities.ContactInfo
import com.zinka.contactsdk.data.remote.model.Contact

/**
 * Created by abhayPratap on 04,January,2022
 */
internal interface ILocalDataSource  {

    fun insertContactInfo(contactInfo: List<Contact>)
    fun deleteAllContactInfo()
    fun updateSyncedContactInfo(
        contactId: String,
        syncedStatus: Boolean
    )

    fun getAllContactInfo(isSynced: Boolean): List<ContactInfo>?

    fun updateContactSyncStatus(isSynced: Boolean)

}