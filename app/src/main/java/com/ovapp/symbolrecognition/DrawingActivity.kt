package com.ovapp.symbolrecognition

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.ovapp.symbolrecognition.DrawView.OnDrawEndListener
import android.view.*


class DrawingActivity : AppCompatActivity() {
    private var pointsX = arrayOf<Float>()
    private var pointsY = arrayOf<Float>()
    private var touchCount = 0
    private var endsOfMove = arrayOf<Int>()
    lateinit var drawManager : DrawManager
    lateinit var dbManager: DbManager
    private var result = true
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
                endsOfMove = drawView.getEndsOfMove()
                drawManager = DrawManager(pointsX,pointsY,endsOfMove, context, height)
                //drawManager.createGesture("Test", "55555555555")
                result = drawManager.run()
                if(!result) {
                    drawView.resetGesture()
                }
            }
        })
    }

}
