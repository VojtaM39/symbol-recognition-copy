package com.example.symbolrecognition

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Toast
import com.example.symbolrecognition.DrawView.OnDrawEndListener




class DrawingActivity : AppCompatActivity() {
    private var pointsX = arrayOf<Float>()
    private var pointsY = arrayOf<Float>()
    private var touchCount = 0
    private var endsOfMove = arrayOf<Int>()
    lateinit var drawManager : DrawManager
    val context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawing)
        val drawView = findViewById<DrawView>(R.id.draw_view)
        val constraintLayout = findViewById<ConstraintLayout>(R.id.constraintLayout)
        drawView.setOnDrawEndListener(object : OnDrawEndListener {
            override fun onDrawEnd() {
                Log.v("", "EVENT FIRED")
                pointsX = drawView.getPointsX()
                pointsY = drawView.getPointsY()
                touchCount = drawView.getTouches()
                endsOfMove = drawView.getEndsOfMove()
                drawManager = DrawManager(pointsX,pointsY,touchCount,endsOfMove)
            }
        })
    }

}
