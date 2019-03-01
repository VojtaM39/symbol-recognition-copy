package com.example.symbolrecognition

import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import android.provider.Telephony.Mms.Addr.CONTACT_ID
import android.util.Log
import android.provider.ContactsContract.Data
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat
import android.Manifest
import android.app.Activity
import android.widget.Toast

class Caller {
    private val contactId : Int
    private val context : Context
    private val CONTACTS_REQUEST_CODE = 101
    constructor(contactId : Int, context: Context) {
        this.contactId = contactId
        this.context = context
    }
    public fun getPhoneNumber(contactId : Int) {

    }

    /**
     * Zdroj: https://stackoverflow.com/questions/31447365/getting-contactdetails-from-contact-id-not-working-in-android
     */
    fun getContactDetails(contactId: Int) {
        Log.d("Details", "---")
        Log.d("Details", "Contact : $contactId")
        val phoneCursor = context.getContentResolver().query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI
            ),
            Data.CONTACT_ID + "=?",
            arrayOf(contactId.toString()), null
        )

        try {
            val idxAvatarUri = phoneCursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI
            )
            val idxName = phoneCursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            )
            val idxPhone = phoneCursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )

            while (phoneCursor.moveToNext()) {
                val phoneNumber = phoneCursor.getString(idxPhone)
                val name = phoneCursor.getString(idxName)
                val avatarUri = phoneCursor.getString(idxAvatarUri)

                Log.d("Details", "Phone number: $phoneNumber")
                Log.d("Details", "Name: $name")
                Log.d("Details", "Avatar URI: $avatarUri")
            }
        } finally {
            phoneCursor.close()
        }

        val emailCursor = context.getContentResolver().query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS),
            Data.CONTACT_ID + "=?",
            arrayOf(contactId.toString()), null
        )

        try {
            val idxAddress = emailCursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Email.ADDRESS
            )
            while (emailCursor.moveToNext()) {
                val address = emailCursor.getString(idxAddress)
                Log.d("Details", "Email: $address")
            }
        } finally {
            emailCursor.close()
        }
    }

    /**
     * Zdroj: https://pranaybhalerao.wordpress.com/2018/02/11/run-time-permission-in-androidkotlin/
     */
    public fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(context,
            Manifest.permission.RECORD_AUDIO)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }

    }
    private fun makeRequest() {
        ActivityCompat.requestPermissions(context as Activity,
            arrayOf(Manifest.permission.READ_CONTACTS),
            CONTACTS_REQUEST_CODE)
    }
    fun handlePermission(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            CONTACTS_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(context,"Permission Denied",Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(context,"Permission Granted",Toast.LENGTH_SHORT).show()

                }
            }
        }
    }
    public fun run() {

        getContactDetails(this.contactId)
    }
}