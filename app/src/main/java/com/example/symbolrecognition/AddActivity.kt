package com.example.symbolrecognition

import android.content.ContentValues
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add.*
import android.provider.ContactsContract
import android.content.Intent
import android.R.attr.data
import android.util.Log


class AddActivity : AppCompatActivity()
{
    val PICK_CONTACT = 2015
    private var name = "Choose contact"
    private var height : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        val observer = drawView.getViewTreeObserver()
        observer.addOnGlobalLayoutListener {
            height = drawView.height
        }

        contactPickerBtn.setOnClickListener()
        {
            val i = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            startActivityForResult(i, PICK_CONTACT)
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
                    }
                }
            }
        }
        else
        {
            Log.e("MainActivity", "Failed to pick contact")
        }
    }
    private fun createGesture() {
        var pointsX = drawView.getPointsX()
        var pointsY = drawView.getPointsY()
        var touchCount = drawView.getTouches()
        var endsOfMove = drawView.getEndsOfMove()
        var drawManager = DrawManager(pointsX,pointsY,touchCount,endsOfMove, this, height)
        drawManager.createGesture("Test", "55555555555")
    }



}