package com.example.symbolrecognition

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.Toast

class EditActivity : AppCompatActivity() {

    var listView: ListView? = null
    var contactList = ArrayList<Contacts>()
    var adapter: ContactsListViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        listView = findViewById<ListView>(R.id.listView)

        adapter = ContactsListViewAdapter(this, contactList)
        (listView as ListView).adapter = adapter

        (listView as ListView).onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            Toast.makeText(
                applicationContext,
                contactList?.get(i)?.name,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
