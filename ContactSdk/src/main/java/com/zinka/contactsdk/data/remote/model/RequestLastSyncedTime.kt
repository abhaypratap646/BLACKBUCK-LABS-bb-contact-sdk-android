package com.zinka.contactsdk.data.remote.model

import com.google.gson.annotations.SerializedName
import java.util.*

internal data class RequestLastSyncedTime(
    @SerializedName("user_id") val user_id: String?,
    @SerializedName("tenant") val tenant: String?,
    @SerializedName("device_id") val device_id: String
)