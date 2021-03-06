package com.ovapp.symbolrecognition

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

//https://android.jlelse.eu/a-guide-to-drawing-in-android-631237ab6e28
class DrawView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val useTimer : Boolean
    private var mPaint = Paint()
    private var mPath = Path()
    private var drew = false
    private var mCurX = 0f
    private var mCurY = 0f
    private var mStartX = 0f
    private var mStartY = 0f
    private var count = 0
    private var drawing = false
    private var pointsX = arrayOf<Float>()
    private var pointsY = arrayOf<Float>()
    //v poli budou indexy bodů, kde končí tah
    private var endsOfMove = arrayOf<Int>()
    fun resetTimer() {
        if(useTimer) {
            this.removeCallbacks(myRunnable)
            this.postDelayed(myRunnable, 1200)
        }
    }
    val myRunnable = Runnable {
        onDrawEndListener!!.onDrawEnd()
    }

    //Pokud uzivatel maloval => true
    public fun getDrew() : Boolean {
        return drew
    }

    //gettery pro zisskani bodu a poctu tahu
    public fun  getPointsX(): Array<Float> {
        return pointsX
    }
    public fun  getPointsY(): Array<Float> {
        return pointsY
    }

    public fun getEndsOfMove() : Array<Int> {
        return endsOfMove
    }

    //metoda vyresetuje drawview
    public fun resetGesture() {
        this.mPath = Path()
        invalidate()
        this.drew = false
        this.pointsX = arrayOf()
        this.pointsY = arrayOf()
        this.endsOfMove = arrayOf()
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
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.DrawView)
        this.useTimer = attributes.getBoolean(R.styleable.DrawView_timer, true)
        attributes.recycle()

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawPath(mPath, mPaint)
    }
    //Pri zacatku tahu se zvysi pocet tahu, reset Timeru
    private fun actionDown(x: Float, y: Float) {
        resetTimer()
        mPath.moveTo(x, y)
        pointsX += x
        pointsY += y
        mCurX = x
        mCurY = y
        if(!drew) {
            drew = true
        }
    }
    //Pri kazdem pohybu se zapisou souradnice do poli
    private fun actionMove(x: Float, y: Float) {
        //reset Timeru pri kazdem pohybu
        resetTimer()
        pointsX += x
        pointsY += y
        Log.i("Drawing", "Hodnota pointsX je ${pointsX[pointsX.size-1]} Hodnota pointsY je ${pointsY[pointsY.size-1]}")
        mPath.quadTo(mCurX, mCurY, (x + mCurX) / 2, (y + mCurY) / 2)
        mCurX = x
        mCurY = y
    }

    private fun actionUp() {
        //reset Timeru
        resetTimer()
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

    /**
     * https://stackoverflow.com/questions/3398363/how-to-define-callbacks-in-android
     */
    private var onDrawEndListener: OnDrawEndListener? = null

    interface OnDrawEndListener {
        fun onDrawEnd()
    }

    // ALLOWS YOU TO SET LISTENER && INVOKE THE OVERIDING METHOD
    // FROM WITHIN ACTIVITY
    fun setOnDrawEndListener(listener: OnDrawEndListener) {
        onDrawEndListener = listener
    }

}