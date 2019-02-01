package com.example.symbolrecognition

import android.util.Log

class DrawManager {
    private var pointsX : Array<Float>
    private var pointsY : Array<Float>
    private var pointsXResult : Array<Short>
    private var pointsYResult : Array<Short>
    private var touchCount : Int
    private val endsOfMove : Array<Int>
    private val SQUARE_SIZE : Int = Constants.SQUARE_SIZE
    private var movesX = mutableListOf<Array<Short>>()
    private var movesY = mutableListOf<Array<Short>>()

    constructor(pointsX:Array<Float>, pointsY : Array<Float>, touchCount : Int, endsOfMove : Array<Int>) {
        this.pointsX = pointsX
        this.pointsY = pointsY
        this.touchCount = touchCount
        this.endsOfMove = endsOfMove
        processArrays()
        this.pointsXResult = floatToShort(this.pointsX)
        this.pointsYResult = floatToShort(this.pointsY)
        this.movesX = generateMoves(pointsXResult, endsOfMove)
        this.movesY = generateMoves(pointsYResult, endsOfMove)

    }
    //Metoda vytvori MutableList ktere bude obsahovat pole s body jednotlivych tahu
    private fun generateMoves(points : Array<Short>, endsOfMove: Array<Int>) :MutableList<Array<Short>> {
        //pomocne pole, do ktereho se budou davat body daneho tahu
        var array = arrayOf<Short>()
        //list vsech poli
        var listOfMoves = mutableListOf<Array<Short>>()
        for(i in points.indices) {
            array += points[i]
            if(endsOfMove.contains(i)) {
                listOfMoves.add(array)
                array = arrayOf<Short>()

            }
        }
        return listOfMoves
    }




    //metoda bere pole se souradnicemi X a Y, jako bigger se posle to pole, ktere ma vetsi rozptyl. Vysledkem je pole bodu ve ctverci 100x100, zarovnane na stred
    private fun cropArrays(bigger : Array<Float>, smaller : Array<Float>) {
        //rozdily od kraje pro kazde pole
        val differenceBig = bigger.min()!!
        val differenceSmall = smaller.min()!!
        //pomer ve kterem se musi obrazec zmensit aby se dostal na pozadovanou velikost
        val ratio = (bigger.max()!!-bigger.min()!!)/SQUARE_SIZE
        //Pole projde kazdy bod z obou poli, posune je na kraj (odecte rozdil od kraje) a zmensi v pomeru, ve kterem je original vetsi od pozadovaneho vysledku
        for(i in 0..bigger.size-1) {
            bigger[i] -=  differenceBig
            smaller[i] -=  differenceSmall
            bigger[i] /= ratio
            smaller[i] /= ratio
        }
        //o kolik se musi body na ose, ktera nedosahuje kraju ctverce posunout do stredu

        val shiftToCenter = (SQUARE_SIZE - smaller.max()!!)/2
        for(i in 0..smaller.size-1) {
            smaller[i] += shiftToCenter
        }


    }

    private fun floatToShort(arr : Array<Float>) : Array<Short>{
        var result = arrayOf<Short>()
        for(item in arr) {
            result += item.toShort()
        }
        return result
    }
    //Metoda vola metodu cropArrays, dosazuje do ni v poradi podle toho, ktera osa ma vetsi rozptyl
    private fun processArrays() {
        //pouze bod
        Log.i("Pocet bodu", this.pointsX.size.toString())
        if((this.pointsX.max()!! - this.pointsX.min()!!) > (this.pointsY.max()!! - this.pointsY.min()!!)) {
            cropArrays(pointsX, pointsY)
        }
        else {
            cropArrays(pointsY, pointsX)
        }
    }

    public fun logArray(tag:String,pointsArray: Array<Float>) {
        var result = ""
        for(i in 0..pointsArray.size-1) {
            result += pointsArray[i].toString() + ", "
        }
        Log.i("pocet bodu: ", pointsArray.size.toString())
        Log.i(tag, result)
    }

    public fun logMoves() {
        var result : String
        for(i in 0..movesX.size-1) {
            result = ""
            for(j in 0..movesX[i].size-1) {
                result += movesX[i][j].toString() + ", "
            }
            Log.i("Tah:", result)
        }
    }

    public fun  run() {

        logMoves()
        //var directionsAlgorithm = DirectionsAlgorithm(pointsXResult,pointsYResult,touchCount,movesX,movesY)
        //directionsAlgorithm.run()
        //val lineDetector = LineDetector(pointsXResult,pointsYResult,touchCount,movesX,movesY)
        //lineDetector.run()
        val connectingPoints = ConnectingPoints(movesX, movesY)
        var connectedPoints = connectingPoints.connectPoints()
        var thickness = connectingPoints.getThickness()
        printPointsOfMutableList(connectedPoints, thickness)
    }

    /**
     * funkce vypise body z connectedPoints a priradi je do pole
     */
    private fun printPointsOfMutableList(connectedPoints: MutableList<Array<Short>>, thickness: MutableList<Array<Short>>)
    {
        var arr = arrayOf<Array<Short>>()
        for(y in (0..SQUARE_SIZE))
        {
            var helpArr = arrayOf<Short>()
            for(x in (0..SQUARE_SIZE))
            {
                helpArr += 0
            }
            arr += helpArr
        }
        for(i in 0..(connectedPoints[0].size - 1))
        {
            print("${connectedPoints[0][i]} ${connectedPoints[1][i]} / ")
            arr[connectedPoints[1][i].toInt()][connectedPoints[0][i].toInt()] = 1.toShort()
        }
        for(i in 0..(thickness[0].size - 1))
        {
            print("${thickness[0][i]} ${thickness[1][i]} / ")
            arr[thickness[1][i].toInt()][thickness[0][i].toInt()] = 1.toShort()
        }
        /*
        for(y in (0..SQUARE_SIZE))
        {
            for(x in (0..SQUARE_SIZE))
            {
                print("${arr[y][x]} ")
            }
            println()
        }*/
        var result: String
        for(i in 0..arr.size - 1)
        {
            result = ""
            for(j in 0..arr[i].size - 1)
            {
                result += arr[i][j].toString() + " "
            }
            Log.i("", result)
        }
    }
}