package com.example.symbolrecognition

import android.content.Context
import android.widget.Toast
import java.lang.Math.pow
import java.lang.Math.sqrt
import kotlin.math.absoluteValue
import kotlin.math.pow

class Evaulator {
    private val context : Context
    private val dbManager : DbManager
    private val movesX : MutableList<Array<Short>> //hodnoty prave nakresleneho gesta
    private val movesY : MutableList<Array<Short>> //hodnoty prave nakresleneho gesta
    private val directionsAlgorithm : DirectionsAlgorithm
    private val lineDetector : LineDetector
    private var gestureMovesX = mutableListOf<Array<Short>>()
    private var gestureMovesY = mutableListOf<Array<Short>>()
    private var drewGesturePoints = mutableListOf<Array<Short>>()
    private var drewGestureThickness = mutableListOf<Array<Short>>()

    //directionsAlgorithm
    private val MAX_RATIO_DIFF = 0.2f
    //final decision
    private val directionsAlgorithmWeight = 0.35f
    private val thicknessAlgorithmWeight: Float = 1 - directionsAlgorithmWeight //0.65f
    private val minimalSimilarity = 0.8f

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
        var directionsAlgorithmValue = arrayOf<Float>()
        var thicknessAlgorithmValue = arrayOf<Float>()
        var matchingGesturesIds = mutableListOf<Long>()

        //filtr pomoci directionsAlgorithmu
        var matchingGestures = getMatchingRatios()
        for(matchingGesture in matchingGestures)
        {
            directionsAlgorithmValue += matchingGesture.result
            matchingGesturesIds.add(matchingGesture.id)
        }

        /*//lineDetector
        var linesResult = getLinesResult(matchingGesturesIds)
        for(lineResult in linesResult) {
            Log.i("Result", "Id: " + lineResult.id + " Result: " + lineResult.result)
            LDValue += lineResult.result
        }*/

        //thicknessAlgorithm
        var thicknessAlgorithmResults = getthicknessAlgorithmValues(matchingGesturesIds)
        for(thicknessAlgorithmResult in thicknessAlgorithmResults)
            thicknessAlgorithmValue += thicknessAlgorithmResult.result

