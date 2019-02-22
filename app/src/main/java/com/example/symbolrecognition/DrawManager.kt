package com.example.symbolrecognition

import android.content.ContentValues
import android.content.Intent
import android.content.Intent.getIntent
import android.util.Log
import 	android.content.Context
import android.gesture.Gesture
import android.graphics.Point

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
    private val areaDivider : AreaDivider
    private var movesXExtra = mutableListOf<Array<Short>>()
    private var movesYExtra = mutableListOf<Array<Short>>()
    private var existsExtraSymbol : Boolean
    private val dbManager : DbManager
    private val context : Context
    private val directionsAlgorithm : DirectionsAlgorithm
    constructor(pointsX:Array<Float>, pointsY : Array<Float>, touchCount : Int, endsOfMove : Array<Int>, context: Context) {
        this.context = context
        this.pointsX = pointsX
        this.pointsY = pointsY
        this.touchCount = touchCount
        this.endsOfMove = endsOfMove
        processArrays()
        this.pointsXResult = floatToShort(this.pointsX)
        this.pointsYResult = floatToShort(this.pointsY)
        this.movesX = generateMoves(pointsXResult, endsOfMove)
        this.movesY = generateMoves(pointsYResult, endsOfMove)
        this.areaDivider = AreaDivider(movesX, movesY)
        this.existsExtraSymbol = areaDivider.doesExistsExtraSymbol()
        if(existsExtraSymbol) {
            this.movesXExtra = removeLastMove(movesX)
            this.movesYExtra = removeLastMove(movesY)
            resizeMoves()
        }
        this.dbManager = DbManager(this.context)
        directionsAlgorithm = DirectionsAlgorithm(movesX, movesY)
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

    public fun run() {
        logMoves()
        //runAlgorithms(movesX, movesY)
        //TODO vyhodnoceni i pro extra symbol
    }

    /**
     * Metoda se spusti, kdyz uzivatel zada gesto a chce vytvorit nove gesto
     *
     */
    public fun createGesture(name : String, phoneNumber : String) {
        var contactId = insertContactToDatabase(name, phoneNumber)
        var gestureId = insertGestureToDatabase(contactId)
        //TODO Pridavani lines
        insertPointsToDatabase(gestureId)
        insertRatiosToDatabase(gestureId)
    }


    private fun insertContactToDatabase(name : String, phoneNumber : String) : Long {
        var values = ContentValues()
        values.put("Name", name)
        values.put("PhoneNumber", phoneNumber)
        val contactId : Long = dbManager.insert(values, Constants.CONTACTS_TABLE)
        Log.i("Inserting","Contact inserted")
        return contactId
    }
    private fun insertGestureToDatabase(contactId : Long) : Long {
        var values = ContentValues()
        values.put("contact_id", contactId)
        val gestureId : Long = dbManager.insert(values, Constants.GESTURES_TABLE)
        Log.i("Inserting","Gesture inserted")
        return gestureId
    }

    private fun insertPointsToDatabase(gestureId : Long) {
        var values : ContentValues
        for(i in movesX.indices) {
            for(j in movesX[i].indices) {
                values = ContentValues()
                values.put("gesture_id", gestureId)
                values.put("move_number", i)
                values.put("point_x", movesX[i][j])
                values.put("point_y", movesY[i][j])
                dbManager.insert(values, Constants.POINTS_TABLE)
            }
        }
        Log.i("Inserting","Points inserted")
    }

    private fun insertRatiosToDatabase(gestureId : Long) {
        val xRatio = directionsAlgorithm.getXRatio()
        val yRatio = directionsAlgorithm.getYRatio()
        var values = ContentValues()
        values.put("gesture_id", gestureId)
        values.put("x_ratio", xRatio)
        values.put("y_ratio", yRatio)
        dbManager.insert(values, Constants.RATIOS_TABLE)
        Log.i("Inserting","Ratios inserted")
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
        Log.i("Pocet bodu", this.pointsX.size.toString())
        if((this.pointsX.max()!! - this.pointsX.min()!!) > (this.pointsY.max()!! - this.pointsY.min()!!)) {
            cropArrays(pointsX, pointsY)
        }
        else {
            cropArrays(pointsY, pointsX)
        }
    }

    /**
     * Metoda odtrhne posledni tah a vrati ho
     */

    private fun removeLastMove(moves : MutableList<Array<Short>>) : MutableList<Array<Short>> {
        var result = mutableListOf<Array<Short>>()
        result.add(moves[moves.size-1])
        moves.removeAt(moves.size-1)
        return result
    }
    /**
     * Metoda zvetsi/zmensi dane tahy na ctverec podle promenne SQUARE_SIZE
     */
    private fun resizeMovesHelper(movesBigger :MutableList<Array<Short>>, movesSmaller :MutableList<Array<Short>>) {
        //TODO dodelat predelani velikosti
        var biggerMax : Short = movesMax(movesBigger)
        var biggerMin : Short = movesMin(movesBigger)
        var smallerMin : Short = movesMin(movesSmaller)
        var smallerMax : Short = movesMax(movesSmaller)
        //Pomer ve kterem se bude obrazec menit
        val ratio = ((biggerMax - biggerMin).toFloat()/SQUARE_SIZE.toFloat()).toFloat()
        //Cyklus prochazi vsechny body, meni jejich pozici podle ratia a prirazi je ke zdi
        for(i in movesBigger.indices) {
            for(j in movesBigger[i].indices) {
                movesBigger[i][j] = (movesBigger[i][j]-biggerMin).toShort()
                movesSmaller[i][j] = (movesSmaller[i][j]-smallerMin).toShort()
                movesBigger[i][j] = (movesBigger[i][j]/ratio).toShort()
                movesSmaller[i][j] = (movesSmaller[i][j]/ratio).toShort()
            }
        }
        val shiftToCenter = ((SQUARE_SIZE - movesMax(movesSmaller))/2).toShort()
        for(i in movesBigger.indices) {
            for(j in movesBigger[i].indices) {
                movesSmaller[i][j] = (movesSmaller[i][j]+shiftToCenter).toShort()
            }
        }

    }

    private fun movesMin(moves:MutableList<Array<Short>>) : Short {
        var min : Short = 0
        for(i in moves.indices) {
            if(i==0) {
                min = moves[i].min()!!
            }
            else if(min > moves[i].min()!!) {
                min = moves[i].min()!!
            }
        }
        return min
    }

    private fun movesMax(moves:MutableList<Array<Short>>) : Short {
        var max : Short = 0
        for(i in moves.indices) {
            if(i==0) {
                max = moves[i].max()!!
            }
            else if(max < moves[i].max()!!) {
                max = moves[i].max()!!
            }
        }
        return max
    }
    private fun resizeMoves() {
        if((movesMax(movesX)-movesMin(movesX)) > (movesMax(movesY)-movesMin(movesY)))
            resizeMovesHelper(movesX, movesY)
        else
            resizeMovesHelper(movesY,movesX)
        if(movesXExtra.any()) {
            if((movesMax(movesXExtra)-movesMin(movesXExtra)) > (movesMax(movesYExtra)-movesMin(movesYExtra)))
                resizeMovesHelper(movesXExtra, movesYExtra)
            else
                resizeMovesHelper(movesYExtra,movesXExtra)
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
        for(i in 0..movesY.size-1) {
            result = ""
            for(j in 0..movesY[i].size-1) {
                result += movesY[i][j].toString() + ", "
            }
            Log.i("Tah:", result)
        }
        if(existsExtraSymbol) {
            Log.i("Extra","Extra symbol existuje")
        }
        for(i in 0..movesYExtra.size-1) {
            result = ""
            for(j in 0..movesYExtra[i].size-1) {
                result += movesYExtra[i][j].toString() + ", "
            }
            Log.i("Tah:", result)
        }
    }

    public fun  runAlgorithms(movesX :MutableList<Array<Short>>, movesY : MutableList<Array<Short>>) {


        logMoves()
       // var directionsAlgorithm = DirectionsAlgorithm(movesX,movesY)
       // directionsAlgorithm.run()
        val lineDetector = LineDetector(movesX,movesY)
        lineDetector.run()
       // val connectingPoints = ConnectingPoints(movesX, movesY)
       // var connectedPoints = connectingPoints.connectPoints()
       // var thickness = connectingPoints.getThickness()
       // printPointsOfMutableList(connectedPoints, thickness)
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