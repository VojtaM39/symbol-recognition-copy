package com.example.symbolrecognition

import android.content.Context
import android.icu.util.UniversalTimeScale.toLong
import android.util.Log
import kotlin.math.absoluteValue

class Evaulator {
    private val context : Context
    private val dbManager : DbManager
    private val movesX : MutableList<Array<Short>>
    private val movesY : MutableList<Array<Short>>
    private val directionsAlgorithm : DirectionsAlgorithm
    private val lineDetector : LineDetector

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
        var matchingGestures = getMatchingRatios()
        var linesResult = getLinesResult(matchingGestures)
        for(lineResult in linesResult) {
            Log.i("Result", "Id: " + lineResult.id + " Result: " + lineResult.result)
        }

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
    private fun getMatchingRatios() : MutableList<Long>{
        val ratios = getAllRatios()
        var result = mutableListOf<Long>()
        //Hodnoty prave namalovaneho gesta
        val ratioX = directionsAlgorithm.getXRatio()
        val ratioY = directionsAlgorithm.getYRatio()
        //cyklus prochazi vsechny gesta v databazi a hleda podobne
        for(i in ratios.indices) {
            if((ratios[i].ratioX - ratioX).absoluteValue < MAX_RATIO_DIFF && (ratios[i].ratioY - ratioY).absoluteValue < MAX_RATIO_DIFF) {
                result.add(ratios[i].id)
            }
        }
        return result
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
    private fun finalResult(): Int
    {
        return 1
    }
}