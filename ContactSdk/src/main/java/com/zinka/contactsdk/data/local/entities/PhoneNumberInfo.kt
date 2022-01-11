package com.zinka.contactsdk.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.TypeConverters
import com.zinka.contactsdk.data.local.typeconvetors.PhoneTypeConverter

@Entity(tableName = "phone_number_info")
class PhoneNumberInfo(
    @ColumnInfo(name = "entity") var entity: String? = null,
    @ColumnInfo(name = "tag") var tag: String? = null,
)