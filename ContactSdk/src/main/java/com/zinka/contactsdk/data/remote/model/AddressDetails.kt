package com.zinka.contactsdk.data.remote.model

import com.google.gson.annotations.SerializedName

data class AddressDetails(
    @SerializedName("entity")
    val entity: String?,

    @SerializedName("tag")
    val tag: String?
)