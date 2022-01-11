package com.zinka.contactsdk.data.remote.model

import com.google.gson.annotations.SerializedName

internal data class RequestSyncContactDataContact(
    @SerializedName("contact") var responseSyncContact: RequestSyncContact,
    @SerializedName("request_id" ) var requestId : String = "request_id"
)

internal data class RequestSyncContact(
    @SerializedName("first_name") var first_name: String? = null,
    @SerializedName("last_name") var last_name: String? = null,
    @SerializedName("phonetic_last_name") var phonetic_last_name: String? = null,
    @SerializedName("phonetic_middle_name") var phonetic_middle_name: String? = null,
    @SerializedName("phonetic_first_name") var phonetic_first_name: String? = null,
    @SerializedName("nickname") var nickname: String? = null,
    @SerializedName("file_as") var file_as: String? = null,
    @SerializedName("company") var company: String? = null,
    @SerializedName("department") var department: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("website") var website: String? = null,
    @SerializedName("relationship") var relationship: String? = null,
    @SerializedName("contact_last_updated_timestamp") var contact_last_updated_timestamp: Long? = null,
    @SerializedName("raw_contact_version") var raw_contact_version: Int? = null,
    @SerializedName("user_id") var user_id: String? = null,
    @SerializedName("tenant") var tenant: String? = null,
    @SerializedName("device_id") var device_id: String? = null,
    @SerializedName("phone") var phone: List<PhoneNumberDetail> = arrayListOf(),
    @SerializedName("email") var email: ArrayList<EmailIdDetails> = arrayListOf(),
    @SerializedName("address") var address: ArrayList<AddressDetails> = arrayListOf(),
    @SerializedName("significant_date") var significant_date: ArrayList<SpecificDateDetails> = arrayListOf(),
)
