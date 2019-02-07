package com.example.symbolrecognition

class AreaDivider {
    private var movesX = mutableListOf<Array<Short>>()
    private var movesY = mutableListOf<Array<Short>>()
    constructor(movesX : MutableList<Array<Short>>, movesY : MutableList<Array<Short>>) {
        this.movesX = movesX
        this.movesY = movesY
    }

    /**
     * Metoda vraci nejvyssi hodnotu tahu
     */
    private fun getMoveMax(move:Array<Short>) : Short {
        var max : Short = 0
        for(i in move.indices) {
            if(i==0) {
                max = move[i]
            }
            else if(move[i] > max) {
                max = move[i]
            }

        }
        return max
    }


    /**
     * Metoda vraci nejnizsi hodnotu tahu
     */
    private fun getMoveMin(move:Array<Short>) : Short {
        var min : Short = 0
        for(i in move.indices) {
            if(i==0) {
                min = move[i]
            }
            else if(move[i] < min) {
                min = move[i]
            }

        }
        return min
    }


    /**
     * Metoda vraci hodnotu nejvyssiho prvku ze vsech tahu, krome posledniho
     */

    private fun getMaxOfMain() : Short{
        var max :Short = 0
        for(i in movesY.indices) {
            if(i!=movesY.size-2) {
                if(i==0) {
                    max = getMoveMax(movesY[i])
                }
                else if(max < getMoveMax(movesY[i])) {
                    max = getMoveMax(movesY[i])
                }
            }

        }
        return max
    }

    /**
     * Najde min (nejvyssi) hodnotu posledniho tahu
     */
    private fun getMinOfLastMove() : Short{
        return getMoveMin(movesY[movesY.size-1])
    }
}