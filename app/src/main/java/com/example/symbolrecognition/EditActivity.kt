package com.example.symbolrecognition

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.SimpleCursorAdapter

class EditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        /*
        val fromColumns = arrayOf(ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER)
        val toViews = intArrayOf(R.id.display_name, R.id.phone_number)

        val adapter = SimpleCursorAdapter(this, R.layout.activity_edit, cursor, fromColumns, toViews, 0)
        val listView = getListView()
        listView.adapter = adapter*/
    }
}
