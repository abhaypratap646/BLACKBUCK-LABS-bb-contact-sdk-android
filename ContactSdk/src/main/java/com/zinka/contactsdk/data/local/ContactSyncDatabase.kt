package com.zinka.contactsdk.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zinka.contactsdk.data.local.dao.ContactSyncDao
import com.zinka.contactsdk.data.local.entities.ContactInfo
import com.zinka.contactsdk.data.local.typeconvetors.AddressTypeConverter
import com.zinka.contactsdk.data.local.typeconvetors.EmailTypeConverter
import com.zinka.contactsdk.data.local.typeconvetors.PhoneTypeConverter
import com.zinka.contactsdk.data.local.typeconvetors.SignificantTypeConverter

/**
 * Created by abhayPratap on 01,January,2022
 */
@Database(
    entities = [ContactInfo::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    PhoneTypeConverter::class,
    EmailTypeConverter::class,
    AddressTypeConverter::class,
    SignificantTypeConverter::class
)
internal abstract class ContactSyncDatabase : RoomDatabase() {
    abstract fun contactSyncDao(): ContactSyncDao?


    companion object {
        @Volatile
        private var INSTANCE: ContactSyncDatabase? = null

        fun getDatabase(context: Context): ContactSyncDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactSyncDatabase::class.java,
                    "contact_sync_database"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}