package com.example.symbolrecognition

import android.util.Log
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt
//TODO opravit kontrolu vzdalenosti pri vytvareni line (nesmi byt relativne k velikosti ctverce ale delce tahu)

class LineDetector {
    private var movesX = mutableListOf<Array<Short>>()
    private var movesY = mutableListOf<Array<Short>>()
    private var lines = mutableListOf<Line>()
    private val MAX_ANGLE_DIFF_MERGE = 0.2f
    //konstanta relativne urcena k velikosti ctverce, vyuziva se u mergeLines
    private val MAX_LINES_DISTANCE_DIFF = Constants.SQUARE_SIZE*20/100
    //konstanta je urcena relativne k velikosti ctverce
    private val MAX_SHIFT_DIFF = Constants.SQUARE_SIZE*20/100
    //pokud je ctverec 500x500, tak pri MINIMAL_SIDE_PERCANTAGE 20 musi byt cara dlouha aspon 100, aby byla povazovana za caru
    private val MINIMAL_SIDE_PERCANTAGE = 40
    private val MAX_RATIO_DIFF = 0.2f
    private val MINIMAL_SIDE_PERCANTAGE_FINAL = 100
    //pokud pujde cara vetsinu casu do jednoho smeru => neni cara
    constructor(movesX : MutableList<Array<Short>>, movesY : MutableList<Array<Short>>) {
        this.movesX = movesX
        this.movesY = movesY
        lines = createLines()
        mergeLines()
    }

