package com.example.symbolrecognition

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast

class EditActivity : AppCompatActivity() {

    private var listView: ListView? = null
    private var contactList: List<Contact>
    private var adapter: ContactsListViewAdapter? = null
    private var db: AppDatabase?
    private var contactsDao: ContactsDao?

    init {
        this.db = AppDatabase.getInstance(context = this)
        this.contactsDao = db?.contactsDao()
        this.contactList = contactsDao!!.getAllContacts()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        listView = findViewById<ListView>(R.id.listView)

        adapter = ContactsListViewAdapter(this, contactList)
        (listView as ListView).adapter = adapter

        //vyplnit contactList


        (listView as ListView).onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            Toast.makeText(
                applicationContext,
                contactList?.get(i)?.name,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
