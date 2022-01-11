package com.zinka.contactsdk.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zinka.contactsdk.data.local.entities.ContactInfo

/**
 * Created by abhayPratap on 04,January,2022
 */
@Dao
internal interface ContactSyncDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(contactSyncData: List<ContactInfo>)

    @Query("DELETE FROM contact_info")
    fun deleteAll()

    @Query("DELETE FROM contact_info where id= :id")
    fun deleteById(id: Int)

    @Query("SELECT * from contact_info where isSync = :isSynced")
    fun getAllContacts(isSynced: Boolean): List<ContactInfo>?

    @Query("UPDATE contact_info SET isSync = :isSynced WHERE id = :contactId")
    fun updateContactSyncStatus(contactId: String, isSynced: Boolean)

    @Query("UPDATE contact_info SET isSync =:isSynced")
    fun updateContactForSyncToServer(isSynced: Boolean)
}