package com.example.symbolrecognition
/**
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.widget.Toast
class DatabaseHelper(context: Context) : SQLiteDatabase{
    override fun onCreate(p0: SQLiteDatabase?) {
        val query = "CREATE TABLE USER (userID TEXT,userName TEXT,userAge INTEGER)"
        p0!!.execSQL(query)
        Log.v("@@@WWE", " Table Created Sucessfully")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0!!.execSQL("DROP TABLE IF EXIST USER")
        onCreate(p0)
    }

    companion object {
        private val DATABASENAME = "test"
        private val DATABASEVERSION = 1
    }

    fun populatePerson(users: Users) {
        val db = this.writableDatabase
        var values = ContentValues()
        values.put("userID", users.userID)
        values.put("userName", users.userName)
        values.put("userAge", users.userAge)
        db.insert("USER", null, values)
        db.close()
        Log.v("@@@WWe ", " Record Inserted Sucessfully")
    }

    fun getUsers(): List<Users> {
        val db = this.writableDatabase
        val list = ArrayList<Users>()
        val cusrsor: Cursor
        cusrsor = db.rawQuery("SELECT * FROM USER", null)
        if (cusrsor != null) {
            if (cusrsor.count > 0) {
                cusrsor.moveToFirst()
                do {
                    val userID = cusrsor.getInt(cusrsor.getColumnIndex("userID"))
                    val userName = cusrsor.getString(cusrsor.getColumnIndex("userName"))
                    val userAge = cusrsor.getInt(cusrsor.getColumnIndex("userAge"))
                    val user = Users(userID, userName, userAge)
                    list.add(user)
                } while (cusrsor.moveToNext())
            }
        }
        return list
    }

    fun updateUser(users: Users) {
        val db = this.writableDatabase
        var values = ContentValues()
        values.put("userID", users.userID)
        values.put("userName", users.userName)
        values.put("userAge", users.userAge)

        val retVal = db.update("USER", values, "userID = " + users.userID, null)
        if (retVal >= 1) {
            Log.v("@@@WWe", " Record updated")
        } else {
            Log.v("@@@WWe", " Not updated")
        }
        db.close()

    }

    fun deleteUser(users: Users) {
        val db = this.writableDatabase
        var values = ContentValues()
        values.put("userID", users.userID)
        values.put("userName", users.userName)
        values.put("userAge", users.userAge)
        val retVal = db.delete("USER", "userID = " + users.userID, null)
        if (retVal >= 1) {
            Log.v("@@@WWe", " Record deleted")
        } else {
            Log.v("@@@WWe", " Not deleted")
        }
        db.close()

    }
}
        */