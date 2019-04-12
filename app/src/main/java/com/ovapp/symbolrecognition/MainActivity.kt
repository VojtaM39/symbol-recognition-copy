package com.ovapp.symbolrecognition

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

class MainActivity : AppCompatActivity() {
    private lateinit var  caller : Caller
    private lateinit var  databaseTester : DatabaseTester

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = (this as Activity)?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putFloat(getString(R.string.settings_accuracy), Constants.ACCURACY_DEFAULT_VALUE)
            commit()
        }

        this.databaseTester = DatabaseTester(this)

        val sharedPrefTest = (this as Activity)?.getPreferences(Context.MODE_PRIVATE) ?: return
        val highScore = sharedPref.getFloat(getString(R.string.settings_accuracy),Constants.ACCURACY_DEFAULT_VALUE)
        Log.i("Shared Preferences Test", highScore.toString())

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        caller.handlePermission(requestCode,permissions,grantResults)
    }


    fun launchAddActivity(view: View)
    {
        var intent: Intent = Intent(this, AddActivity::class.java)
        startActivity(intent)
    }

    fun launchDrawingActivity(view: View)
    {
        databaseTester.logPredefinedPoints()
        var intent: Intent = Intent(this, DrawingActivity::class.java)
        intent.putExtra("addingToDatabase", false)
        startActivity(intent)

    }

    fun launchEditActivity(view: View)
    {
        var intent: Intent = Intent(this, EditActivity::class.java)
        intent.putExtra("editOnClick", true)
        startActivity(intent)
    }

    fun launchDeleteActivity(view: View)
    {
        var intent: Intent = Intent(this, EditActivity::class.java)
        intent.putExtra("editOnClick", false)
        startActivity(intent)
    }

    fun launchSettingsActivity(view: View)
    {
        var intent: Intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
}

