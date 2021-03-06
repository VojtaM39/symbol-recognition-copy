package com.ovapp.symbolrecognition

import android.content.Context
import android.util.Log

class DatabaseTester {
    private val context : Context
    private val dbManager : DbManager
    constructor(context: Context) {
        this.context = context
        dbManager = DbManager(context)
    }

    private fun logAllRows(table: String, columns : List<String>) {
        Log.i("DbLog", "Log of table " + table)
        val cursor = dbManager.queryAll(table)
        if (cursor != null) {
            cursor.moveToFirst()
            while (cursor.moveToNext()) {
                var result = ""
                for(index in columns) {
                    result += cursor.getString(cursor.getColumnIndex(index)) + "| "
                }
                Log.i("Row", result)
            }
        }
        cursor.close()
    }

    public fun logAllWithWhere(table: String, columns : List<String>, where : String) {
        Log.i("DbLog", "Log of table " + table)
        val cursor = dbManager.queryWithWhere(table, where)
        if (cursor != null) {
            cursor.moveToFirst()
            do  {
                var result = ""
                for(index in columns) {
                    result += cursor.getString(cursor.getColumnIndex(index)) + "| "
                }
                Log.i("Row", result)
            }
                while (cursor.moveToNext())
        }
        cursor.close()
    }

    public fun logTest() {
        logAllWithWhere(Constants.GESTURES_TABLE,Constants.GESTURES_COLUMNS, Constants.GESTURES_ID + " = 1")
    }

    public fun logGestuesWithWhere(where : String) {
        logAllWithWhere(Constants.GESTURES_TABLE, Constants.GESTURES_COLUMNS, where)
    }

    public fun logGestures() {
        logAllRows(Constants.GESTURES_TABLE, Constants.GESTURES_COLUMNS)
    }

    public fun logPoints() {
        logAllRows(Constants.POINTS_TABLE, Constants.POINTS_COLUMNS)
    }
    public fun logLines() {
        logAllRows(Constants.LINES_TABLE, Constants.LINES_COLUMNS)
    }
    public fun logRatios() {
        logAllRows(Constants.RATIOS_TABLE, Constants.RATIOS_COLUMNS)
    }

    public fun logPredefinedPoints() {
        logAllRows(Constants.POINTS_PREDEFINED_TABLE, Constants.POINTS_COLUMNS)
    }
}