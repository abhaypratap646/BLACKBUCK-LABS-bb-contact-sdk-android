package com.example.myapplication.datalayer

import okhttp3.Headers
import java.util.*

internal class RequestHeader {
    val headerMap: Map<String, String>

    companion object {
        private var requestHeader: RequestHeader? = null

        @get:Synchronized
        val instance: RequestHeader?
            get() {
                if (requestHeader == null) {
                    requestHeader =
                        RequestHeader()
                }
                return requestHeader
            }

        @Synchronized
        fun newInstance(): RequestHeader? {
            requestHeader =
                RequestHeader()
            return requestHeader
        }

        fun get(): Headers.Builder {
            val builder = Headers.Builder()
            builder.add("Authorization", "Token wdoc78a36vhm2w6manwvz5b1fag8t1m8")
            builder.add("Content-Type", "application/json")
            builder.add("Accept-Language", "en")
            return builder
        }
    }

    init {
        headerMap = HashMap()
    }
}