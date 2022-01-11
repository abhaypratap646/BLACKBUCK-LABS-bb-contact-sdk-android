package com.zinka.contactsdk.observer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.ContactsContract
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.zinka.contactsdk.utils.ContactUtils


/**
 * Created by AbhayPratap on 1,January,2022
 *
 */
@Deprecated("Not Used As of now , To be used in future")
internal class ContactContentObserver(h: Handler?, val context: Context) : ContentObserver(h) {
    companion object {
        val TAG = "ContactContentObserver"
        private var contactObserver: ContactContentObserver? = null

        fun registerContactObserver(context: Context) {
            if (ContactUtils.checkContactPermissions(context)){
                contactObserver?.let { context.contentResolver.unregisterContentObserver(it) }
                contactObserver = ContactContentObserver(Handler(), context.applicationContext)
                try {
                    context.contentResolver.registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, contactObserver!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

        }
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        contactAdded(selfChange)
    }


    private fun contactAdded(selfChange: Boolean) {
        if (!selfChange) {
            try {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_CONTACTS
                    )
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    val cr = context.contentResolver
                    val cursor =
                        cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
                    if (cursor != null && cursor.count > 0) {
                        //moving cursor to last position
                        //to get last element added
                        cursor.moveToLast()
                        var contactName: String? = null
                        val photo: String? = null
                        var contactNumber: String? = null
                        val id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                        if (cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                                .toInt() > 0
                        ) {
                            val pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                arrayOf(id),
                                null
                            )
                            if (pCur != null) {
                                pCur.moveToFirst()
                                contactNumber =
                                    pCur.getString(pCur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                contactName =
                                    pCur.getString(pCur.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))

                            }
                            pCur!!.close()
                        }
                        cursor.close()
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }


}