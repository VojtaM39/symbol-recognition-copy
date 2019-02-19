package com.example.symbolrecognition

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*val btnEdit = findViewById<Button>(R.id.btnEdit)
        btnEdit.setOnClickListener()
        {
            var intent: Intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
        }*/
    }

    fun launchAddActivity(view: View)
    {
        var intent: Intent = Intent(this, AddActivity::class.java)
        intent.putExtra("addingToDatabase", true)
        startActivity(intent)
    }

    fun launchDrawingActivity(view: View)
    {
        var intent: Intent = Intent(this, DrawingActivity::class.java)
        intent.putExtra("addingToDatabase", false)
        startActivity(intent)
    }

    fun launchEditActivity(view: View)
    {
        var intent: Intent = Intent(this, EditActivity::class.java)
        startActivity(intent)
    }
}

