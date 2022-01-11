package com.zinka.contactsdk.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.zinka.contactsdk.data.local.entities.*
import com.zinka.contactsdk.data.remote.model.*


/**
 * Created by AbhayPratap on 02,January,2022
 */

internal object ContactUtils {


    fun checkContactPermissions(context: Context): Boolean {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        )
    }

    fun chunkArrayList(
        arrayToChunk: List<ContactInfo>,
        chunkSize: Int,
        device_id: String
    ): ArrayList<List<RequestSyncContactDataContact>> {
        val chunkList: ArrayList<List<RequestSyncContactDataContact>> = ArrayList()
        val data = getChunkData(arrayToChunk, device_id)
        var guide: Int = arrayToChunk.size
        var index = 0
        var tale = chunkSize
        while (tale < arrayToChunk.size) {
            chunkList.add(data.subList(index, tale))
            guide -= chunkSize
            index += chunkSize
            tale += chunkSize
        }
        if (guide > 0) {
            chunkList.add(data.subList(index, index + guide))
        }
        return chunkList
    }

    private fun getChunkData(
        arrayToChunk: List<ContactInfo>,
        device_id: String
    ): ArrayList<RequestSyncContactDataContact> {
        val contacts: ArrayList<RequestSyncContactDataContact> = arrayListOf()
        arrayToChunk.forEach { contact ->
            contacts.add(
                RequestSyncContactDataContact(
                RequestSyncContact(
                    contact.firstName,
                    contact.lastName,
                    contact.phoneticLastName,
                    contact.phoneticMiddleName,
                    contact.phoneticLastName,
                    contact.nickname,
                    "",
                    contact.company,
                    contact.departement,
                    contact.title,
                    contact.website,
                    "",
                    contact.lastUpdatedTimeStamp.toLong(),
                    contact.raw_contact_version,
                    contact.user_id,
                    contact.tenant,
                    device_id,
                    getPhoneNumber(contact.phone),
                    getEmail(contact.email),
                    getAddress(contact.address), getSignificantDate(contact.significant_date)
                )
            ))

        }
        return contacts

    }

    private fun getSignificantDate(significantDate: ArrayList<SignificantDateInfo>): ArrayList<SpecificDateDetails> {
        val dateData: ArrayList<SpecificDateDetails> = arrayListOf()
        significantDate.forEach { dateInfo ->
            dateData.add(SpecificDateDetails(dateInfo.entity, dateInfo.tag))
        }
        return dateData
    }

    private fun getAddress(address: ArrayList<AddressInfo>): ArrayList<AddressDetails> {
        val addressData: ArrayList<AddressDetails> = arrayListOf()
        address.forEach { addressInfo ->
            addressData.add(AddressDetails(addressInfo.entity, addressInfo.tag))
        }
        return addressData
    }

    private fun getEmail(email: ArrayList<EmailIdInfo>): ArrayList<EmailIdDetails> {
        val emailData: ArrayList<EmailIdDetails> = arrayListOf()
        email.forEach { emailInfo ->
            emailData.add(EmailIdDetails(emailInfo.entity, emailInfo.tag))
        }
        return emailData
    }

    private fun getPhoneNumber(phone: ArrayList<PhoneNumberInfo>): List<PhoneNumberDetail> {
        val phoneNumberData: ArrayList<PhoneNumberDetail> = arrayListOf()
        phone.forEach { phoneNumberInfo ->
            phoneNumberData.add(PhoneNumberDetail(phoneNumberInfo.entity, phoneNumberInfo.tag))
        }
        return phoneNumberData

    }


    fun stringToJson(jsonStr: String): JsonObject? {
        val gson = Gson()
        val element: JsonElement = gson.fromJson(jsonStr, JsonElement::class.java)
        return element.asJsonObject
    }

    @SuppressLint("HardwareIds")
    fun androidId(context: Context): String {
        return Settings.Secure.getString(
            context.applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

}