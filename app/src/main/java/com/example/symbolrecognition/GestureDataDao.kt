package com.example.symbolrecognition

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface GestureDataDao
{
    @Query("SELECT * from gestureData")
    fun getAllPeople(): List<GestureData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPerson(gestureData: GestureData)
}