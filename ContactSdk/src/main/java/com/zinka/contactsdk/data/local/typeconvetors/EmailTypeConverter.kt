package com.zinka.contactsdk.data.local.typeconvetors

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zinka.contactsdk.data.local.entities.EmailIdInfo
import com.zinka.contactsdk.data.local.entities.PhoneNumberInfo
import java.util.*
import kotlin.collections.ArrayList

class EmailTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun stringToList(data: String?): ArrayList<EmailIdInfo> {
        if (data == null) {
            return ArrayList()
        }

        val listType = object : TypeToken<ArrayList<EmailIdInfo>>() {

        }.type

        return gson.fromJson<ArrayList<EmailIdInfo>>(data, listType)
    }

    @TypeConverter
    fun listToString(someObjects: ArrayList<EmailIdInfo>): String {
        return gson.toJson(someObjects)
    }
}