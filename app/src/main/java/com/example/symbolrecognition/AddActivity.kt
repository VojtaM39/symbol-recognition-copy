package com.example.symbolrecognition

import android.content.ContentValues
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add.*
import android.provider.ContactsContract
import android.content.Intent



class AddActivity : AppCompatActivity()
{
    val PICK_CONTACT = 2015
    var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        try {
            var bundle: Bundle? = intent.extras
            id = bundle!!.getInt("MainActId", 0)
            if (id != 0) {
                edtName.setText(bundle.getString("MainActName"))
                edtPhoneNumber.setText(bundle.getString("MainActPhoneNumber"))
            }
        } catch (ex: Exception) {
        }

        btnSave.setOnClickListener {
            var dbManager = DbManager(this)

            var values = ContentValues()
            values.put("Name", edtName.text.toString())
            values.put("PhoneNumber", edtPhoneNumber.text.toString())

            if (id == 0) {
                val mID = dbManager.insert(values, Constants.CONTACTS_TABLE)

                if (mID > 0) {
                    Toast.makeText(this, "Add note successfully!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, "Fail to add note!", Toast.LENGTH_LONG).show()
                }
            } else {
                var selectionArs = arrayOf(id.toString())
                val mID = dbManager.updateContacts(values, "Id=?", selectionArs)

                if (mID > 0) {
                    Toast.makeText(this, "Add note successfully!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, "Fail to add note!", Toast.LENGTH_LONG).show()
                }
            }
        }

        btnContactPicker.setOnClickListener()
        {
            val i = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            startActivityForResult(i, PICK_CONTACT)
        }
    }
    /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_CONTACT && resultCode == Activity.RESULT_OK) {
            val contactUri = data!!.data
            val cursor = contentResolver.query(contactUri!!, null, null, null, null)
            cursor!!.moveToFirst()
            val column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            normalizePhoneNumberTask().execute(cursor.getString(column))
        }
    }

    internal inner class normalizePhoneNumberTask : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String): String {

            var normalizedPhoneNumber = ""

            try {
                val httpclient = DefaultHttpClient()
                val httpGet = HttpGet(
                    "https://callingapi.sinch.com/v1/calling/query/number/" + params[0].replace(
                        "\\s+".toRegex(),
                        ""
                    )
                )

                val usernamePassword = "application:$APP_KEY:$APP_SECRET"
                val encoded = Base64.encodeToString(usernamePassword.toByteArray(), Base64.NO_WRAP)
                httpGet.addHeader("Authorization", "Basic $encoded")

                val response = httpclient.execute(httpGet)
                val handler = BasicResponseHandler()
                normalizedPhoneNumber = parseJSONResponse(handler.handleResponse(response))
            } catch (e: ClientProtocolException) {
                Log.d("ClientProtocolException", e.getMessage())
            } catch (e: IOException) {
                Log.d("IOException", e.getMessage())
            }

            return normalizedPhoneNumber
        }

        override fun onPostExecute(normalizedPhoneNumber: String) {
            Toast.makeText(applicationContext, normalizedPhoneNumber, Toast.LENGTH_LONG).show()
        }

        private fun parseJSONResponse(jsonString: String): String {

            var returnString = ""

            try {
                val jsonObject = JSONObject(jsonString)
                returnString = jsonObject.getJSONObject("number").getString("normalizedNumber")
            } catch (e: JSONException) {
                Log.d("JSONException", e.message)
            }

            return returnString
        }
    }*/
}