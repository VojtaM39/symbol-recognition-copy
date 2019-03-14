package com.example.symbolrecognition

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.Intent.getIntent
import android.util.Log
import 	android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.gesture.Gesture
import android.graphics.Point
import android.view.Display
import android.view.WindowManager
import android.widget.Toast


class DrawManager {
    private val drawViewHeight : Int
    private var pointsX : Array<Float>
    private var pointsY : Array<Float>
    private var pointsExtraX = arrayOf<Float>()
    private var pointsExtraY = arrayOf<Float>()
    private var pointsXResult : Array<Short>
    private var pointsYResult : Array<Short>
    private var pointsXExtraResult = arrayOf<Short>()
    private var pointsYExtraResult = arrayOf<Short>()
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
    private val lineDetector : LineDetector
    private val databaseTester : DatabaseTester
    private val evaulator : Evaulator
    private val caller : Caller
    constructor(pointsX:Array<Float>, pointsY : Array<Float>, touchCount : Int, endsOfMove : Array<Int>, context: Context, drawViewHeight : Int) {
        this.drawViewHeight = drawViewHeight
        this.context = context
        this.pointsX = pointsX
        this.pointsY = pointsY
        this.touchCount = touchCount
        this.endsOfMove = endsOfMove
        this.areaDivider = AreaDivider(pointsY, endsOfMove, drawViewHeight)
        if(this.endsOfMove.size == 1) {
            this.existsExtraSymbol = false
        }
        else {
            this.existsExtraSymbol = areaDivider.doesExistsExtraSymbol()
            if(this.existsExtraSymbol) {
                //Pokud existuje extra symbol, dame ho do zvlastniho pole a odstranime z hlavniho gesta
                this.pointsExtraX = createExtraPoints(pointsX)
                this.pointsExtraY = createExtraPoints(pointsY)
                this.pointsX = deleteLastMove(pointsX)
                this.pointsY = deleteLastMove(pointsY)
            }
        }
        processArrays(this.pointsX, this.pointsY)
        if(this.existsExtraSymbol) {
            processArrays(pointsExtraX, pointsExtraY)
        }

        this.pointsXResult = floatToShort(this.pointsX)
        this.pointsYResult = floatToShort(this.pointsY)
        if(this.existsExtraSymbol) {
            this.pointsXExtraResult = floatToShort(this.pointsExtraX)
            this.pointsYExtraResult = floatToShort(this.pointsExtraY)
        }
        this.movesX = generateMoves(pointsXResult, false)
        this.movesY = generateMoves(pointsYResult, false)
        if(this.existsExtraSymbol) {
            this.movesXExtra = generateMoves(pointsXExtraResult, true)
            this.movesYExtra = generateMoves(pointsYExtraResult, true)
        }
        this.dbManager = DbManager(this.context)
        this.directionsAlgorithm = DirectionsAlgorithm(movesX, movesY)
        this.databaseTester = DatabaseTester(context)
        this.lineDetector = LineDetector(movesX, movesY)
        this.evaulator = Evaulator(context, movesX, movesY, movesXExtra, movesYExtra)
        this.caller = Caller(context)
    }

    public fun getExistsExtraSymbol() : Boolean {
        return this.existsExtraSymbol
    }

  //Metoda vytvori MutableList ktere bude obsahovat pole s body jednotlivych tahu
    private fun generateMoves(points : Array<Short>, extra : Boolean) : MutableList<Array<Short>>{
      var moves = mutableListOf<Array<Short>>()
      var array = arrayOf<Short>()
      for(i in points.indices) {
          array += points[i]
          //Pokud je dane id posledni v tahu nebo posledni bod v celem poli (kvuli extra poli), vytvori se nove pole
          if((this.endsOfMove.contains(i) && !extra)  || (i == points.size-1 && extra)) {
              moves.add(array)
              array = arrayOf()
          }
      }
    return moves
    }

    //Pokud gesto bylo nalezeno, spusti akci, vraci truem, jinak vraci false
    public fun run() : Boolean {
        logMoves()
        var result : Long? = evaulator.run()
        val action = evaulator.getAction()

        if(result != null) {
            when(action) {
                Constants.ACTION_CONTACT -> caller.call(result)
                Constants.ACTION_CALL -> caller.call(result)
                Constants.ACTION_SMS -> caller.openSms(result)
            }
        }

        else {
            Toast.makeText(context, "Contact was not found.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun createExtraPoints(points : Array<Float>) : Array<Float> {
        var result = arrayOf<Float>()
        val lastIndex = endsOfMove[endsOfMove.size-2]
        for (i in lastIndex+1..points.size-1) {
            result+=points[i]
        }
        return result
    }

    private fun deleteLastMove(points: Array<Float>) : Array<Float> {
        var result = arrayOf<Float>()
        val lastIndex = endsOfMove[endsOfMove.size-2]
        for (i in 0..lastIndex) {
            result+=points[i]
        }
        return result
    }

    /**
     * Metoda se spusti, kdyz uzivatel zada gesto a chce vytvorit nove gesto
     *
     */
    public fun createGesture(contactId: Long) {
        var gestureId = insertGestureToDatabase(contactId)
        insertPointsToDatabase(gestureId)
        insertRatiosToDatabase(gestureId)
        insertLinesToDatabase(gestureId)
    }

    public fun updateGesture(gestureId: Long) {
        dbManager.deleteGesturesData(gestureId)
        insertPointsToDatabase(gestureId)
        insertRatiosToDatabase(gestureId)
        insertLinesToDatabase(gestureId)
    }

    public fun deleteGestureTest(gestureId: Long) {
        dbManager.delete(Constants.GESTURES_ID + " = ?", arrayOf(gestureId.toString()), Constants.GESTURES_TABLE)
    }

    private fun deleteByGestureId(gestureId: Long, table : String) {
        dbManager.delete("gesture_id=?",arrayOf(gestureId.toString()),table)
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

    private fun insertLinesToDatabase(gestureId: Long) {
        var lines = lineDetector.getLines()
        var values : ContentValues
        for(line in lines) {
            values = ContentValues()
            values.put(Constants.LINES_GESTURE_ID, gestureId)
            values.put(Constants.LINES_X1, line.x1)
            values.put(Constants.LINES_Y1, line.y1)
            values.put(Constants.LINES_X2, line.x2)
            values.put(Constants.LINES_Y2, line.y2)
            dbManager.insert(values, Constants.LINES_TABLE)
        }
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
    private fun processArrays(pointsX: Array<Float>, pointsY: Array<Float>) {
        Log.i("Pocet bodu", pointsX.size.toString())
        if((pointsX.max()!! - pointsX.min()!!) > (pointsY.max()!! - pointsY.min()!!)) {
            cropArrays(pointsX, pointsY)
        }
        else {
            cropArrays(pointsY, pointsX)
        }
    }


    /**
     * Metoda zvetsi/zmensi dane tahy na ctverec podle promenne SQUARE_SIZE
     */
    private fun resizeMovesHelper(movesBigger :MutableList<Array<Short>>, movesSmaller :MutableList<Array<Short>>) {
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
        Log.i("Draw View height", drawViewHeight.toString())
        var result : String
        for(i in 0..movesY.size-1) {
            result = ""
            for(j in 0..movesY[i].size-1) {
                result += movesY[i][j].toString() + ", "
            }
            Log.i("Y", result)
        }

        for(i in 0..movesX.size-1) {
            result = ""
            for(j in 0..movesX[i].size-1) {
                result += movesX[i][j].toString() + ", "
            }
            Log.i("X", result)
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