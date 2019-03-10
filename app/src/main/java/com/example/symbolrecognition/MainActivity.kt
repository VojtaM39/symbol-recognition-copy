package com.example.symbolrecognition

import android.Manifest
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.telecom.Call
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {
    private lateinit var  caller : Caller
    private lateinit var  databaseTester : DatabaseTester

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.databaseTester = DatabaseTester(this)
        /**
        caller = Caller(7, this)
        caller.setupPermissions()
        val drawBtn = findViewById<TextView>(R.id.btnDrawing)
        drawBtn.setOnClickListener {

            caller.run()
        }
        */

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
        /**
        var intent: Intent = Intent(this, DrawingActivity::class.java)
        intent.putExtra("addingToDatabase", false)
        startActivity(intent)
        */
        databaseTester.logPoints()
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
}

