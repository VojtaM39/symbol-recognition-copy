package com.example.symbolrecognition

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class AddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        MainActivity.drawView = findViewById<DrawView>(R.id.draw_view)
    }
}
