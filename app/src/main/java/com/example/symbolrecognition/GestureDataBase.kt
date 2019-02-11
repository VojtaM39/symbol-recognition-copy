package com.example.symbolrecognition

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = arrayOf(GestureData::class), version = 1)
abstract class GestureDataBase : RoomDatabase()
{
    abstract fun gestureDataDao() : GestureDataDao

    companion object {
        private var instance: GestureDataBase? = null

        fun getInstance(context: Context): GestureDataBase? {
            if (instance == null) {
                synchronized(GestureDataBase::class)
                {
                    instance = Room.databaseBuilder(context.getApplicationContext(), GestureDataBase::class.java,"gesture.db").build()
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