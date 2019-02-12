package com.example.symbolrecognition

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = arrayOf(Contacts::class), version = 1)
abstract class AppDatabase : RoomDatabase()
{
    abstract fun contactsDao() : ContactsDao

    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (instance == null) {
                synchronized(AppDatabase::class)
                {
                    instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase::class.java,"appDatabase.db").build()
                }
            }
            return instance
        }

        fun destroyInstance()
        {
            instance = null
        }
    }
}