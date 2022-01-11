package com.zinka.contactsdk.data.remote.model

import com.google.gson.annotations.SerializedName

data class SpecificDateDetails(
    @SerializedName("tag")  val type: String?,
    @SerializedName("entity") val date: String?
)