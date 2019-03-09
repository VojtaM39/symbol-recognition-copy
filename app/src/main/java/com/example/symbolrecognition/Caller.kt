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
import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat.startActivity
import android.widget.Toast

class Caller {
    private val context : Context
    private val CONTACTS_REQUEST_CODE = 101
    private var number : String = ""
    private var name : String = ""
    constructor(context: Context) {
        this.context = context
    }
    /**
     * Zdroj: https://stackoverflow.com/questions/31447365/getting-contactdetails-from-contact-id-not-working-in-android
     */
    /**
     * param returnValue: "number", "name"
     */
    fun getContactDetails(contactId: Int, returnValue : String) : String{
        var result = ""
        Log.d("Details", "---")
        Log.d("Details", "Contact : $contactId")
        val phoneCursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            Data.CONTACT_ID + "=?",
            arrayOf(contactId.toString()), null
        )

        try {
            val idxName = phoneCursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            )
            val idxPhone = phoneCursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )

            while (phoneCursor.moveToNext()) {
                if(returnValue == "name")
                    result = phoneCursor.getString(idxName)
                else
                    result =  phoneCursor.getString(idxPhone)
            }
        } finally {
            phoneCursor.close()
        }
        return result
}

    /**
     * Zdroj: https://pranaybhalerao.wordpress.com/2018/02/11/run-time-permission-in-androidkotlin/
     */
    public fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(context,
            Manifest.permission.READ_CONTACTS)
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

    private fun getOneContact(contactId: Int) : String {
        return getContactDetails(contactId, "number")
    }

    public fun getContactName(contactId : Int) : String {
        return getContactDetails(contactId, "name")
    }
    private fun logContact() {
        Log.i("Name:", this.name)
        Log.i("Phone number:", this.number)

    }

    private fun call() {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$number")
        context.startActivity(intent)
    }
    public fun run() {
        logContact()
        call()
    }
    public fun getName() : String {
        return this.name
    }

    /**
     * https://stackoverflow.com/questions/9644704/how-to-get-specific-contact-number-by-using-contact-id
     */
    fun getNameByContactId(contactId: String) : String{



        val cContactIdString = ContactsContract.Contacts._ID
        val cCONTACT_CONTENT_URI = ContactsContract.Contacts.CONTENT_URI
        val cDisplayNameColumn = ContactsContract.Contacts.DISPLAY_NAME

        val selection = "$cContactIdString = ? "
        val selectionArgs = arrayOf(contactId)

        val cursor = context.contentResolver.query(cCONTACT_CONTENT_URI, null, selection, selectionArgs, null)
        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            while (cursor != null && cursor.isAfterLast === false) {
                if (cursor.getColumnIndex(cContactIdString) >= 0) {
                    if (contactId == cursor.getString(cursor.getColumnIndex(cContactIdString))) {
                        val name = cursor.getString(cursor.getColumnIndex(cDisplayNameColumn))
                        break
                    }
                }
                cursor.moveToNext()
            }
        }
        if (cursor != null)
            cursor.close()

        return name
    }

    public fun getContactIdByGestureId(gestureId : Long) : Int {
        val dbManager = DbManager(context)
        var result = 0
        val cursor = dbManager.queryOneWithWhere(Constants.GESTURES_TABLE, Constants.GESTURES_CONTACT_ID + " = " + gestureId)
        if (cursor != null) {
            cursor.moveToFirst()
            while (cursor.moveToNext()) {
                for(index in Constants.GESTURES_COLUMNS) {
                    result = cursor.getString(cursor.getColumnIndex(index)).toInt()
                    Log.i("Contact_Id", result.toString())
                }
            }
        }
        cursor.close()
        return result
    }
}