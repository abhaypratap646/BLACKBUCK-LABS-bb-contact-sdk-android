package com.zinka.contactsdk.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "address_info")

class AddressInfo(
    @ColumnInfo(name = "entity") var entity: String? = null,
    @ColumnInfo(name = "tag") var tag: String? = null,
)