package com.example.symbolrecognition

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log

class DrawingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawing)
        drawView = findViewById<DrawView>(R.id.draw_view)
        DrawingActivity.addingToDatabase = intent.getBooleanExtra("addingToDatabase", false)

    }
    companion object {
        private var pointsX = arrayOf<Float>()
        private var pointsY = arrayOf<Float>()
        private var touchCount = 0
        lateinit var drawView : DrawView
        private var endsOfMove = arrayOf<Int>()
        public val myHandler = Handler()
        var addingToDatabase : Boolean = false


        //Runnable se spusti po urcite dobe, co uzivatel prestane malovat
        val myRunnable = Runnable {
            getParametersFromView()
            if(addingToDatabase) {
                Log.i("Adding", "Pridava se do databaze")
            }
            //var drawManager = DrawManager(pointsX, pointsY, touchCount, endsOfMove)
            //drawManager.run()

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
