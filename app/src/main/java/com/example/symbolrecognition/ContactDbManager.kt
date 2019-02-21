package com.example.symbolrecognition

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class ContactDbManager
{
    private val dbName = "JSAContacts"
    private val dbTableContacts = "Contacts"
    private val colContactsId = "Id"
    private val colContactsName = "Name"
    private val colContactsPhoneNumber = "PhoneNumber"

    private val dbTableGestures = "Gestures"
    private val colGesturesId = "Id"
    private val colGesturesContactId = "contact_id"
    private val dbVersion = 1

    private val CREATE_TABLE_CONTACTS_SQL = "CREATE TABLE IF NOT EXISTS " + dbTableContacts + " (" + colContactsId + " INTEGER PRIMARY KEY," + colContactsName + " TEXT, " + colContactsPhoneNumber + " TEXT);"

    private var db: SQLiteDatabase? = null

    constructor(context: Context) {
        var dbHelper = DatabaseHelper(context)
        db = dbHelper.writableDatabase
    }

    fun insertToContacts(values: ContentValues): Long {

        val ID = db!!.insert(dbTableContacts, "", values)
        return ID
    }

    fun queryAllFromContacts(): Cursor {

        return db!!.rawQuery("select * from " + dbTableContacts, null)
    }

    fun deleteContacts(selection: String, selectionArgs: Array<String>): Int {

        val count = db!!.delete(dbTableContacts, selection, selectionArgs)
        return count
    }

    fun updateContacts(values: ContentValues, selection: String, selectionargs: Array<String>): Int {

        val count = db!!.update(dbTableContacts, values, selection, selectionargs)
        return count
    }

    inner class DatabaseHelper : SQLiteOpenHelper {

        var context: Context? = null

        constructor(context: Context) : super(context, dbName, null, dbVersion) {
            this.context = context
        }

        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL(CREATE_TABLE_CONTACTS_SQL)
            Toast.makeText(this.context, " database is created", Toast.LENGTH_LONG).show()
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("Drop table IF EXISTS " + dbTableContacts)
        }
    }
}