        //vyber konecneho vysledku
        var result: Int? = finalDecision(matchingGesturesIds, directionsAlgorithmValue, thicknessAlgorithmValue)
        var finalResult: Long?
        if(result == null)
            finalResult = null
        else
            finalResult = matchingGesturesIds[finalDecision(matchingGesturesIds, directionsAlgorithmValue, thicknessAlgorithmValue)!!.toInt()]
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
                var valueX = getdirectionsAlgorithmValue(ratios[i].ratioX, ratioX)
                var valueY = getdirectionsAlgorithmValue(ratios[i].ratioY, ratioY)
                var value: Float = (valueX + valueY) / 2
                result.add(AlgorithmResult(ratios[i].id, value))
            }
        }
        return result
    }

    /**
     * Vrati pomer vysledku directionsAlgorithmu
     */
    private fun getdirectionsAlgorithmValue(ratio1: Float, ratio2: Float): Float
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

    private fun getthicknessAlgorithmValues(matchingGesturesIds: MutableList<Long>): MutableList<AlgorithmResult>
    {
        var finalResult = mutableListOf<AlgorithmResult>()
        var drewGestureLength = getGestureLength(movesX, movesY)
        var alreadyExistsDrewGestureThickness = false

        for(matchingGestureId in matchingGesturesIds)
        {
            var result: AlgorithmResult
            getGestureFromDatabase(matchingGestureId)
            var databaseGestureLength = getGestureLength(gestureMovesX, gestureMovesY)

            if(drewGestureLength >= databaseGestureLength) //vetsi je prave nakreslene gesto
            {
                val connectingPoints = ConnectingPoints(gestureMovesX, gestureMovesY)
                var databaseGesturePoints = connectingPoints.connectPoints()
                var databaseGestureThickness = connectingPoints.getThickness()

                result = AlgorithmResult(matchingGestureId, getRatioOfContainedPoints(movesX, movesY, databaseGesturePoints, databaseGestureThickness))
            }
            else //vetsi je gesto z databaze
            {
                if(!alreadyExistsDrewGestureThickness)
                {
                    //ziskat tloustku z prave nakresleneho gesta
                    val connectingPoints = ConnectingPoints(movesX, movesY)
                    drewGesturePoints = connectingPoints.connectPoints()
                    drewGestureThickness = connectingPoints.getThickness()
                    alreadyExistsDrewGestureThickness = true
                }
                result = AlgorithmResult(matchingGestureId, getRatioOfContainedPoints(gestureMovesX, gestureMovesY, drewGesturePoints, drewGestureThickness))
            }
            //ulozeni do finalResult
            finalResult.add(result)
        }
        return finalResult
    }

    /**
     * funkce vrati gesto
     */
    private fun getGestureFromDatabase(gestureId: Long)
    {
        var dbManager = DbManager(context)
        var where = "${Constants.POINTS_GESTURE_ID} = $gestureId"
        val cursor = dbManager.queryWithWhere(Constants.POINTS_TABLE, where) //upravit
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

    /**
     * metoda ziska konecnou vzdalenost mezi body
     */
    private fun getGestureLength(movesX: MutableList<Array<Short>>, movesY: MutableList<Array<Short>>): Float
    {
        var result: Float = 0f

        //pythagorova veta
        for(i in 0..(movesX.size - 1))
            for(j in 0..(movesX[i].size - 1 - 1)) //prvni -1 pricitame kvuli tomu, ze se pohybujeme vezi indexy a druhe -1 kvuli tomu, ze posledni bod uz neni s cim spojit
                result += (sqrt((movesX[i][j] - movesX[i][j + 1]).absoluteValue.toDouble()) + sqrt((movesY[i][j] - movesY[i][j + 1]).absoluteValue.toDouble())).pow(2).toFloat()
        return result
    }

    private fun getRatioOfContainedPoints(movesX: MutableList<Array<Short>>, movesY: MutableList<Array<Short>>, connectedPoints: MutableList<Array<Short>>, thickness: MutableList<Array<Short>>): Float
    {
        var contains: Int = 0
        var points: Int = 0

        var arr = arrayOf<Array<Int>>()
        for(y in (0..Constants.SQUARE_SIZE))
        {
            var helpArr = arrayOf<Int>()
            for(x in (0..Constants.SQUARE_SIZE))
                helpArr += 0
            arr += helpArr
        }
        for(i in 0..(connectedPoints[0].size - 1))
            arr[connectedPoints[1][i].toInt()][connectedPoints[0][i].toInt()] = 1
        for(i in 0..(thickness[0].size - 1))
            arr[thickness[1][i].toInt()][thickness[0][i].toInt()] = 1

        for(i in 0..(movesX.size - 1))
        {
            for (j in 0..(movesX[i].size - 1))
            {
                if (arr[movesY[i][j].toInt()][movesX[i][j].toInt()] == 1)
                {
                    contains++
                }
                points++
            }
        }

        return (contains.toFloat() / points)
    }

    private fun finalDecision(ids: MutableList<Long>, directionsAlgorithmValue: Array<Float>, thicknessAlgorithmValue: Array<Float>): Int?
    {
        var mostSimilarIndex: Int = 0
        var mostSimilarValue: Float = (directionsAlgorithmValue[mostSimilarIndex] * directionsAlgorithmWeight) + (thicknessAlgorithmValue[mostSimilarIndex] * thicknessAlgorithmWeight)
        if(ids.size > 1)
        {
            for (currentIndex in 1..(ids.size - 1))
            {
                var currentValue: Float = (directionsAlgorithmValue[currentIndex] * directionsAlgorithmWeight) + (thicknessAlgorithmValue[currentIndex] * thicknessAlgorithmWeight)
                if(currentValue > mostSimilarValue)
                {
                    mostSimilarValue = currentValue
                    mostSimilarIndex = currentIndex
                }
                else if(currentValue == mostSimilarValue)
                {
                    if (thicknessAlgorithmValue[currentIndex] > thicknessAlgorithmValue[mostSimilarIndex])
                    {
                        mostSimilarValue = currentValue
                        mostSimilarIndex = currentIndex
                    }
                }
            }
        }
        //rozhodnuti, zda dosahuje vysledek dostacujici hodnoty
        if(mostSimilarValue < minimalSimilarity)
            return null
        return mostSimilarIndex
    }
}