package com.zinka.contactsdk.data.remote.model

import com.google.gson.annotations.SerializedName

internal data class ResponseLastSyncedTime(
    @SerializedName("response") var response: Response

)

internal data class Response(

    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("error_message") var errorMessage: String? = null,
    @SerializedName("request_id") var requestId: String? = null,
    @SerializedName("contact_last_updated_timestamp") var contact_last_updated_timestamp: String? = null,
)

