package com.zinka.contactsdk.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "significant_date_info")

class SignificantDateInfo(
    @ColumnInfo(name = "entity") var entity: String? = null,
    @ColumnInfo(name = "tag") var tag: String? = null,
)
