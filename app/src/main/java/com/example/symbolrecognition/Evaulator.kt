package com.example.symbolrecognition

import android.content.Context
import android.icu.util.UniversalTimeScale.toLong
import android.util.Log
import kotlin.math.absoluteValue

class Evaulator {
    private val context : Context
    private val dbManager : DbManager
    private val movesX : MutableList<Array<Short>> //hodnoty prave nakresleneho gesta
    private val movesY : MutableList<Array<Short>> //hodnoty prave nakresleneho gesta
    private val directionsAlgorithm : DirectionsAlgorithm
    private val lineDetector : LineDetector
    private var gestureMovesX = mutableListOf<Array<Short>>()
    private var gestureMovesY = mutableListOf<Array<Short>>()

    private val MAX_RATIO_DIFF = 0.2f
    constructor(context: Context, movesX : MutableList<Array<Short>>, movesY : MutableList<Array<Short>>) {
        this.context = context
        this.dbManager = DbManager(context)
        this.movesX = movesX
        this.movesY = movesY
        this.directionsAlgorithm = DirectionsAlgorithm(this.movesX, this.movesY)
        this.lineDetector = LineDetector(this.movesX, this.movesY)
    }
    public fun run() {
        //var LDValue = arrayOf<Float>()
        var DAValue = arrayOf<Float>()
        var ThicknessValue = arrayOf<Float>()
        var matchingGesturesIds = mutableListOf<Long>()

        //filtr pomoci directionsAlgorithmu
        var matchingGestures = getMatchingRatios()
        for(matchingGesture in matchingGestures)
        {
            DAValue += matchingGesture.result
            matchingGesturesIds.add(matchingGesture.id)
        }

        /*//lineDetector
        var linesResult = getLinesResult(matchingGesturesIds)
        for(lineResult in linesResult) {
            Log.i("Result", "Id: " + lineResult.id + " Result: " + lineResult.result)
            LDValue += lineResult.result
        }*/

        //thicknessAlgorithm
        /*
        var thicknessAlgorithmResults = getThicknessValues(movesX, movesY)
        for(thicknessAlgorithmResult in thicknessAlgorithmResults)
            ThicknessValue += thicknessAlgorithmResult.result
        */
    }

    /**
     * Vraci mutable list se vsemi ratios v db
     */
    private fun getAllRatios() : MutableList<RatioResult>{
        var result = mutableListOf<RatioResult>()
        var id : Long
        var ratioX : Float
        var ratioY : Float
        val cursor = dbManager.queryAll("Ratios")
        if (cursor != null) {
            cursor.moveToFirst()
            while (cursor.moveToNext()) {
                id = cursor.getString(cursor.getColumnIndex(Constants.RATIOS_GESTURE_ID)).toLong()
                ratioX = cursor.getString(cursor.getColumnIndex(Constants.RATIOS_X)).toFloat()
                ratioY = cursor.getString(cursor.getColumnIndex(Constants.RATIOS_Y)).toFloat()
                result.add(RatioResult(id,ratioX,ratioY))
            }
        }
        cursor.close()
        return result
    }

    /**
     * Vraci pole s indexy gest, ketre podle alogritmu DirectionsAlgorithm odpovidaji namalovanemu gestu
     */
    private fun getMatchingRatios() : MutableList<AlgorithmResult>{
        val ratios = getAllRatios()
        var result = mutableListOf<AlgorithmResult>()
        //Hodnoty prave namalovaneho gesta
        val ratioX = directionsAlgorithm.getXRatio()
        val ratioY = directionsAlgorithm.getYRatio()
        //cyklus prochazi vsechny gesta v databazi a hleda podobne
        for(i in ratios.indices) {
            if((ratios[i].ratioX - ratioX).absoluteValue < MAX_RATIO_DIFF && (ratios[i].ratioY - ratioY).absoluteValue < MAX_RATIO_DIFF) {
                var valueX = getDAValue(ratios[i].ratioX, ratioX)
                var valueY = getDAValue(ratios[i].ratioY, ratioY)
                var value: Float = (valueX + valueY) / 2
                result.add(AlgorithmResult(ratios[i].id, value))
            }
        }
        return result
    }

