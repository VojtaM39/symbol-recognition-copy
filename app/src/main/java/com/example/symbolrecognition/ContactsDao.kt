package com.example.symbolrecognition

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface ContactsDao
{
    @Query("SELECT * from contacts")
    fun getAllPeople(): List<Contact>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPerson(contacts: Contact): Long
}