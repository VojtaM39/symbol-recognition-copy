package com.example.symbolrecognition

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View

class GesturePreviewView(context: Context, attrs: AttributeSet) : View(context, attrs)  {
    private var mPaint = Paint()
    private var mPath = Path()
    private var gestureId : Long
    private val dbManager : DbManager
    private var movesX = mutableListOf<Array<Short>>()
    private var movesY = mutableListOf<Array<Short>>()
    private var viewWidth : Int = 0
    private var viewHeight : Int = 0


    init {
        dbManager = DbManager(getContext())
        //TODO Pass Gesture Id by paramater
        gestureId = 1

        mPaint.apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 5f
            isAntiAlias = true
        }
    }

    public fun setGestureId(id : Long) {
        this.gestureId = id
        getPoints()
    }

    private fun getPoints() {
        var where = "${Constants.POINTS_GESTURE_ID} = $gestureId"
        val cursor = dbManager.queryWithWhere(Constants.POINTS_TABLE, where) //upravit
        var arrMovesX = arrayOf<Short>()
        var arrMovesY = arrayOf<Short>()
        var index = 0 //spolehame na to, ze jsou v databazi zaznamy sedridene podle tahu a ze zaciname na indexu 0

        if (cursor.moveToFirst())
        {
            do
            {
                val moveNumber = cursor.getInt(cursor.getColumnIndex(Constants.POINTS_MOVE_NUMBER))
                val pointX = cursor.getShort(cursor.getColumnIndex(Constants.POINTS_X))
                val pointY = cursor.getShort(cursor.getColumnIndex(Constants.POINTS_Y))

                if(moveNumber != index)
                {
                    movesX.add(index, arrMovesX)
                    movesY.add(index, arrMovesY)
                    arrMovesX = arrayOf<Short>()
                    arrMovesY = arrayOf<Short>()
                    index = moveNumber
                }
                if(moveNumber == index)
                {
                    arrMovesX += pointX
                    arrMovesY += pointY
                }
            } while (cursor.moveToNext())
            movesX.add(index, arrMovesX)
            movesY.add(index, arrMovesY)
        }
    }

    private fun createPath() {
        var ratio : Float = (Constants.SQUARE_SIZE.toFloat() / viewWidth.toFloat())
        Log.i("ratio", ratio.toString())
        var curX : Float
        var curY : Float
        var lastX = 0f
        var lastY = 0f
        var startX = 0f
        var startY = 0f
        for(i in movesX.indices) {
            for(j in movesX[i].indices) {
                curX = movesX[i][j].toFloat()/ratio
                curY = movesY[i][j].toFloat()/ratio
                if(j == 0) { //actionDown
                    startX = curX
                    startY = curY
                    mPath.moveTo(curX, curY)

                }
                else if (j == movesX[i].size-1) { //actionUp
                    mPath.lineTo(lastX, lastY)
                    // draw a dot on click
                    if (startX == curX && startY == curY) {
                        mPath.lineTo(curX, curY + 2)
                        mPath.lineTo(curX + 1, curY + 2)
                        mPath.lineTo(curX + 1, curY)
                    }
                }
                else { //actionMove
                    mPath.quadTo(lastX, lastY, (curX + lastX) / 2, (curY + lastY) / 2)
                }
                lastX = curX
                lastY = curY
            }
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        createPath()
        super.onDraw(canvas)
        canvas.drawPath(mPath, mPaint)
    }
    //https://stackoverflow.com/questions/4074937/android-how-to-get-a-custom-views-height-and-width
    override fun onSizeChanged(xNew: Int, yNew: Int, xOld: Int, yOld: Int) {
        super.onSizeChanged(xNew, yNew, xOld, yOld)
        var size : Int
        this.viewWidth = xNew
        Log.i("width", viewWidth.toString())
        this.viewHeight= yNew
        createPath()
        if (width < height) {
            size = height;
        } else {
            size = width;
        }
        setMeasuredDimension(size, size);
    }


}