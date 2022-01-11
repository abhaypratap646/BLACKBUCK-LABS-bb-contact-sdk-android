package com.zinka.contactsdk.utils

internal object Constant {

    object Api {

        const val SYNC_CONTACT = "singular-callback/event/contacts/v1/bulk"
        const val GET_LAST_SYNC_TIME = "singular-callback/event/contacts/v1/last_sync_time"

    }

    object QueryAndPath{
        const val USER_ID="user_id"
        const val TENANT="tenant"
        const val DEVICE_ID="device_id"

    }

    const val DEFAULT_REQUEST_ID = "request_id"

}