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

    companion object {
        private var pointsX = arrayOf<Float>()
        private var pointsY = arrayOf<Float>()
        private var touchCount = 0
        lateinit var drawView : DrawView
        private var endsOfMove = arrayOf<Int>()

        public val myHandler = Handler()
        //Runnable se spusti po urcite dobe, co uzivatel prestane malovat
        val myRunnable = Runnable {
            getParametersFromView()
            var drawManager = DrawManager(pointsX, pointsY, touchCount, endsOfMove)
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

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu)
        val wannaDraw: Button = findViewById<Button>(R.id.button_drawing)
        /*wannaDraw.setOnClickListener()
        {
            setContentView(R.layout.drawing)
            drawView = findViewById<DrawView>(R.id.draw_view)
        }*/
    }
    /*private fun launchAddActivity(view: View)
    {
        var intent: Intent = Intent(this, AddActivity)
        startActivity(intent)
    }*/

}

