package com.example.symbolrecognition

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText

class AddActivity : AppCompatActivity() {

    private var db: GestureDataBase? = null
    private var gestureDataDao: GestureDataDao? = null
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
        db = GestureDataBase.getInstance(context = this)
        gestureDataDao = db?.gestureDataDao()

        var record = GestureData(name = nameTextStr, phoneNumber = phoneNumberStr)
        gestureDataDao?.insertPerson(record)
    }
}
