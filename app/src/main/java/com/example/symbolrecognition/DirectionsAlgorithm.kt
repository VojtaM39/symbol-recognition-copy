package com.example.symbolrecognition

import android.util.Log
import kotlin.math.pow
import kotlin.math.sqrt

class DirectionsAlgorithm {
    private var movesX = mutableListOf<Array<Short>>()
    private var movesY = mutableListOf<Array<Short>>()
    private var startingPointsX = mutableListOf<Array<Int>>()
    private var startingPointsY = mutableListOf<Array<Int>>()
    private var endingPointsX = mutableListOf<Array<Int>>()
    private var endingPointsY = mutableListOf<Array<Int>>()
    private val sideToLenghtRatioX : Float
    private val sideToLenghtRatioY : Float
    private val startingPointsFinder : StartingPointsFinder
    constructor(movesX : MutableList<Array<Short>>, movesY : MutableList<Array<Short>>) {
        this.movesX = movesX
        this.movesY = movesY
        startingPointsFinder = StartingPointsFinder()
        this.startingPointsX = startingPointsFinder.findStartingPoints(movesX)
        this.startingPointsY = startingPointsFinder.findStartingPoints(movesY)
        this.endingPointsX = startingPointsFinder.findEndingPoints(movesX)
        this.endingPointsY = startingPointsFinder.findEndingPoints(movesY)
        sideToLenghtRatioX = getSideToLenghtRatio(startingPointsX,endingPointsX,movesX,movesY)
        sideToLenghtRatioY = getSideToLenghtRatio(startingPointsY, endingPointsY, movesY, movesX)
    }




    private fun logStartingPoints() {
        for(arr in startingPointsX) {
            Log.i("Pocet Starting X:", arr.size.toString())
        }

        for(arr in startingPointsY) {
            Log.i("Pocet Starting Y:", arr.size.toString())
        }
        for(arr in endingPointsX) {
            Log.i("Pocet Ending X:", arr.size.toString())
        }
        for(arr in endingPointsY) {
            Log.i("Pocet Ending Y:", arr.size.toString())
        }
    }
    //metoda vraci cislo 0-1, ktere znaci pomer vzdalenosti do dane strany ku celkove delce (napriklad rovna cara do daneho smeru bude mit pomer blizko 1, dokonale rovna cara 1, cara v opacnem smeru blizko 0)
    private fun getSideToLenghtRatio(startingPoints : MutableList<Array<Int>>, endingPoints : MutableList<Array<Int>>, movesMain: MutableList<Array<Short>>, movesSide: MutableList<Array<Short>>) : Float {
        //z kazdeho startingPoint pojede algoritmus na obe strany dokud nenarazi na ending point
        var sideLenght = 0f
        var lenght = 0f
        var k = 0
        //cyklus projizdi vsechny tahy
        for(i in movesMain.indices) {
            //prochazi jednotlive startingPoints daneho tahu
            for(j in startingPoints[i].indices) {
                //cyklus prochazejici vsechny body na obe strany od starting point, dokud nenarazi na ending point
                //k nastavime na prvni testovany bod
                k = startingPoints[i][j] - 1
                while(k >= 0 && !endingPoints[i].contains(k)) {
                    sideLenght += movesMain[i][k-1]-movesMain[i][k]
                    Log.i("Side Lenght", sideLenght.toString())
                    //Pythagorova veta
                    lenght += sqrt((movesMain[i][k]-movesMain[i][k-1]).toDouble().pow(2) + (movesSide[i][k]-movesSide[i][k-1]).toDouble().pow(2)).toShort()
                    Log.i("Lenght", lenght.toString())
                    k--
                }

                //k nastavime na prvni testovany bod
                k = startingPoints[i][j] + 1
                while(k <= movesMain[i].size-1 && !endingPoints[i].contains(k)) {
                    sideLenght += movesMain[i][k+1]-movesMain[i][k]
                    lenght += sqrt((movesMain[i][k]-movesMain[i][k+1]).toDouble().pow(2) + (movesSide[i][k]-movesSide[i][k+1]).toDouble().pow(2)).toShort()
                    k++
                }
            }
        }
        return sideLenght/lenght
    }

    private fun logRatios() {
        Log.i("Ratio X" , sideToLenghtRatioX.toString())
        Log.i("Ratio Y" , sideToLenghtRatioY.toString())
    }
    public fun run() {
        logRatios()
    }
    public fun getXRatio() : Float {
        return sideToLenghtRatioX
    }

    public fun getYRatio() : Float {
        return sideToLenghtRatioY
    }





}