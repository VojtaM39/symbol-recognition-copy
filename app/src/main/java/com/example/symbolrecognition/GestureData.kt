package com.example.symbolrecognition

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contacts(@PrimaryKey(autoGenerate = true) val id: Long? = null, var name: String, var phoneNumber: String)
{
    constructor(): this(null, "", "")
}
