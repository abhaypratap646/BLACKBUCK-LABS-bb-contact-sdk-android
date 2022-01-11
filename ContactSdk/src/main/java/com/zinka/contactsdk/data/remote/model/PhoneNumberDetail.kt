package com.zinka.contactsdk.data.remote.model

import com.google.gson.annotations.SerializedName

data class PhoneNumberDetail(
    @SerializedName("tag")
    val phoneType: String?,
    @SerializedName("entity")
    val phoneNumber: String?

)