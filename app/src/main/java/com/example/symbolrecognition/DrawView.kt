package com.example.symbolrecognition

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import java.util.*
import kotlin.concurrent.schedule
import android.content.ContextWrapper



class DrawView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var mPaint = Paint()
    private var mPath = Path()

    private var mCurX = 0f
    private var mCurY = 0f
    private var mStartX = 0f
    private var mStartY = 0f
    private var count = 0
    private var ended = false
    private var drawing = false
    private var pointsX = arrayOf<Float>()
    private var pointsY = arrayOf<Float>()
    //v poli budou indexy bodů, kde končí tah
    private var endsOfMove = arrayOf<Int>()

    //gettery pro zisskani bodu a poctu tahu
    public fun  getPointsX(): Array<Float> {
        return pointsX
    }
    public fun  getPointsY(): Array<Float> {
        return pointsY
    }
    public fun getTouches() : Int {
        return count
    }

    public fun getEndsOfMove() : Array<Int> {
        return endsOfMove
    }
    init {

        mPaint.apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 15f
            isAntiAlias = true
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawPath(mPath, mPaint)
    }
    //Pri zacatku tahu se zvysi pocet tahu, reset Timeru
    private fun actionDown(x: Float, y: Float) {
        mPath.moveTo(x, y)
        pointsX += x
        pointsY += y
        mCurX = x
        mCurY = y
        countUp()
        DrawingActivity.resetTimer()
    }
    //Pri kazdem pohybu se zapisou souradnice do poli
    private fun actionMove(x: Float, y: Float) {
        //reset Timeru pri kazdem pohybu
        DrawingActivity.resetTimer()
        pointsX += x
        pointsY += y
        Log.i("Drawing", "Hodnota pointsX je ${pointsX[pointsX.size-1]} Hodnota pointsY je ${pointsY[pointsY.size-1]}")
        mPath.quadTo(mCurX, mCurY, (x + mCurX) / 2, (y + mCurY) / 2)
        mCurX = x
        mCurY = y
    }

    private fun actionUp() {
        //reset Timeru
        DrawingActivity.resetTimer()
        mPath.lineTo(mCurX, mCurY)
        drawing = false
        //pridani indexu do endsOfMove
        endsOfMove += (pointsX.size-1)
        // draw a dot on click
        if (mStartX == mCurX && mStartY == mCurY) {
            mPath.lineTo(mCurX, mCurY + 2)
            mPath.lineTo(mCurX + 1, mCurY + 2)
            mPath.lineTo(mCurX + 1, mCurY)
        }
    }


    private fun countUp() {
        count++

    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mStartX = x
                mStartY = y
                actionDown(x, y)
            }
            MotionEvent.ACTION_MOVE -> actionMove(x, y)
            MotionEvent.ACTION_UP -> actionUp()
        }
        invalidate()
        return true
    }

}