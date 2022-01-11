package com.zinka.contactsdk.data.remote.model

import com.google.gson.annotations.SerializedName


data class ResponseSyncContactData(
    @SerializedName("response") var response: ArrayList<ResponseSyncContact> = arrayListOf()
)

data class ResponseSyncContact(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("error_message") var errorMessage: String? = null,
    @SerializedName("request_id") var requestId: String? = null
)