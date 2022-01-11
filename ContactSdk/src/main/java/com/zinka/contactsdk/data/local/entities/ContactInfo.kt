package com.zinka.contactsdk.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.zinka.contactsdk.data.local.typeconvetors.AddressTypeConverter
import com.zinka.contactsdk.data.local.typeconvetors.EmailTypeConverter
import com.zinka.contactsdk.data.local.typeconvetors.PhoneTypeConverter
import com.zinka.contactsdk.data.local.typeconvetors.SignificantTypeConverter
import com.zinka.contactsdk.data.remote.model.AddressDetails
import com.zinka.contactsdk.data.remote.model.EmailIdDetails
import com.zinka.contactsdk.data.remote.model.PhoneNumberDetail
import com.zinka.contactsdk.data.remote.model.SpecificDateDetails
import java.util.*
import kotlin.collections.ArrayList

@Entity(tableName = "contact_info", primaryKeys = ["id"])

class ContactInfo(
    @ColumnInfo(name = "id") var id: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "lastUpdatedTimeStamp") var lastUpdatedTimeStamp: String,
    @ColumnInfo(name = "firstName") var firstName: String? = null,
    @ColumnInfo(name = "middleName") var middleName: String? = null,
    @ColumnInfo(name = "lastName") var lastName: String? = null,
    @ColumnInfo(name = "phoneticFirstName") var phoneticFirstName: String? = null,
    @ColumnInfo(name = "phoneticMiddleName") var phoneticMiddleName: String? = null,
    @ColumnInfo(name = "phoneticLastName") var phoneticLastName: String? = null,
    @ColumnInfo(name = "nickname") var nickname: String? = null,
    @ColumnInfo(name = "website") var website: String? = null,
    @TypeConverters(PhoneTypeConverter::class)
    @ColumnInfo(name = "phone") var phone: ArrayList<PhoneNumberInfo> = arrayListOf(),
    @TypeConverters(EmailTypeConverter::class)
    @ColumnInfo(name = "email") var email: ArrayList<EmailIdInfo> = arrayListOf(),
    @TypeConverters(AddressTypeConverter::class)
    @ColumnInfo(name = "address") var address: ArrayList<AddressInfo> = arrayListOf(),
    @TypeConverters(SignificantTypeConverter::class)
    @ColumnInfo(name = "significant_date") var significant_date: ArrayList<SignificantDateInfo> = arrayListOf(),
    @ColumnInfo(name = "company") var company: String? = null,
    @ColumnInfo(name = "title") var title: String? = null,
    @ColumnInfo(name = "raw_contact_version") var raw_contact_version: Int? = null,
    @ColumnInfo(name = "department") var departement: String? = null,
    @ColumnInfo(name = "user_id") var user_id: String?,
    @ColumnInfo(name = "device_id") var device_id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "tenant") var tenant: String?,
    @ColumnInfo(name = "isSync") var isSync: Boolean = false,
)