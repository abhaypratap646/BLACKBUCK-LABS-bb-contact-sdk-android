package com.zinka.contactsdk.data.local.typeconvetors

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zinka.contactsdk.data.local.entities.AddressInfo
import com.zinka.contactsdk.data.local.entities.SignificantDateInfo
import java.util.*
import kotlin.collections.ArrayList

class SignificantTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun stringToList(data: String?): ArrayList<SignificantDateInfo> {
        if (data == null) {
            return ArrayList()
        }

        val listType = object : TypeToken<ArrayList<SignificantDateInfo>>() {

        }.type

        return gson.fromJson<ArrayList<SignificantDateInfo>>(data, listType)
    }

    @TypeConverter
    fun listToString(someObjects: ArrayList<SignificantDateInfo>): String {
        return gson.toJson(someObjects)
    }

}