package com.example.symbolrecognition

import android.content.ContentValues
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add.*
import android.provider.ContactsContract
import android.content.Intent
import android.R.attr.data
import android.app.Activity
import android.util.Log


class AddActivity : AppCompatActivity()
{
    val PICK_CONTACT = 2015
    private lateinit var  caller : Caller
    var contactId : Long = 0
    private var name = "Choose contact"
    private var height : Int = 0
    private var selected = false
    private var edit = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        caller  = Caller(this as Activity)
        caller.setupPermissions()

        nameTxtView.setText(name)
        val observer = drawView.getViewTreeObserver()
        observer.addOnGlobalLayoutListener {
            height = drawView.height
        }

        contactPickerBtn.setOnClickListener()
        {
            val i = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            startActivityForResult(i, PICK_CONTACT)
            this.selected = true
        }

        deleteBtn.setOnClickListener {
            drawView.resetGesture()
        }

        createBtn.setOnClickListener {
            //Uzivatel uz vybral kontakt
            //TODO overit malovani
            if(!selected || !drawView.getDrew()) {
                Log.i("Add", "Error add")
                Toast.makeText(this, "You have to fill contact and draw gesture.",Toast.LENGTH_SHORT).show()
            }
            else {
                createGesture(contactId)
                Toast.makeText(this, "Gesture was created successfully.",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

    }
    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?) {
        // check whether the result is ok
        if (resultCode == RESULT_OK)
        {
            // Check for the request code, we might be usign multiple startActivityForReslut
            if(requestCode == PICK_CONTACT) {
                /**
                 * https://developer.android.com/training/basics/intents/result
                 */

                val projection: Array<String> = arrayOf(ContactsContract.Contacts._ID)

                // Get the URI that points to the selected contact
                data!!.data?.also { contactUri ->
                    contentResolver.query(contactUri, null, null, null, null)?.apply {
                        moveToFirst()

                        val column: Int = getColumnIndex(ContactsContract.Contacts._ID)
                        val id: Long? = getLong(column)
                        val columnName: Int = getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                        val name: String? = getString(columnName)
                        Log.i("Contact picker", "Picked contact ID: " + id)
                        nameTxtView.setText(name)
                        contactId = id!!
                    }
                }
            }
        }
        else
        {
            Log.e("MainActivity", "Failed to pick contact")
        }
    }



    private fun createGesture(contactId : Long) {
        var pointsX = drawView.getPointsX()
        var pointsY = drawView.getPointsY()
        var touchCount = drawView.getTouches()
        var endsOfMove = drawView.getEndsOfMove()
        var drawManager = DrawManager(pointsX,pointsY,touchCount,endsOfMove, this, height)
        drawManager.createGesture(contactId)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        caller.handlePermission(requestCode,permissions,grantResults)
        var gestureId : Long
        if(intent.hasExtra("gestureId")) {
            gestureId = intent.getLongExtra("gestureId", 0)
            nameTxtView.setText(caller.getNameByContactId(caller.getContactIdByGestureId(gestureId).toString()))
            Log.i("Contact name", caller.getNameByContactId(caller.getContactIdByGestureId(gestureId).toString()))
            this.selected = true
        }
    }
}