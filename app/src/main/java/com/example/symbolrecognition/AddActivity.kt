package com.example.symbolrecognition

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText

class AddActivity : AppCompatActivity() {

    private var db: AppDatabase? = null
    private var contactsDao: ContactsDao? = null
    private lateinit var nameTextStr: String
    private lateinit var phoneNumberStr: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        var nameTextET: EditText = findViewById(R.id.nameText)
        nameTextStr = nameTextET.toString()
        var phoneNumberET: EditText = findViewById(R.id.phoneNumberText)
        phoneNumberStr = phoneNumberET.toString()
    }

    fun addRecord(view: View)
    {
        db = AppDatabase.getInstance(context = this)
        contactsDao = db?.contactsDao()

        var record = Contact(name = nameTextStr, phoneNumber = phoneNumberStr)
        contactsDao?.insertPerson(record)


    }
}
