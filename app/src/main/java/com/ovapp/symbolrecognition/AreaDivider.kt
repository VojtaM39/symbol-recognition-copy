package com.ovapp.symbolrecognition

class AreaDivider {
    private val pointsY : Array<Float>
    private val endsOfMove : Array<Int>
    private val drawViewHeight : Int
    //minimalni vzdalenost posledniho tahu od gesta, aby se povazovalv za extra symbol
    private val MINIMAL_DISTANCE_OF_GESTURES : Float
    constructor(pointsY : Array<Float>, endsOfMove : Array<Int>, drawViewHeight : Int) {
        this.pointsY = pointsY
        this.endsOfMove = endsOfMove
        this.drawViewHeight = drawViewHeight
        MINIMAL_DISTANCE_OF_GESTURES = 80f
    }

    /**
     * Metoda vraci hodnotu nejvyssiho prvku ze vsech tahu, krome posledniho
     */

    private fun getMaxOfMain() : Float{
        var max :Float = 0f
        for(i in 0..endsOfMove[endsOfMove.lastIndex-1]) {
            if(i==0) {
                max = pointsY[i]
            }
            else {
                if(pointsY[i]>max){
                    max = pointsY[i]
                }
            }
        }
        return max
    }

    /**
     * Najde min (nejvyse postaveny bod) hodnotu posledniho tahu
     */
    private fun getMinOfLastMove() : Float{

        val from = endsOfMove[endsOfMove.lastIndex-1]+1
        var min = pointsY[from]
        for(i in from..pointsY.size-1) {
                if(pointsY[i]<min) {
                    min = pointsY[i]
                }
        }
        return min
    }

    /**
     * Metoda vraci true, pokud posledni tah uzivatele je extra symbol
     */
    public fun doesExistsExtraSymbol() : Boolean{
        if(getMinOfLastMove()-getMaxOfMain()>MINIMAL_DISTANCE_OF_GESTURES)
            return true
        else
            return false
    }

    /**
     *http://www.androidtutorialshub.com/how-to-get-width-and-height-android-screen-in-pixels/
     */



}