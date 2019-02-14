package com.example.symbolrecognition

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.gesture.Gesture

@Database(entities = arrayOf(Contact::class, Point::class, Gesture::class), version = 1)
abstract class AppDatabase : RoomDatabase()
{
    abstract fun contactsDao() : ContactsDao
    abstract  fun gesturesDao() : GesturesDao
    abstract fun pointsDao() : PointsDao
    companion object {
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (instance == null) {
                synchronized(AppDatabase::class)
                {
                    instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "myDB").allowMainThreadQueries().build()
                }
            }
            return instance
        }
        /*INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "xxx")
                    // To simplify the codelab, allow queries on the main thread.
                    // Don't do this on a real app! See PersistenceBasicSample for an example.
                    .allowMainThreadQueries()
                    .build();*/

        fun destroyInstance() {
            instance = null
        }
    }
}