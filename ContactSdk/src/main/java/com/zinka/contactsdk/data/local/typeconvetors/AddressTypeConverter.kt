package com.zinka.contactsdk.data.local.typeconvetors

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zinka.contactsdk.data.local.entities.AddressInfo
import com.zinka.contactsdk.data.local.entities.EmailIdInfo
import java.util.*
import kotlin.collections.ArrayList

class AddressTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun stringToList(data: String?): ArrayList<AddressInfo> {
        if (data == null) {
            return ArrayList()
        }

        val listType = object : TypeToken<ArrayList<AddressInfo>>() {

        }.type

        return gson.fromJson<ArrayList<AddressInfo>>(data, listType)
    }

    @TypeConverter
    fun listToString(someObjects: ArrayList<AddressInfo>): String {
        return gson.toJson(someObjects)
    }
}