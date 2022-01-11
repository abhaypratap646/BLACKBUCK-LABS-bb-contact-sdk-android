package com.zinka.contactsdk.data.local

import android.content.Context
import com.zinka.contactsdk.data.local.entities.*
import com.zinka.contactsdk.data.remote.model.*
import java.util.*


/**
 * Created by abhayPratap on 04,January,2022
 */
internal class LocalDataSource private constructor(context: Context) : ILocalDataSource {

    private var dataBase: ContactSyncDatabase? = null

    init {
        dataBase = ContactSyncDatabase.getDatabase(context)
    }

    companion object {
        @Volatile
        private var INSTANCE: LocalDataSource? = null

        fun getInstance(context: Context): LocalDataSource {
            return INSTANCE ?: synchronized(this) {
                val instance = LocalDataSource(context)
                INSTANCE = instance
                instance
            }
        }
    }

    override fun insertContactInfo(contactInfo: List<Contact>) {
        val contactData = arrayListOf<ContactInfo>()
        contactInfo.forEach { contact ->
            contactData.add(
                ContactInfo(
                    contact.id,
                    contact.name,
                    contact.lastUpdatedTimeStamp,
                    contact.extraNameDetails.firstName,
                    contact.extraNameDetails.middleName,
                    contact.extraNameDetails.lastName,
                    contact.extraNameDetails.phoneticFirstName,
                    contact.extraNameDetails.phoneticMiddleName,
                    contact.extraNameDetails.phoneticLastName,
                    contact.extraNameDetails.nickname,
                    contact.website,
                    getPhoneInfo(contact.phone),
                    getEmailInfo(contact.emails),
                    getAddressInfo(contact.address),
                    getSignificantDte(contact.dates),
                    contact.workDetail.company,
                    contact.workDetail.title,
                    1,
                    contact.workDetail.department,
                    contact.user_id,
                    UUID.randomUUID().toString(),
                    contact.tenant,
                    false

                )
            )
        }
        dataBase?.contactSyncDao()?.insert(contactData)
    }

    private fun getSignificantDte(dates: ArrayList<SpecificDateDetails>): ArrayList<SignificantDateInfo> {
        val dateInfo = arrayListOf<SignificantDateInfo>()
        dates.forEach { dateDetails ->
            dateInfo.add(
                SignificantDateInfo(
                    dateDetails.date, dateDetails.type
                )
            )

        }
        return dateInfo
    }

    private fun getAddressInfo(address: ArrayList<AddressDetails>): ArrayList<AddressInfo> {
        val addressInfo = arrayListOf<AddressInfo>()
        address.forEach { addressDetails ->
            addressInfo.add(
                AddressInfo(
                    addressDetails.entity, addressDetails.tag
                )
            )

        }
        return addressInfo

    }

    private fun getEmailInfo(emails: ArrayList<EmailIdDetails>): ArrayList<EmailIdInfo> {
        val emailInfo = arrayListOf<EmailIdInfo>()
        emails.forEach { emailDetails ->
            emailInfo.add(
                EmailIdInfo(
                    emailDetails.email, emailDetails.emailType
                )
            )
        }
        return emailInfo

    }

    private fun getPhoneInfo(phone: ArrayList<PhoneNumberDetail>): ArrayList<PhoneNumberInfo> {
        val phoneNumberInfo = arrayListOf<PhoneNumberInfo>()
        phone.forEach { phoneNumberDetail ->
            phoneNumberInfo.add(
                PhoneNumberInfo(
                    phoneNumberDetail.phoneNumber, phoneNumberDetail.phoneType
                )
            )
        }
        return phoneNumberInfo

    }

    override fun deleteAllContactInfo() {
        dataBase?.contactSyncDao()?.deleteAll()
    }

    override fun updateSyncedContactInfo(contactId: String, syncedStatus: Boolean) {
        dataBase?.contactSyncDao()?.updateContactSyncStatus(contactId, syncedStatus)
    }

    override fun getAllContactInfo(isSynced: Boolean): List<ContactInfo>? {
        return dataBase?.contactSyncDao()?.getAllContacts(isSynced)
    }

    override fun updateContactSyncStatus(isSynced: Boolean) {
        dataBase?.contactSyncDao()?.updateContactForSyncToServer(isSynced)
    }
}