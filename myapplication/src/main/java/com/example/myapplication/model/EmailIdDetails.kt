package com.example.myapplication.model

import com.google.gson.annotations.SerializedName

data class EmailIdDetails(
    @SerializedName("tag")
    val emailType: String?,
    @SerializedName("entity")
    val email: String?)