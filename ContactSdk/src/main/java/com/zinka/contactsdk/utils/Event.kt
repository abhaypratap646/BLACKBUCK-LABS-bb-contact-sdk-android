package com.zinka.contactsdk.utils

/**
 * Created by AbhayPratap on 11,January,2022
 */
internal object Event {
    object EventDataField {
        const val SYNC_TIME = "sync_time"
        const val LAST_SYNC_TIMESTAMP="last_sync_timestamp"
        const val CONTACT_SYNC_SIZE="contact_sync_size"
    }

    object EventType {
        const val EVENT_SYNC_CONTACT_JOB_STARTED = "sync_contact_job_started"
        const val EVENT_SYNC_CONTACT_JOB_INITIATED = "sync_contact_job_initiated"
        const val EVENT_FETCH_CONTACT_INITIATED = "sync_fetch_contact_initiated"
        const val EVENT_CONTACT_SYNC_SIZE = "contact_sync_size"

    }
}