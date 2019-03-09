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


        val myPhoneUri = Uri.withAppendedPath(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, contactId.toString())
        val phoneCursor = context.contentResolver.query(
            myPhoneUri, null, null, null, null)
        phoneCursor.moveToFirst()
        while (!phoneCursor.isAfterLast())
        {
            if(returnValue == "name") {
                return phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            }
            else {
                return phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
            }
            phoneCursor.moveToNext()
        }
        return ""

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
        val cursor = dbManager.queryWithWhere(Constants.GESTURES_TABLE, Constants.GESTURES_ID + " = " + gestureId)
        if (cursor != null) {
            cursor.moveToFirst()
                    result = cursor.getString(cursor.getColumnIndex(Constants.GESTURES_CONTACT_ID)).toInt()
                    Log.i("Contact_Id", result.toString())
        }
        cursor.close()
        return result
    }
}