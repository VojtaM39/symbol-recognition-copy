package com.example.symbolrecognition

import android.util.Log
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt
//TODO overit ze se nezmenila line v pravem uhlu (stejny pomer, takze by pokracovala)
//TODO dodelat angle
//TODO dodelat slucovani lines
//TODO zpravit overeni vzdalenosti line (pred vytvorenim)
class LineDetector {
    private var pointsX : Array<Short>
    private var pointsY : Array<Short>
    private var movesX = mutableListOf<Array<Short>>()
    private var movesY = mutableListOf<Array<Short>>()
    private var lines = mutableListOf<Line>()
    //pokud je ctverec 500x500, tak pri MINIMAL_SIDE_PERCANTAGE 20 musi byt cara dlouha aspon 100, aby byla povazovana za caru
    private val MINIMAL_SIDE_PERCANTAGE = 20
    private val MAX_RATIO_DIFF = 0.1f
    constructor(pointsX:Array<Short>, pointsY : Array<Short>, touchCount : Int, movesX : MutableList<Array<Short>>, movesY : MutableList<Array<Short>>) {
        this.pointsX = pointsX
        this.pointsY = pointsY
        this.movesX = movesX
        this.movesY = movesY
        lines = getLines()
    }

    private fun getLines() : MutableList<Line>{
        //Algoritmus prochazi vsechny body
        //Zapise si vzdycky startingRatio dvou bodu a dokud je ratio dalsich bodu dost podobne, tak prodluzuje line
        //dokud nenajde velkou zmenu v ratio
        //pokud je line dost dlouha zapise se
        //ratio se predela na angle

        //ratio X/Lenght prnvnich dvou bodu v kazde line
        var lines = mutableListOf<Line>()
        var startingRatio = 0f
        var ratioSum = 0f
        var currentRatio = 0f
        var currentLineLenght = 0f
        var currentLinePointsCount : Short = 0
        var currentPointLenght = 0f
        var startingX:Short = 0
        var startingY:Short = 0
        var endingX:Short = 0
        var endingY:Short = 0
        //cyklus projizdi jednotlive tahy
        for(i in movesX.indices) {
            //cyklus projizdi jednotlive body
            for(j in movesX[i].indices) {
                //prvni bod neresime
                if(j!=0) {
                    currentRatio = (movesX[i][j] - movesX[i][j-1]).absoluteValue/sqrt((movesX[i][j] - movesX[i][j-1]).toDouble().pow(2) + (movesY[i][j] - movesY[i][j-1]).toDouble().pow(2)).toFloat()
                    currentPointLenght = sqrt((movesX[i][j] - movesX[i][j-1]).toDouble().pow(2) + (movesY[i][j] - movesY[i][j-1]).toDouble().pow(2)).toFloat()
                    //ratio je moc velke => zakladame novou line
                    //Druhy bod => prvni dvojice => vzdy zapisujem novou linu
                    //posledni bod => musime ulozit pripadnou line
                    //zalozeni nove line
                    if(j==1 || (currentRatio-startingRatio).absoluteValue > MAX_RATIO_DIFF || j == movesX[i].size) {
                        //pokud byla predesla line dostatecne dlouha, vytvorime novou line do listu
                        if(currentLineLenght > MINIMAL_SIDE_PERCANTAGE) {
                            lines.add(Line(0,startingX,startingY,endingX,endingY))
                        }
                        //Nova Line
                        Log.i("Line Debug", "Nova Line")
                        startingX = movesX[i][j-1]
                        startingY = movesY[i][j-1]
                        ratioSum = currentRatio
                        currentLineLenght = currentPointLenght
                        currentLinePointsCount = 1
                        startingRatio = currentRatio
                        Log.i("SR", startingRatio.toString())
                    }
                    //Pokracujeme v line
                    else {
                        Log.i("Line Debug", "Pokracujem v line")
                        Log.i("CR", currentRatio.toString())
                        currentLinePointsCount++
                        ratioSum += currentRatio
                        currentLineLenght += currentPointLenght
                        endingX = movesX[i][j]
                        endingY = movesY[i][j]
                    }
                }
            }
        }
        return lines
    }

    private fun logLines() {
        for(line in lines) {
            Log.i("Line", "Starting: " + line.point1X.toString() + ", " + line.point1Y.toString() + ". Ending: " + line.point2X.toString() + ", " + line.point2Y.toString())
        }
    }

    public fun run() {
        logLines()
    }



}