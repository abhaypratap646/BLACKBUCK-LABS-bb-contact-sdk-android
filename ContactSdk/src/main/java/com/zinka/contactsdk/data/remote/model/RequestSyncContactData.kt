package com.zinka.contactsdk.data.remote.model

import com.google.gson.annotations.SerializedName

internal data class RequestSyncContactData(
    @SerializedName("contacts")
    val contact: List<RequestSyncContactDataContact> = arrayListOf(),
    )
