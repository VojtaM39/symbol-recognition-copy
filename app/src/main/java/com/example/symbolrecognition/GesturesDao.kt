package com.example.symbolrecognition

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface GesturesDao {
    @Query("SELECT * FROM points")
    fun getAllGestures(): List<Gestures>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGesture(gesture : Gestures) : Long
}