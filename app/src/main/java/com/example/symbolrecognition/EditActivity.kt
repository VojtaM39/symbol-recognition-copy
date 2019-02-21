package com.example.symbolrecognition

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.activity_edit.*

class EditActivity : AppCompatActivity()
{
    private var listContacts = ArrayList<Contact>()
    private var editOnClick: Boolean

    init
    {
        this.editOnClick = getExtra()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

//        listContacts.add(Contact(1, "JavaSampleApproach", "Java technology, Spring Framework - approach to Java by Sample."))
//        listContacts.add(Contact(2, "Kotlin Android Tutorial", "Create tutorial for people to learn Kotlin Android. Kotlin is now an official language on Android. It's expressive, concise, and powerful. Best of all, it's interoperable with our existing Android languages and runtime."))
//        listContacts.add(Contact(3, "Android Studio", "Android Studio 3.0 provides helpful tools to help you start using Kotlin. Convert entire Java files or convert code snippets on the fly when you paste Java code into a Kotlin file."))
//        listContacts.add(Contact(4, "Java Android Tutorial", "Create tutorial for people to learn Java Android. Learn Java in a greatly improved learning environment with more lessons, real practice opportunity, and community support."))
//        listContacts.add(Contact(5, "Spring Boot Tutorial", "Spring Boot help build stand-alone, production Spring Applications easily, less configuration then rapidly start new projects."))

        loadQueryAll()

        lvContacts.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            Toast.makeText(this, "Click on " + listContacts[position].name, Toast.LENGTH_SHORT).show()
        }
    }

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.layout.menu_main, menu)

        return super.onCreateOptionsMenu(menu)
    }*/

    /*override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            when (item.itemId) {
                R.id.addContact -> {
                    var intent = Intent(this, ContactActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }*/

    override fun onResume() {
        super.onResume()
        loadQueryAll()
    }

    private fun getExtra(): Boolean
    {
        val editOnClick: Boolean
        val extras = intent.extras
        editOnClick = extras.getBoolean("editOnClick")
        return editOnClick
    }

    fun loadQueryAll() {

        var dbManager = ContactDbManager(this)
        val cursor = dbManager.queryAllFromContacts()

        listContacts.clear()
        if (cursor.moveToFirst()) {

            do {
                val id = cursor.getInt(cursor.getColumnIndex("Id"))
                val name = cursor.getString(cursor.getColumnIndex("Name"))
                val phoneNumber = cursor.getString(cursor.getColumnIndex("PhoneNumber"))

                listContacts.add(Contact(id, name, phoneNumber))

            } while (cursor.moveToNext())
        }

        var contactsAdapter = ContactsAdapter(this, listContacts)
        lvContacts.adapter = contactsAdapter
    }

    inner class ContactsAdapter : BaseAdapter {

        private var contactsList = ArrayList<Contact>()
        private var context: Context? = null

        constructor(context: Context, contactsList: ArrayList<Contact>) : super() {
            this.contactsList = contactsList
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

            val view: View?
            val vh: ViewHolder

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.activity_listview_items, parent, false)
                vh = ViewHolder(view)
                view!!.tag = vh
                Log.i("JSA", "set Tag for ViewHolder, position: " + position)
            } else {
                view = convertView
                vh = view.tag as ViewHolder
            }

            var mContact = contactsList[position]

            vh.tvName.text = mContact.name
            vh.tvPhoneNumber.text = mContact.phoneNumber

            /*vh.ivEdit.setOnClickListener {
                updateContact(mContact)
            }

            vh.ivDelete.setOnClickListener {
                var dbManager = ContactDbManager(this.context!!)
                val selectionArgs = arrayOf(mContact.id.toString())
                dbManager.delete("Id=?", selectionArgs)
                loadQueryAll()
            }*/

            return view
        }

        override fun getItem(position: Int): Any {
            return contactsList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return contactsList.size
        }
    }

    private fun updateContact(contact: Contact) {
        var intent = Intent(this, AddActivity::class.java)
        intent.putExtra("MainActId", contact.id)
        intent.putExtra("MainActName", contact.name)
        intent.putExtra("MainActPhoneNumber", contact.phoneNumber)
        startActivity(intent)
    }

    private class ViewHolder(view: View?) {
        val tvName: TextView
        val tvPhoneNumber: TextView
        /*val ivEdit: ImageView
        val ivDelete: ImageView*/

        init {
            this.tvName = view?.findViewById(R.id.tvName) as TextView
            this.tvPhoneNumber = view?.findViewById(R.id.tvPhoneNumber) as TextView
            /*this.ivEdit = view?.findViewById(R.id.ivEdit) as ImageView
            this.ivDelete = view?.findViewById(R.id.ivDelete) as ImageView*/
        }

        // with API 26
//        init {
//            this.tvName = view?.findViewById<TextView>(R.id.tvName) as TextView
//            this.tvPhoneNumber = view?.findViewById<TextView>(R.id.tvPhoneNumber) as TextView
//            this.ivEdit = view?.findViewById<ImageView>(R.id.ivEdit) as ImageView
//            this.ivDelete = view?.findViewById<ImageView>(R.id.ivDelete) as ImageView
    }
}