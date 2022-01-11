package com.zinka.contactsdk.data.remote.model

import com.google.gson.annotations.SerializedName

internal data class BaseResponse<T>(
        @SerializedName("status") var status: String? = null,
        @SerializedName("error_message") val message: String? = null,
        @SerializedName("result") val result: T? = null,
        @SerializedName("contact_last_updated_timestamp") val contact_last_updated_timestamp: T? = null
)