    /**
     * Vrati pomer vysledku directionsAlgorithmu
     */
    private fun getDAValue(ratio1: Float, ratio2: Float): Float
    {
        if(ratio1 >= ratio2)
            return ratio2 / ratio1
        else
            return ratio1 / ratio2
    }

    /**
     * Metoda vraci vsechny lines ktere prosli DirectionsAlgorithms
     */
    private fun getMatchingLines(matchingGestures : MutableList<Long>) : MutableList<LineResult> {
        var result = mutableListOf<LineResult>()
        var line : Line
        var id : Long
        var x1 : Short
        var y1 : Short
        var x2 : Short
        var y2 : Short
        //Pomocne mapa, do ktere jsou ulozeny jako key, id gesta a jako value mutable list s lines
        var linesHelper = mutableMapOf<Long, MutableList<Line>>()
        val cursor = dbManager.queryAll("Lines")
        if (cursor != null) {
            cursor.moveToFirst()
            while (cursor.moveToNext()) {

                id = cursor.getString(cursor.getColumnIndex("gesture_id")).toLong()
                if(matchingGestures.contains(id)) {
                    x1 = cursor.getString(cursor.getColumnIndex("x1")).toShort()
                    y1 = cursor.getString(cursor.getColumnIndex("y1")).toShort()
                    x2 = cursor.getString(cursor.getColumnIndex("x2")).toShort()
                    y2 = cursor.getString(cursor.getColumnIndex("y2")).toShort()
                    line = Line(x1,y1,x2,y2)
                    if(linesHelper.containsKey(id)) {
                        linesHelper.get(id)!!.add(line)
                    }
                    else {
                        linesHelper.put(id, mutableListOf(line))
                    }
                }
            }
        }
        cursor.close()
        for((id, list) in linesHelper) {
            result.add(LineResult(id, list))
        }
        return result
    }
    /**
     * Metoda prochazi gesta s temito id a hleda podobne podle LineDetector
     */
    private fun getLinesResult(matchingGestures : MutableList<Long>) : MutableList<AlgorithmResult>{
        var matchingLines = getMatchingLines(matchingGestures)
        val lines = lineDetector.getLines()
        var points = 0
        var maxPoints = 1
        var resultHelper = 0f
        var results = mutableListOf<AlgorithmResult>()
        for(i in matchingLines.indices) {
            if(lines.count() == matchingLines[i].lines.count()) {
                points+=1
            }
            resultHelper = (points/maxPoints).toFloat()
            results.add(AlgorithmResult(matchingLines[i].id, resultHelper))
        }
        return results
    }

    private fun getThicknessValues(movesX: MutableList<Array<Short>>, movesY: MutableList<Array<Short>>, matchingGesturesIds: MutableList<Long>)/*: MutableList<AlgorithmResult> */
    {
        //ziskat tloustku z prave nakresleneho gesta
        val connectingPoints = ConnectingPoints(movesX, movesY)
        var drewGesturePoints = connectingPoints.connectPoints()
        var drewGestureThickness = connectingPoints.getThickness()

        for(matchingGestureId in matchingGesturesIds)
        {
            getGestureFromDatabase(matchingGestureId)
            val connectingPoints = ConnectingPoints(gestureMovesX, gestureMovesY)
            var databaseGesturePoints = connectingPoints.connectPoints()
            var databaseGestureThickness = connectingPoints.getThickness()
        }
    }

    /**
     * funkce vrati gesto
     */
    private fun getGestureFromDatabase(gestureId: Long)
    {
        var dbManager = DbManager(context)
        val cursor = dbManager.queryAll(Constants.POINTS_TABLE)
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
                    gestureMovesX.add(moveNumber, arrMovesX)
                    gestureMovesY.add(moveNumber, arrMovesY)
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
        }
    }
}