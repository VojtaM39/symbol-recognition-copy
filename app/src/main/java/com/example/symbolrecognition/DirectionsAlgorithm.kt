package com.example.symbolrecognition

import android.util.Log
import kotlin.math.pow
import kotlin.math.sqrt

class DirectionsAlgorithm {
    private var pointsX : Array<Short>
    private var pointsY : Array<Short>
    private var touchCount : Int
    private var movesX = mutableListOf<Array<Short>>()
    private var movesY = mutableListOf<Array<Short>>()
    private var startingPointsX = mutableListOf<Array<Int>>()
    private var startingPointsY = mutableListOf<Array<Int>>()
    private var endingPointsX = mutableListOf<Array<Int>>()
    private var endingPointsY = mutableListOf<Array<Int>>()
    private val sideToLenghtRatioX : Float
    private val sideToLenghtRatioY : Float
    constructor(pointsX:Array<Short>, pointsY : Array<Short>, touchCount : Int, movesX : MutableList<Array<Short>>, movesY : MutableList<Array<Short>>) {
        this.pointsX = pointsX
        this.pointsY = pointsY
        this.touchCount = touchCount
        this.movesX = movesX
        this.movesY = movesY
        this.startingPointsX = findStartingPoints(movesX)
        this.startingPointsY = findStartingPoints(movesY)
        this.endingPointsX = findEndingPoints(movesX)
        this.endingPointsY = findEndingPoints(movesY)
        sideToLenghtRatioX = getSideToLenghtRatio(startingPointsX,endingPointsX,movesX,movesY)
        sideToLenghtRatioY = getSideToLenghtRatio(startingPointsY, endingPointsY, movesY, movesX)
    }

    private fun findStartingPoints(points : MutableList<Array<Short>>) : MutableList<Array<Int>> {
        //cyklus prochazi tahy
        var isStarting : Boolean
        var startingPoints = mutableListOf<Array<Int>>()
        for(arr in points) {
            //startingPoints daneho tahu (indexy)
            var startingPointsArr = arrayOf<Int>()
            //prochazi jednlotlive body
            for(i in arr.indices) {
                isStarting = true
                //zjistim jestli body na obe strany od daneho bodu jsou vic vlevo(nahore), pokud jsou oba vic vpravo, je dany bod starting point
                if(i-1 >= 0) {
                    if(arr[i-1] < arr[i])
                        isStarting = false
                }
                if(i+1 <= arr.size-1) {
                    if(arr[i+1] < arr[i])
                        isStarting = false
                }
                if(isStarting) {
                    startingPointsArr += i
                }

            }
            startingPoints.add(startingPointsArr)
        }
        return startingPoints
    }

    private fun findEndingPoints(points : MutableList<Array<Short>>) : MutableList<Array<Int>> {
        //cyklus prochazi tahy
        var isEnding : Boolean
        var endingPoints = mutableListOf<Array<Int>>()
        for(arr in points) {
            //endingPoints daneho tahu (indexy)
            var endingPointsArr = arrayOf<Int>()
            //prochazi jednlotlive body
            for(i in arr.indices) {
                isEnding = true
                //zjistim jestli body na obe strany od daneho bodu jsou vic vlevo(nahore), pokud jsou oba vic vpravo, je dany bod starting point
                if(i-1 >= 0) {
                    if(arr[i-1] > arr[i])
                        isEnding = false
                }
                if(i+1 <= arr.size-1) {
                    if(arr[i+1] > arr[i])
                        isEnding = false
                }
                if(isEnding) {
                    endingPointsArr += i
                }

            }
            endingPoints.add(endingPointsArr)
        }
        return endingPoints
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




}