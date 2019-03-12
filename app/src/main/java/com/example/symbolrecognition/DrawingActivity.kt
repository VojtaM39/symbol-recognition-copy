package com.example.symbolrecognition

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.util.DisplayMetrics
import android.util.Log
import android.view.View.OnTouchListener
import android.widget.Toast
import com.example.symbolrecognition.DrawView.OnDrawEndListener
import android.opengl.ETC1.getHeight
import android.view.*


class DrawingActivity : AppCompatActivity() {
    private var pointsX = arrayOf<Float>()
    private var pointsY = arrayOf<Float>()
    private var touchCount = 0
    private var endsOfMove = arrayOf<Int>()
    lateinit var drawManager : DrawManager
    lateinit var dbManager: DbManager
    val context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_drawing)
        this.dbManager = DbManager(this)
        var numberOfGestures = dbManager.count(Constants.GESTURES_TABLE)
        if(numberOfGestures==0) {
            Toast.makeText(this, "You have to first create some gesture", Toast.LENGTH_SHORT).show()
            var intent: Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        val drawView = findViewById<DrawView>(R.id.draw_view)
        val observer = drawView.getViewTreeObserver()
        var height : Int = 0
        observer.addOnGlobalLayoutListener {
            height = drawView.height
        }
        drawView.setOnDrawEndListener(object : OnDrawEndListener {
            override fun onDrawEnd() {
                Log.v("", "EVENT FIRED")
                pointsX = drawView.getPointsX()
                pointsY = drawView.getPointsY()
                touchCount = drawView.getTouches()
                endsOfMove = drawView.getEndsOfMove()
                drawManager = DrawManager(pointsX,pointsY,touchCount,endsOfMove, context, height)
                //drawManager.createGesture("Test", "55555555555")
                drawManager.run()
            }
        })
    }

}
