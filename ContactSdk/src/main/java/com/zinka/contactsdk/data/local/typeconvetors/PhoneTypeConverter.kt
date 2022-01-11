package com.zinka.contactsdk.data.local.typeconvetors

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zinka.contactsdk.data.local.entities.PhoneNumberInfo
import java.util.*
import kotlin.collections.ArrayList

class PhoneTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun stringToList(data: String?): ArrayList<PhoneNumberInfo> {
        if (data == null) {
            return ArrayList()
        }

        val listType = object : TypeToken<ArrayList<PhoneNumberInfo>>() {

        }.type

        return gson.fromJson<ArrayList<PhoneNumberInfo>>(data, listType)
    }

    @TypeConverter
    fun listToString(someObjects: ArrayList<PhoneNumberInfo>): String {
        return gson.toJson(someObjects)
    }
}