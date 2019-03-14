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
    private lateinit var dbManager : DbManager
    private var editingGestureId : Long = 0
    private var successSaveToast = "Contact was created successfully"
    private val context = this
    private val MAX_SIMILARITY = 0.85f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        dbManager = DbManager(this)
        caller  = Caller(this)
        caller.setupPermissions()

        nameTxtView.setText(name)
        val observer = drawView.getViewTreeObserver()
        observer.addOnGlobalLayoutListener {
            height = drawView.height
        }

        if(intent.hasExtra("gestureId")) {
            this.successSaveToast = "Contact was updated successfully"
            createBtn.text = "update"
            contactPickerBtn.isEnabled = false
            var gestureId : Long
            this.editingGestureId = intent.getIntExtra("gestureId",0).toLong()
            nameTxtView.setText(caller.getContactName(caller.getContactIdByGestureId(this.editingGestureId)))
            Log.i("Contact name", caller.getNameByContactId(caller.getContactIdByGestureId(this.editingGestureId).toString()))
            this.selected = true
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
            if(!selected || !drawView.getDrew()) {
                Log.i("Add", "Error add")
                Toast.makeText(this, "You have to fill contact and draw gesture.",Toast.LENGTH_SHORT).show()
            }
            else {
                var pointsX = drawView.getPointsX()
                var pointsY = drawView.getPointsY()
                var touchCount = drawView.getTouches()
                var endsOfMove = drawView.getEndsOfMove()
                var drawManager = DrawManager(pointsX,pointsY,touchCount,endsOfMove, this, height)

                var result : Float? = null
                var similarContactName : String? = null
                var numberOfGestures = dbManager.count(Constants.GESTURES_TABLE)
                if(numberOfGestures==0) {
                    result = drawManager.getMostSimilarValue()
                    similarContactName = drawManager.getMostSimilarContactName()
                }
                //uzivatel vytvoril gesto, ktere obsahuje maly symbol
                if(drawManager.getExistsExtraSymbol()) {
                    Toast.makeText(this,"You drew gesture, that contains small extra gesture.",Toast.LENGTH_SHORT).show()
                }
                else if(drawManager.getMostSimilarValue() > MAX_SIMILARITY) {
                    Toast.makeText(this,"This gesture is too similar to gesture of contact " + drawManager.getMostSimilarContactName(),Toast.LENGTH_SHORT).show()
                }
                else {
                    if(!intent.hasExtra("gestureId")) {
                        drawManager.createGesture(contactId)
                    }
                    else {
                        drawManager.updateGesture(this.editingGestureId)
                    }
                    Toast.makeText(this, this.successSaveToast,Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
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
                        //Pro vybrany kontakt uz existuje gesto
                        if(dbManager.countWithWhere(Constants.GESTURES_TABLE, Constants.GESTURES_CONTACT_ID + "=" + id.toString()) > 0) {
                            Toast.makeText(context, "Gesture for this contact was already created",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            nameTxtView.setText(name)
                            contactId = id!!
                        }
                    }
                }
            }
        }
        else
        {
            Log.e("MainActivity", "Failed to pick contact")
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        caller.handlePermission(requestCode,permissions,grantResults)

    }
}