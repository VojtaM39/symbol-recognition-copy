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
    private val dbTableContacts = Constants.CONTACTS_TABLE
    private val colContactsId = "Id"
    private val colContactsName = "Name"
    private val colContactsPhoneNumber = "PhoneNumber"

    private val dbTableGestures = Constants.GESTURES_TABLE
    private val colGesturesId = "Id"
    private val colGesturesContactId = "contact_id"
    private val dbVersion = 4

    private val dbTablePoints = Constants.POINTS_TABLE
    private val colPointsId = "Id"
    private val colPointsGestureId = "gesture_id"
    private val colPointsMoveNumber = "move_number"
    private val colPointsPointX = "point_x"
    private val colPointsPointY = "point_y"

    private val dbTableRatios = Constants.RATIOS_TABLE
    private val colRatiosId = "Id"
    private val colRatiosGestureId = "gesture_id"
    private val colRatiosXRatio = "x_ratio"
    private val colRatiosYRatio = "y_ratio"

    private val dbTableLines = Constants.LINES_TABLE
    private val colLinesId = "Id"
    private val colLinesGestureId = "gesture_id"
    private val colLinesX1 = "x1"
    private val colLinesY1 = "y1"
    private val colLinesX2 = "x2"
    private val colLinesY2 = "y2"

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


    fun queryAllFromContacts(): Cursor {

        return db!!.rawQuery("select * from " + dbTableContacts, null)
    }

    fun queryAll():Cursor

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