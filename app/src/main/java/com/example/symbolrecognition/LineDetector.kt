package com.example.symbolrecognition

import android.util.Log
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt
//TODO opravit spatne zjistovani prekryvani car
//TODO opravit kontrolu vzdalenosti pri vytvareni line (nesmi byt relativne k velikosti ctverce ale delce tahu)

class LineDetector {
    private var pointsX : Array<Short>
    private var pointsY : Array<Short>
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
        val furthestPoints : Byte = getFurthestPoints(lines[line1Index],lines[line2Index])
        if(furthestPoints == 1.toByte()) {
            x1 = lines[line1Index].x1
            y1 = lines[line1Index].y1
            x2 = lines[line2Index].x1
            y2 = lines[line2Index].y1
        }

        else if(furthestPoints == 2.toByte()) {
            x1 = lines[line1Index].x1
            y1 = lines[line1Index].y1
            x2 = lines[line2Index].x2
            y2 = lines[line2Index].y2
        }

        else if(furthestPoints == 3.toByte()) {
            x1 = lines[line1Index].x2
            y1 = lines[line1Index].y2
            x2 = lines[line2Index].x1
            y2 = lines[line2Index].y1
        }

        else {
            x1 = lines[line1Index].x2
            y1 = lines[line1Index].y2
            x2 = lines[line2Index].x2
            y2 = lines[line2Index].y2
        }
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
        //Lines maji podobny angle
        //TODO u horizontalnich car dodelat, aby se bral i angle s opacnym znamenkem napr. u cary 0.95 i angle -0.95
        if ((line1.angle - line2.angle).absoluteValue < MAX_ANGLE_DIFF_MERGE) {
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
        var overlapChecks = arrayOf<Short>()
        overlapChecks+=((line1.x1 - line2.x1) * (line1.x1 - line2.x2)).toShort()
        overlapChecks+=((line1.x2 - line2.x1) * (line1.x2 - line2.x2)).toShort()
        overlapChecks+=((line1.y1 - line2.y1) * (line1.y1 - line2.y2)).toShort()
        overlapChecks+=((line1.y2 - line2.y1) * (line1.y2 - line2.y2)).toShort()
        if(overlapChecks.min()!! < 0)
            return true
        else
            return false
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
     * Metoda najde nejvzdalenejsi body dvou car
     * Vraci Byte
     * Line1/Line2
     * 1 - 1/1
     * 2 - 1/2
     * 3 - 2/1
     * 4 - 2/2
     */

    private fun getFurthestPoints(line1 : Line, line2 : Line) : Byte{
        var distances = arrayOf<Short>()
        distances += getDistance(line1.x1,line1.y1,line2.x1,line2.y1)
        distances += getDistance(line1.x2,line1.y2,line2.x1,line2.y1)
        distances += getDistance(line1.x1,line1.y1,line2.x2,line2.y2)
        distances += getDistance(line1.x2,line1.y2,line2.x2,line2.y2)
        return (distances.indexOf(distances.max()!!)  + 1).toByte()
    }

    /**
     * Metoda vraci vzdalenost dvou bodu
     */
    private fun getDistance(x1 : Short,y1 : Short, x2 : Short, y2 :  Short) : Short {
        val distance = sqrt((x1-x2).toDouble().pow(2)+(y1-y2).toDouble().pow(2)).toShort()
        return distance
    }


    public fun run() {
        logLines()
        mergeLines()
        logLines()
    }



}