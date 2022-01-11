package com.zinka.contactsdk.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity


@Entity(tableName = "email_id_info")

class EmailIdInfo(
    @ColumnInfo(name = "entity") var entity: String? = null,
    @ColumnInfo(name = "tag") var tag: String? = null,
)