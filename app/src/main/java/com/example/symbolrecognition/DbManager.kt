package com.example.symbolrecognition

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class DbManager
{
    private val dbName = "JSAContacts"
    private val dbVersion = 6

    private val dbTableContacts = Constants.CONTACTS_TABLE
    private val colContactsId = Constants.CONTACTS_ID
    private val colContactsName = Constants.CONTACTS_NAME
    private val colContactsPhoneNumber = Constants.CONTACTS_PHONE_NUMBER

    private val dbTableGestures = Constants.GESTURES_TABLE
    private val colGesturesId = Constants.GESTURES_ID
    private val colGesturesContactId = Constants.GESTURES_CONTACT_ID


    private val dbTablePoints = Constants.POINTS_TABLE
    private val colPointsId = Constants.POINTS_ID
    private val colPointsGestureId = Constants.POINTS_GESTURE_ID
    private val colPointsMoveNumber = Constants.POINTS_MOVE_NUMBER
    private val colPointsPointX = Constants.POINTS_X
    private val colPointsPointY = Constants.POINTS_Y

    private val dbTableRatios = Constants.RATIOS_TABLE
    private val colRatiosId = Constants.RATIOS_ID
    private val colRatiosGestureId = Constants.RATIOS_GESTURE_ID
    private val colRatiosXRatio = Constants.RATIOS_X
    private val colRatiosYRatio = Constants.RATIOS_Y

    private val dbTableLines = Constants.LINES_TABLE
    private val colLinesId = Constants.LINES_ID
    private val colLinesGestureId = Constants.LINES_GESTURE_ID
    private val colLinesX1 = Constants.LINES_X1
    private val colLinesY1 = Constants.LINES_Y1
    private val colLinesX2 = Constants.LINES_X2
    private val colLinesY2 = Constants.LINES_Y2

    private val CREATE_TABLE_CONTACTS_SQL = "CREATE TABLE IF NOT EXISTS " + dbTableContacts + " (" + colContactsId + " INTEGER PRIMARY KEY," + colContactsName + " TEXT, " + colContactsPhoneNumber + " TEXT);"
    private val CREATE_TABLE_GESTURES_SQL = "CREATE TABLE IF NOT EXISTS " + dbTableGestures + " (" + colGesturesId + " INTEGER PRIMARY KEY," + colGesturesContactId + " INT);"
    private val CREATE_TABLE_POINTS_SQL = "CREATE TABLE IF NOT EXISTS " + dbTablePoints + " (" + colPointsId + " INTEGER PRIMARY KEY," + colPointsGestureId + " INT, " + colPointsMoveNumber + " INT, " + colPointsPointX + " INT, " + colPointsPointY + " INT);"
    private val CREATE_TABLE_RATIOS_SQL = "CREATE TABLE IF NOT EXISTS " + dbTableRatios + " (" + colRatiosId + " INTEGER PRIMARY KEY," + colRatiosGestureId + " INT, " + colRatiosXRatio + " FLOAT, " + colRatiosYRatio + " FLOAT);"
    private val CREATE_TABLE_LINES_SQL = "CREATE TABLE IF NOT EXISTS " + dbTableLines + " (" + colLinesId + " INTEGER PRIMARY KEY," + colLinesGestureId + " INT, " + colLinesX1 + " INT, " + colLinesY1 + " INT, " + colLinesX2 + " INT, " + colLinesY2 + " INT);"

    private var db: SQLiteDatabase? = null

    constructor(context: Context) {
        var dbHelper = DatabaseHelper(context)
        db = dbHelper.writableDatabase
    }

    fun insert(values: ContentValues, table : String): Long {

        val ID = db!!.insert(table, "", values)
        return ID
    }

    fun queryAll(table : String): Cursor {

        return db!!.rawQuery("select * from " + table, null)
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
            db!!.execSQL(CREATE_TABLE_GESTURES_SQL)
            db!!.execSQL(CREATE_TABLE_POINTS_SQL)
            db!!.execSQL(CREATE_TABLE_RATIOS_SQL)
            db!!.execSQL(CREATE_TABLE_LINES_SQL)

            Toast.makeText(this.context, " database is created", Toast.LENGTH_LONG).show()
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("Drop table IF EXISTS " + dbTableContacts)
            db!!.execSQL("Drop table IF EXISTS " + dbTableGestures)
            db!!.execSQL("Drop table IF EXISTS " + dbTablePoints)
            db!!.execSQL("Drop table IF EXISTS " + dbTableRatios)
            db!!.execSQL("Drop table IF EXISTS " + dbTableLines)
        }
    }
}