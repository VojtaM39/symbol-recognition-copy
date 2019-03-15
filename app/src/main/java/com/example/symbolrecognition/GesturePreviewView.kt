package com.example.symbolrecognition

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class GesturePreviewView(context: Context, attrs: AttributeSet) : View(context, attrs)  {
    private var mPaint = Paint()
    private var mPath = Path()
    private val gestureId : Long
    private val dbManager : DbManager
    private var movesX = mutableListOf<Array<Short>>()
    private var movesY = mutableListOf<Array<Short>>()

    init {
        dbManager = DbManager(getContext())
        //TODO Pass Gesture Id by paramater
        gestureId = 1
        getPoints()
        createPath()
        mPaint.apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 1f
            isAntiAlias = true
        }
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
        var width = width
        var height = height
        val ratio = (Constants.SQUARE_SIZE / width).toFloat()
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
                    mPath.lineTo(curX, curY)

                }
                else if (j == movesX[i].size-1) { //actionUp
                    mPath.lineTo(curX, curY)
                    // draw a dot on click
                    if (startX == curX && startY == curY) {
                        mPath.lineTo(curX, curY + 2)
                        mPath.lineTo(curX + 1, curY + 2)
                        mPath.lineTo(curX + 1, curY)
                    }
                }
                else { //actionMove
                    mPath.quadTo(curX, curY, (lastX + curX) / 2, (lastY + curY) / 2)
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


}