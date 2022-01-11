package com.zinka.contactsdk.worker

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import androidx.work.*
import com.zinka.contactsdk.ContactSdk
import com.zinka.contactsdk.data.Repository
import com.zinka.contactsdk.data.remote.model.*
import com.zinka.contactsdk.shared_preference.ContactPrefHelper
import com.zinka.contactsdk.utils.ContactUtils
import com.zinka.contactsdk.utils.Event
import kotlinx.coroutines.*
import org.json.JSONObject
import org.koin.java.KoinJavaComponent.inject

/**
 * Created by AbhayPratap on 02,January,2022
 */
internal class SyncContactWorker(
    private val context: Context,
    private val parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {

    companion object {
        const val TAG = "SyncContactWorker"
        fun startOneTimeRequest(context: Context): OneTimeWorkRequest? {
            if (ContactUtils.checkContactPermissions(context) && ContactPrefHelper.getIsUserLoggedIn(context)) {
                val oneTimeWorkRequest =
                    OneTimeWorkRequest.Builder(SyncContactWorker::class.java).build()
                WorkManager.getInstance(context)
                    .beginUniqueWork(TAG, ExistingWorkPolicy.KEEP, oneTimeWorkRequest).enqueue()
                return oneTimeWorkRequest
            }
            return null
        }
    }

    private val repository: Repository by inject(Repository::class.java)
    private var lastContactSyncTime: String = "0"
    private val timeSelectionQuery =
        ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP + " > ?"
    private var requestId = "request_id"
    private var device_id = ContactUtils.androidId(context)


    override suspend fun doWork(): Result {
        if (ContactPrefHelper.getLastSyncTime(context) == 0L) {
            logFetchContactInitiated()
            fetchContact()
        }
        return init()
    }

    private suspend fun init(): Result {
        var result: Result = Result.failure()
        CoroutineScope(Dispatchers.IO).launch {
            when (val response = repository.getLastSyncedContact(
                RequestLastSyncedTime(
                    ContactPrefHelper.getUserId(context),
                    ContactPrefHelper.getTenant(context),
                    device_id
                )
            )) {
                is com.zinka.contactsdk.data.remote.retrofit.Result.Success -> {
                    result = Result.success()

                    lastContactSyncTime =
                        if (response.data.response.contact_last_updated_timestamp != null) {
                            response.data.response.contact_last_updated_timestamp!!
                        } else {
                            System.currentTimeMillis().toString()
                        }
                    logTimeStampFromServer(lastContactSyncTime)
                    ContactPrefHelper.setLastSyncTime(context, lastContactSyncTime.toLong())
                    if (response.data.response.contact_last_updated_timestamp != null) {
                        repository.deleteAllContactInfo()
                        fetchContact()
                        getChuckData()
                    } else {
                        getChuckData()
                    }

                }
                is com.zinka.contactsdk.data.remote.retrofit.Result.Error -> {
                    result = Result.failure()
                }
            }
        }

        return result
    }

    private suspend fun getChuckData(): Result {
        val data = repository.getAllContactInfo(false)

        return if (data.isNullOrEmpty()) {
            logContactSize(0)
            Result.success()
        } else {
            logContactSize(data.size)
            val chunkData = ContactUtils.chunkArrayList(data, 50, device_id)
            sendDataToServer(chunkData, chunkData.size, 0)
        }

    }

    private suspend fun sendDataToServer(
        data: ArrayList<List<RequestSyncContactDataContact>>,
        size: Int,
        position: Int
    ): Result {
        return syncContact(data, data[position], size, position)
    }

    private suspend fun syncContact(
        data: ArrayList<List<RequestSyncContactDataContact>>,
        requestSyncContactData: List<RequestSyncContactDataContact>,
        size: Int,
        position: Int
    ): Result {
        var result: Result = Result.failure()
        CoroutineScope(Dispatchers.IO).launch {
            when (repository.syncContact(RequestSyncContactData(requestSyncContactData))) {
                is com.zinka.contactsdk.data.remote.retrofit.Result.Success -> {
                    result = Result.success()
                    val sizeLeft = size - 1
                    if (sizeLeft == 0) {
                        Result.success()
                        repository.updateContactSyncStatus(true)
                        requestId = "requestId"
                    } else {
                        syncContact(data, data[position], sizeLeft, position + 1)
                    }
                }
                is com.zinka.contactsdk.data.remote.retrofit.Result.Error -> {
                    result = Result.failure()

                }

            }
        }
        return result
    }

    private suspend fun fetchContact() {
        withContext(Dispatchers.IO) {
            val contactsListAsync = async { getPhoneContacts(lastContactSyncTime) }
            val contactNumbersAsync = async { getContactNumbers(lastContactSyncTime) }
            val contactEmailAsync = async { getContactEmails(lastContactSyncTime) }
            val contactAddressAsync = async { getContactAddress(lastContactSyncTime) }
            val contactContactSpecificDatesAsync =
                async { getContactSpecificDates(lastContactSyncTime) }
            val contactWorkDetailAsync = async { getContactWorkDetails(lastContactSyncTime) }
            val contactWebsiteAsync = async { getContactWebsite(lastContactSyncTime) }
            val contactExtraNameDetailsAsync = async { getExtraNameDetails(lastContactSyncTime) }
            //    val getContactTestAsync = async { getContactTest(lastContactSyncTime) }

            val contacts: List<Contact> = contactsListAsync.await()
            val contactNumbers = contactNumbersAsync.await()
            val contactEmails = contactEmailAsync.await()
            val contactAddress = contactAddressAsync.await()
            val contactContactSpecificDates = contactContactSpecificDatesAsync.await()
            val contactWorkDetails = contactWorkDetailAsync.await()
            val contactWebsite = contactWebsiteAsync.await()
            val contactExtraNameDetails = contactExtraNameDetailsAsync.await()
            // val getTestData = getContactTestAsync.await()


            contacts.forEach {
                contactNumbers[it.id]?.let { numbers ->
                    it.phone = numbers
                }
                contactEmails[it.id]?.let { emails ->
                    it.emails = emails
                }
                contactAddress[it.id]?.let { address ->
                    it.address = address
                }
                contactContactSpecificDates[it.id]?.let { date ->
                    it.dates = date
                }
                contactWorkDetails[it.id]?.let { workDetails ->
                    it.workDetail = workDetails
                }

                contactWebsite[it.id]?.let { website ->
                    it.website = website
                }
                contactExtraNameDetails[it.id]?.let { extraName ->
                    it.extraNameDetails = extraName
                }
                /*  getTestData[it.id]?.let { test ->
                      it.rowVersionTest = test

                  }*/

            }
            repository.insertContactInfo(contacts)
        }


    }

    private suspend fun getPhoneContacts(contactLastUpdatedTime: String): ArrayList<Contact> {
        val contactsList = ArrayList<Contact>()
        val contactsCursor = context.contentResolver?.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            timeSelectionQuery,
            arrayOf(contactLastUpdatedTime),
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        if (contactsCursor != null && contactsCursor.count > 0) {
            val idIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val lastUpdatedTimeStampIndex =
                contactsCursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP)
            while (contactsCursor.moveToNext()) {
                val id = contactsCursor.getString(idIndex)
                val name = contactsCursor.getString(nameIndex)
                val lastUpdatedTimeStamp =
                    contactsCursor.getString(lastUpdatedTimeStampIndex)

                if (name != null) {
                    contactsList.add(
                        Contact(
                            id,
                            name,
                            lastUpdatedTimeStamp,
                            ContactPrefHelper.getUserId(context),
                            ContactPrefHelper.getTenant(context)
                        )
                    )
                }

            }
            contactsCursor.close()

        }
        return contactsList
    }


    private suspend fun getExtraNameDetails(contactLastUpdatedTime: String): HashMap<String, ExtraNameDetails> {
        val contactsExtraNameMap = HashMap<String, ExtraNameDetails>()
        val whereName =
            ContactsContract.Data.MIMETYPE + " = ?"
        val whereNameParams =
            arrayOf(
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
            )
        val extraCur = context.contentResolver?.query(
            ContactsContract.Data.CONTENT_URI,
            null,
            whereName,
            whereNameParams,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        if (extraCur != null && extraCur.count > 0) {
            val contactIdIndex =
                extraCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID)

            val familyNameIndex =
                extraCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)
            val givenNameIndex =
                extraCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME)
            val middleNameIndex =
                extraCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME)
            val phoneticFamilyNameIndex =
                extraCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME)
            val phoneticGivenNameIndex =
                extraCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME)
            val phoneticMiddleNameIndex =
                extraCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_MIDDLE_NAME)
            val phoneticNameIndex =
                extraCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_NAME)

            while (extraCur.moveToNext()) {
                val id = extraCur.getString(contactIdIndex)
                val firstName = extraCur.getString(givenNameIndex)
                val lastName = extraCur.getString(familyNameIndex)
                val middleName = extraCur.getString(middleNameIndex)
                val phoneticFirstName = extraCur.getString(phoneticGivenNameIndex)
                val phoneticMiddleName = extraCur.getString(phoneticMiddleNameIndex)
                val phoneticLastName = extraCur.getString(phoneticFamilyNameIndex)
                val phoneticName = extraCur.getString(phoneticNameIndex)

                contactsExtraNameMap[id] = ExtraNameDetails(
                    firstName,
                    lastName,
                    phoneticFirstName,
                    phoneticMiddleName,
                    phoneticLastName,
                    phoneticName,
                    middleName
                )
            }

            extraCur.close()
        }
        return contactsExtraNameMap
    }


    private suspend fun getContactNumbers(contactLastUpdatedTime: String): HashMap<String, ArrayList<PhoneNumberDetail>> {
        val contactsNumberMap = HashMap<String, ArrayList<PhoneNumberDetail>>()
        val phoneCursor: Cursor? = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            timeSelectionQuery,
            arrayOf(contactLastUpdatedTime),
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        if (phoneCursor != null && phoneCursor.count > 0) {
            val contactIdIndex =
                phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val numberIndex =
                phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)


            while (phoneCursor.moveToNext()) {
                val type = ContactsContract.CommonDataKinds.Phone.getTypeLabel(
                    context.resources,
                    phoneCursor.getInt(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE)),
                    ""
                ) as String
                val contactId = phoneCursor.getString(contactIdIndex)
                val number: String =
                    phoneCursor.getString(numberIndex).replace("+91", "").replace("-", "")
                        .removePrefix("0").replace(" ", "")
                if (contactsNumberMap.containsKey(contactId) && !contactsNumberMap[contactId]!!.contains(
                        PhoneNumberDetail(type, number)
                    )
                ) {
                    contactsNumberMap[contactId]?.add(PhoneNumberDetail(type, number))
                } else if (!contactsNumberMap.containsKey(contactId)) {
                    contactsNumberMap[contactId] =
                        arrayListOf(PhoneNumberDetail(type, number))
                }
            }

            phoneCursor.close()
        }
        return contactsNumberMap
    }


    private suspend fun getContactEmails(contactLastUpdatedTime: String): HashMap<String, ArrayList<EmailIdDetails>> {
        val contactsEmailMap = HashMap<String, ArrayList<EmailIdDetails>>()
        var lastEmailId = ""
        val emailCursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            null,
            timeSelectionQuery, arrayOf(contactLastUpdatedTime),
            null
        )
        if (emailCursor != null && emailCursor.count > 0) {
            val contactIdIndex =
                emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID)
            val emailIndex =
                emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
            while (emailCursor.moveToNext()) {
                val emailType = ContactsContract.CommonDataKinds.Phone.getTypeLabel(
                    context.resources,
                    emailCursor.getInt(emailCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.TYPE)),
                    ""
                ) as String
                val contactId = emailCursor.getString(contactIdIndex)
                val email = emailCursor.getString(emailIndex)
                //check if the map contains key or not, if not then create a new array list with email
                if (contactsEmailMap.containsKey(contactId) && email != lastEmailId) {
                    contactsEmailMap[contactId]?.add(EmailIdDetails(emailType, email))
                    lastEmailId = email
                } else {
                    contactsEmailMap[contactId] = arrayListOf(EmailIdDetails(emailType, email))
                    lastEmailId = email
                }
            }
            emailCursor.close()
        }
        return contactsEmailMap
    }

    private suspend fun getContactAddress(contactLastUpdatedTime: String): HashMap<String, ArrayList<AddressDetails>> {
        val contactsAddressMap = HashMap<String, ArrayList<AddressDetails>>()
        val addressCursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
            null,
            null, null,
            null
        )
        if (addressCursor != null && addressCursor.count > 0) {
            val contactIdIndex =
                addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID)
            val streetIndex =
                addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET)
            val cityIndex =
                addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY)

            val stateIndex =
                addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION)
            val postalCodeIndex =
                addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE)
            val countryIndex =
                addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY)

            while (addressCursor.moveToNext()) {
                val addressType = ContactsContract.CommonDataKinds.Phone.getTypeLabel(
                    context.resources,
                    addressCursor.getInt(addressCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredPostal.TYPE)),
                    ""
                ) as String
                val contactId = addressCursor.getString(contactIdIndex)
                val street = addressCursor.getString(streetIndex)
                val city = addressCursor.getString(cityIndex)
                val state = addressCursor.getString(stateIndex)
                val postalCode = addressCursor.getString(postalCodeIndex)
                val country = addressCursor.getString(countryIndex)
                //check if the map contains key or not, if not then create a new array list with email
                if (contactsAddressMap.containsKey(contactId)) {
                    contactsAddressMap[contactId]?.add(
                        AddressDetails(
                            """$street$city$state$country$postalCode""",
                            addressType
                        )
                    )
                } else {
                    contactsAddressMap[contactId] = arrayListOf(
                        AddressDetails(
                            """$street$city$state$country$postalCode""",
                            addressType
                        )
                    )
                }
            }
            addressCursor.close()
        }
        return contactsAddressMap
    }


    private suspend fun getContactWorkDetails(contactLastUpdatedTime: String): HashMap<String, WorkDetails> {
        val contactsWorkDetailsMap = HashMap<String, WorkDetails>()
        val orgWhere =
            timeSelectionQuery + " AND " + ContactsContract.Data.MIMETYPE + " = ?"
        val orgWhereParams = arrayOf(
            contactLastUpdatedTime,
            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE
        )
        val workCursor = context.contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            null,
            orgWhere, orgWhereParams,
            null
        )
        if (workCursor != null && workCursor.count > 0) {
            val contactIdIndex =
                workCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)

            while (workCursor.moveToNext()) {

                val contactId = workCursor.getString(contactIdIndex)
                val companyName =
                    workCursor.getString(workCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Organization.COMPANY))
                val tile =
                    workCursor.getString(workCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Organization.TITLE))
                val department =
                    workCursor.getString(workCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Organization.DEPARTMENT))

                contactsWorkDetailsMap[contactId] =
                    WorkDetails(companyName, tile, department)


            }
            workCursor.close()
        }
        return contactsWorkDetailsMap
    }


    private suspend fun getContactSpecificDates(contactLastUpdatedTime: String): HashMap<String, ArrayList<SpecificDateDetails>> {
        val contactsSpecificDatesMap = HashMap<String, ArrayList<SpecificDateDetails>>()
        val selection = ContactsContract.Data.MIMETYPE + " = '" +
                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE + "'" + " And  " + timeSelectionQuery
        val datesCursor = context.contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            null,
            selection, arrayOf(contactLastUpdatedTime),
            null
        )
        if (datesCursor != null && datesCursor.count > 0) {
            val contactIdIndex =
                datesCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)

            while (datesCursor.moveToNext()) {
                val contactId = datesCursor.getString(contactIdIndex)
                val dataIndex =
                    datesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.DATA)
                val dateType = ContactsContract.CommonDataKinds.Event.getTypeLabel(
                    context.resources,
                    datesCursor.getInt(datesCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Event.TYPE)),
                    ""
                ) as String
                val data = datesCursor.getString(dataIndex)

                if (contactsSpecificDatesMap.containsKey(contactId)) {
                    contactsSpecificDatesMap[contactId]?.add(SpecificDateDetails(dateType, data))
                } else if (!contactsSpecificDatesMap.containsKey(contactId)) {
                    contactsSpecificDatesMap[contactId] =
                        arrayListOf(SpecificDateDetails(dateType, data))
                }


            }
            datesCursor.close()
        }
        return contactsSpecificDatesMap
    }


    private suspend fun getContactWebsite(contactLastUpdatedTime: String): HashMap<String, String> {
        val contactsWebsiteMap = HashMap<String, String>()

        val selection: String =
            ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE + "'" + " And " + timeSelectionQuery

        val websiteCursor = context.contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            null,
            selection, arrayOf(contactLastUpdatedTime),
            null
        )
        if (websiteCursor != null && websiteCursor.count > 0) {
            while (websiteCursor.moveToNext()) {
                val contactIdIndex =
                    websiteCursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)
                val websiteIndex =
                    websiteCursor.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL)

                val contactId = websiteCursor.getString(contactIdIndex)
                val website = websiteCursor.getString(websiteIndex)

                contactsWebsiteMap[contactId] = website

            }
            websiteCursor.close()
        }
        return contactsWebsiteMap
    }


    private suspend fun getContactTest(contactLastUpdatedTime: String): HashMap<String, String?> {
        val contactsTestMap = HashMap<String, String?>()
        val testCursor = context.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        if (testCursor != null && testCursor.count > 0) {
            while (testCursor.moveToNext()) {
                val contactIdIndex = testCursor.getColumnIndex(ContactsContract.Contacts._ID)
                val websiteIndex =
                    testCursor.getColumnIndex(ContactsContract.Contacts.CONTACT_STATUS_TIMESTAMP)
                val contactId = testCursor.getString(contactIdIndex)
                val website = testCursor.getString(websiteIndex)
                contactsTestMap[contactId] = website
            }
            testCursor.close()
        }
        return contactsTestMap
    }

    /*  private fun enumerateColumns() {
          val cursor = applicationContext.contentResolver.query(
              ContactsContract.Contacts.CONTENT_URI,
              null, null, null, null
          )
          if (cursor != null && cursor.moveToFirst()) {
              do {
                  for (i in 0 until cursor.columnCount) {
                      Log.d(TAG, cursor.getColumnName(i) + " : " + cursor.getString(i))
                  }
              } while (cursor.moveToNext())
              cursor.close()
          }
      }*/

    private fun logFetchContactInitiated() {
        val body = JSONObject()
        ContactSdk.logEventListener.invoke(Event.EventType.EVENT_FETCH_CONTACT_INITIATED, body)
    }

    private fun logTimeStampFromServer(timeStamp: String) {
        val body = JSONObject()
        try {
            body.put(Event.EventDataField.LAST_SYNC_TIMESTAMP, timeStamp)
        } catch (e1: Exception) {
            e1.printStackTrace()
        }
        ContactSdk.logEventListener.invoke(Event.EventType.EVENT_FETCH_CONTACT_INITIATED, body)
    }

    private fun logContactSize(size: Int) {
        val body = JSONObject()
        try {
            body.put(Event.EventDataField.CONTACT_SYNC_SIZE, size)
        } catch (e1: Exception) {
            e1.printStackTrace()
        }
        ContactSdk.logEventListener.invoke(Event.EventType.EVENT_CONTACT_SYNC_SIZE, body)
    }


}