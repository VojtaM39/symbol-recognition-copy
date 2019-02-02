package com.example.symbolrecognition

import android.util.Log
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt
//TODO dodelat slucovani lines
//TODO opravit overeni vzdalenosti line (pred vytvorenim)
class LineDetector {
    private var pointsX : Array<Short>
    private var pointsY : Array<Short>
    private var movesX = mutableListOf<Array<Short>>()
    private var movesY = mutableListOf<Array<Short>>()
    private var lines = mutableListOf<Line>()
    private val MAX_ANGLE_DIFF_MERGE = 0.2f
    //konstanta je urcena relativne k velikosti ctverce
    private val MAX_SHIFT_DIFF = Constants.SQUARE_SIZE*20/100
    //pokud je ctverec 500x500, tak pri MINIMAL_SIDE_PERCANTAGE 20 musi byt cara dlouha aspon 100, aby byla povazovana za caru
    private val MINIMAL_SIDE_PERCANTAGE = 40
    private val MAX_RATIO_DIFF = 0.2f
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
        var alreadyCreated = false
        //cyklus projizdi jednotlive tahy
        for(i in movesX.indices) {
            //cyklus projizdi jednotlive body
            for(j in movesX[i].indices) {
                //prvni bod neresime
                if(j!=0) {
                    Log.i("Current line lenght", currentLineLenght.toString())
                    currentRatio = (movesX[i][j] - movesX[i][j-1]).absoluteValue/sqrt((movesX[i][j] - movesX[i][j-1]).toDouble().pow(2) + (movesY[i][j] - movesY[i][j-1]).toDouble().pow(2)).toFloat()
                    currentPointLenght = sqrt((movesX[i][j] - movesX[i][j-1]).toDouble().pow(2) + (movesY[i][j] - movesY[i][j-1]).toDouble().pow(2)).toFloat()
                    //ratio je moc velke => zakladame novou line
                    //Druhy bod => prvni dvojice => vzdy zapisujem novou linu
                    //posledni bod => musime ulozit pripadnou line
                    //zalozeni nove line
                    if(j==1 || (currentRatio-startingRatio).absoluteValue > MAX_RATIO_DIFF || j == movesX[i].size-1) {
                        //pokud byla predesla line dostatecne dlouha, vytvorime novou line do listu
                        if(currentLineLenght > (MINIMAL_SIDE_PERCANTAGE*Constants.SQUARE_SIZE/100)) {
                            endingX = movesX[i][j]
                            endingY = movesY[i][j]
                            lines.add(Line(startingX,startingY,endingX,endingY))
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
                    }


                }
                alreadyCreated = false
            }
        }
        return lines
    }

    private fun logLines() {
        for(line in lines) {
            Log.i("Line", "Starting: " + line.x1.toString() + ", " + line.y1.toString() + ". Ending: " + line.x2.toString() + ", " + line.y2.toString() + ", Angle: " + line.angle.toString() + ", ShiftX: " + line.shiftCoefficientX.toString()+ ", ShiftY: " + line.shiftCoefficientY.toString())
        }
    }

    //Metoda bude prochazet lines a ty lines ktere maji podobny smer a jsou blizko sebe bude slucovat
    private fun mergeLines() {
        /**
         * cyklus prochazi vsechny lines a hleda podobne
         */
        for(i in lines.indices) {
            for(j in i..lines.size-1) {

            }
        }
    }

    /**
     * Metoda vraci true, pokud se tyto dve lines daji sloucit
     * Zkousi:
     * 1. Angle
     * 2. Shift
     * 3. Vzdalenost
     */
    /**
    private fun testLines(line1 : Line, line2 : Line) : Boolean {
        var result = false
        var shift1 : Short
        var shift2 : Short
        //Lines maji podobny angle
        if ((line1.angle - line2.angle).absoluteValue < MAX_ANGLE_DIFF_MERGE) {
            /**
             * Pokud je line1 spise vertikalni, pak testujeme shiftCoefficient na ose X, jinak na ose Y
             */
            if(line1.angle.absoluteValue < 0.5f) {
                shift1 = line1.shiftCoefficientX
                shift2 = line2.shiftCoefficientX
            }
            else {
                shift1 = line1.shiftCoefficientY
                shift2 = line2.shiftCoefficientY
            }
            /**
             * Shift coefs jsou dostatecne male
             */
            if((shift1-shift2).absoluteValue < MAX_SHIFT_DIFF) {
                //TODO dodelat test vzdalenosti

            }
        }
    }
*/



    public fun run() {
        logLines()
    }



}