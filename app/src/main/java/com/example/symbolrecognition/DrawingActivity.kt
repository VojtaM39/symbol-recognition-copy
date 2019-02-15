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



class DrawingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawing)
        val drawView = findViewById<DrawView>(R.id.draw_view)
        val constraintLayout = findViewById<ConstraintLayout>(R.id.constraintLayout)
        DrawingActivity.addingToDatabase = intent.getBooleanExtra("addingToDatabase", false)
        constraintLayout.setOnTouchListener(OnTouchListener { v, event ->
            //show dialog here
            false
        })
        }


    companion object {
        private var pointsX = arrayOf<Float>()
        private var pointsY = arrayOf<Float>()
        private var touchCount = 0
        //lateinit var drawView : DrawView
        private var endsOfMove = arrayOf<Int>()
        public val myHandler = Handler()
        var addingToDatabase : Boolean = false


        //Runnable se spusti po urcite dobe, co uzivatel prestane malovat
        val myRunnable = Runnable {
            getParametersFromView()
            if(addingToDatabase) {
                Log.i("Adding", "Pridava se do databaze")
            }
            var drawManager = DrawManager(pointsX, pointsY, touchCount, endsOfMove, this)
            drawManager.run()

        }
        fun resetTimer() {
            myHandler.removeCallbacks(myRunnable);
            myHandler.postDelayed(myRunnable, 3000);
        }
        fun getParametersFromView() {

            pointsX = drawView.getPointsX()
            pointsY = drawView.getPointsY()
            touchCount = drawView.getTouches()
            endsOfMove = drawView.getEndsOfMove()
            Log.i("Done", "Nacteno")
            Log.i("Done", "Pocet tahu: ${touchCount}")
        }
    }
}