    private fun createLines() : MutableList<Line>{
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

    public fun getLines() : MutableList<Line> {
        return lines
    }

    private fun logLines() {
        for(line in lines) {
            Log.i("Line", "Starting: " + line.x1.toString() + ", " + line.y1.toString() + ". Ending: " + line.x2.toString() + ", " + line.y2.toString() + ", Angle: " + line.angle.toString() + ", ShiftX: " + line.shiftCoefficientX.toString()+ ", ShiftY: " + line.shiftCoefficientY.toString())
        }
    }

    //Metoda bude prochazet lines a t   y lines ktere maji podobny smer a jsou blizko sebe bude slucovat
    private fun mergeLines() {
        //Lines ktere budou na konci smazany
        var toDelete = arrayOf<Int>()
        var mergedLines = mutableListOf<Line>()
        /**
         * cyklus prochazi vsechny lines a hleda podobne
         * Pokud jsou dostatecne podobne, tak je vytvori novou sloucenou caru a dve cary, ze kterych byla vytvorena se daji do pole na smazani
         * Prochazi se jen cary, ktere nebudou na konci smazany
         */
        for(i in lines.indices) {
            Log.i("Merge", "i")
            if(!toDelete.contains(i)) {
                for(j in i+1..lines.size-1) {
                    Log.i("Merge", "j")
                    logLinesIndexes()
                    if(testLines(lines[i], lines[j])) {
                        toDelete+=i
                        toDelete+=j
                        mergeThisLines(i,j)
                        logLinesIndexes()
                        break
                    }
                }
            }
        }
        //Vytvori se novy list, do ktereho se daji vsechny lines,ktere nemaji byt smazany
        for(i in lines.indices) {
            if(!toDelete.contains(i)) {
                mergedLines.add(lines[i])
            }
        }
        this.lines = mergedLines

    }

    private fun logLinesIndexes() {
        var result = ""
        for (i in lines.indices) {
            result+=i.toString() + ", "
        }
        Log.i("Lines indexes", result)
    }

    /**
     * Metoda dostava jako argument indexy lines, ktere jdou spojit
     * Odstrani je z listu lines a prida do listu lines nakonec jejich zprumerovanou verzi
     */
    private fun mergeThisLines(line1Index : Int, line2Index : Int) {
        val x1 : Short
        val y1 : Short
        val x2 : Short
        val y2 : Short
        var points = arrayOf<Array<Short>>()
        var linesLocal = mutableListOf<Line>()
        linesLocal.add(lines[line1Index])
        linesLocal.add(lines[line2Index])
        for(line in linesLocal) {
            points+=arrayOf<Short>(line.x1,line.y1)
            points+=arrayOf<Short>(line.x2,line.y2)
        }
        val furthestPoint = getFurthestPoints(points)
        x1 = points[furthestPoint[0]][0]
        y1 = points[furthestPoint[0]][1]
        x2 = points[furthestPoint[1]][0]
        y2 = points[furthestPoint[1]][1]
        Log.i("Merge", "Spojuji line, " + line1Index.toString() + " a line " + line2Index.toString())
        lines.add(Line(x1,y1,x2,y2))


    }

    /**
     * Metoda vraci true, pokud se tyto dve lines daji sloucit
     * Zkousi:
     * 1. Angle
     * 2. Shift
     * 3. Vzdalenost
     */

    private fun testLines(line1 : Line, line2 : Line) : Boolean {
        var result = false
        var shift1 : Short
        var shift2 : Short
        var overOneAngle = line1.angle.absoluteValue + MAX_ANGLE_DIFF_MERGE
        //Pokud je u horizontalnich car vykyv mirne pres osu x, tak potrebujeme osetrit, zda se jeste line ma sloucit
        var overOneAngleBool = false
        if(overOneAngle > 1) {
            overOneAngle = 1-(overOneAngle-1)
            if(line2.angle > overOneAngle) {
                Log.i("Horizontal Line Fix", "Did")
                overOneAngleBool = true
            }
        }

        //Lines maji podobny angle
        if ((line1.angle - line2.angle).absoluteValue < MAX_ANGLE_DIFF_MERGE || overOneAngleBool) {
             // Pokud je line1 spise vertikalni, pak testujeme shiftCoefficient na ose X, jinak na ose Y
            if(line1.angle.absoluteValue < 0.5f) {
                shift1 = line1.shiftCoefficientX
                shift2 = line2.shiftCoefficientX
            }
            else {
                shift1 = line1.shiftCoefficientY
                shift2 = line2.shiftCoefficientY
            }

             //Shift coefs jsou dostatecne male
            if((shift1-shift2).absoluteValue < MAX_SHIFT_DIFF) {
                /**
                 * Cary se prekryvaji
                 * hledame rozdil od kazdeho bodu, ke kazdemu bodu na obou osach
                 * Pokud je nejaky nasobek zaporny=>prekryvaji se, muzou se sloucit
                 */
                if(overlapCheck(line1,line2)) {
                    Log.i("Overlap","Prekryvaji se")
                    result = true
                }
                else if(getLinesDistance(line1, line2)<MAX_LINES_DISTANCE_DIFF){
                    Log.i("Overlap","Mala vzdalenost")
                    result = true
                }
            }
        }
        return result
    }

    /**
     * Pokud jsou tyto cary prekrizene, vraci true
     * jinak false
     */
    private fun overlapCheck(line1 : Line, line2 : Line) : Boolean {
        if(checkThisOverlaps(line1,line2) < 0 || checkThisOverlaps(line2,line1) < 0)
            return true
        else
            return false
    }

    /**
     * Metoda vraci nejmensi hodnotu z pole
     * Pokud jsou cary prekrizene, tak je tato hodnota zaporna
     *
     */
    private fun checkThisOverlaps(line1 : Line, line2 : Line) : Short {
        var overlapChecks = arrayOf<Short>()
        overlapChecks+=((line1.x1 - line2.x1) * (line1.x1 - line2.x2)).toShort()
        overlapChecks+=((line1.x2 - line2.x1) * (line1.x2 - line2.x2)).toShort()
        overlapChecks+=((line1.y1 - line2.y1) * (line1.y1 - line2.y2)).toShort()
        overlapChecks+=((line1.y2 - line2.y1) * (line1.y2 - line2.y2)).toShort()
        return overlapChecks.min()!!
    }

    /**
     * Metoda najde vzdalenost danych car
     * Bere v potaz dva nejblizsi krajni body
     * Prekryvani je vyreseno v jine metode
     */
    private fun getLinesDistance(line1 : Line, line2 : Line) : Short {
        var distances = arrayOf<Short>()
        distances += getDistance(line1.x1,line1.y1,line2.x1,line2.y1)
        distances += getDistance(line1.x2,line1.y2,line2.x1,line2.y1)
        distances += getDistance(line1.x1,line1.y1,line2.x2,line2.y2)
        distances += getDistance(line1.x2,line1.y2,line2.x2,line2.y2)
        return distances.min()!!
    }

    /**
     * Do metody se posilaji ctyri body, ktere tvori dane cary
     * Metoda vraci indexy dvou nejvzdalenejsich bodu
     */

    private fun getFurthestPoints(points : Array<Array<Short>>) : MutableList<Int>{
        var pointsIndexes = mutableListOf<Int>()
        var longestLenght : Short = 0
        var lenght : Short = 0
        for(i in points.indices) {
            for(j in i+1..points.size-1) {
                lenght = getDistance(points[i][0],points[i][1],points[j][0],points[j][1])
                if(lenght > longestLenght) {
                    longestLenght = lenght
                    pointsIndexes.clear()
                    pointsIndexes.add(i)
                    pointsIndexes.add(j)
                }
            }
        }
        return pointsIndexes
    }

    /**
     * Metoda vraci vzdalenost dvou bodu
     */
    private fun getDistance(x1 : Short,y1 : Short, x2 : Short, y2 :  Short) : Short {
        val distance = sqrt((x1-x2).toDouble().pow(2)+(y1-y2).toDouble().pow(2)).toShort()
        return distance
    }

    /**
     * Metoda smaze lines, ktere nejsou dostatecne dlouhe
     */
    private fun deleteShortLines() {
        //lines, ktere zustanou
        var keepLines = mutableListOf<Line>()
        //cyklus prochazi lines
        for(line in lines) {
            if(line.lenght > Constants.SQUARE_SIZE*MINIMAL_SIDE_PERCANTAGE_FINAL/100) {
                keepLines.add(line)
            }
        }
        this.lines = keepLines
    }

    public fun run() {
        logLines()
    }



}