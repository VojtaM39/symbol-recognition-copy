package com.example.symbolrecognition

class AreaDivider {
    private var movesX = mutableListOf<Array<Short>>()
    private var movesY = mutableListOf<Array<Short>>()
    //minimalni vzdalenost posledniho tahu od gesta, aby se povazovalv za extra symbol
    private val MINIMAL_DISTANCE_OF_GESTURES : Short = (Constants.SQUARE_SIZE*20/100).toShort()
    constructor(movesX : MutableList<Array<Short>>, movesY : MutableList<Array<Short>>) {
        this.movesX = movesX
        this.movesY = movesY
    }

    /**
     * Metoda vraci hodnotu nejvyssiho prvku ze vsech tahu, krome posledniho
     */

    private fun getMaxOfMain() : Short{
        var max :Short = 0
        for(i in movesY.indices) {
            if(i!=movesY.size-1) {
                if(i==0) {
                    max = movesY[i].max()!!
                }
                else if(max < movesY[i].max()!!) {
                    max = movesY[i].max()!!
                }
            }

        }
        return max
    }

    /**
     * Najde min (nejvyssi) hodnotu posledniho tahu
     */
    private fun getMinOfLastMove() : Short{
        return movesY[movesY.size-1].min()!!
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


}