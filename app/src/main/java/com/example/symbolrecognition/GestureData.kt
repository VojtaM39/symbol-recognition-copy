package com.example.symbolrecognition

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contacts(@PrimaryKey(autoGenerate = true) val id: Long? = null, var name: String, var phoneNumber: String)
{
    constructor(): this(null, "", "")
}
@Entity(tableName = "points")
data class Points(@PrimaryKey(autoGenerate = true) val id: Long? = null, var gesture_id : Long?, var move_number : Long?, var point_x : Short?,var point_y : Short? )
{
    constructor(): this(null,null, null, null,null)
}

@Entity(tableName = "gestures")
data class Gestures(@PrimaryKey(autoGenerate = true) val id: Long? = null, var contact_id : Long?)
{
    constructor(): this(null,null)
}