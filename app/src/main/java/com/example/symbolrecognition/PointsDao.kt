package com.example.symbolrecognition

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface PointsDao {
    @Query("SELECT * FROM points WHERE gesture_id = :gestureId")
    fun getPointsOfGesture(gestureId : Long): List<Point>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGesturePoints(points : MutableList<Point>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGesturePoint(pointsData : Point)
}