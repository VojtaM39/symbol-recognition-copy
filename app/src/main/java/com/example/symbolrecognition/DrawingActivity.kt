package com.example.symbolrecognition

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class DrawingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawing)
        MainActivity.drawView = findViewById<DrawView>(R.id.draw_view)
    }
}
