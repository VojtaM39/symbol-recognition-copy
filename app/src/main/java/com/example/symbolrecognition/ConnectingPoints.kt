package com.example.symbolrecognition

import kotlin.math.abs
import kotlin.math.truncate
import kotlin.math.round

class ConnectingPoints
{
    private var movesX = mutableListOf<Array<Short>>()
    private var movesY = mutableListOf<Array<Short>>()
    private var connectedPoints = mutableListOf<Array<Short>>()

    constructor(movesX: MutableList<Array<Short>>, movesY: MutableList<Array<Short>>)
    {
        this.movesX = movesX
        this.movesY = movesY
        this.connectedPoints = connectAllPoints(movesX, movesY)
    }

    fun connectPoints(): MutableList<Array<Short>> //public
    {
        return connectedPoints
    }

    /**
     * metoda spojujici body
     * vystupem je dvojrozmerne pole se spojenymi body
     * */
    private fun connectAllPoints(movesX: MutableList<Array<Short>>, movesY: MutableList<Array<Short>>): MutableList<Array<Short>>
    {
        var connectedPoints = mutableListOf<Array<Short>>() //connectedPoints[0] - x souradnice, [1] - y souradnice
        var connectedTwoPoints = arrayOf<Array<Short>>() //connectedTwoPoints[0] - x souradnice, [1] - y souradnice
        var helpArrayX = arrayOf<Short>() //pomocne pole do ktereho budeme pridavat pole bodu po vygenerovani
        var helpArrayY = arrayOf<Short>() //pomocne pole do ktereho budeme pridavat pole bodu po vygenerovani

        for (i in 0..(movesX.size - 1)) //projede vsechny cary
        {
            if (movesX[i].size == 1 ) //pokud se v poli nachazi 1 bod, vrat toto pole
            {
                connectedPoints[0] += movesX[i]
                connectedPoints[1] += movesY[i]
            }
            else
            {
                for (j in 0..(movesX[i].size - 1 - 1)) //-1 za delku pole a -1 protoze posledni souradnici uz nemame s cim spojovat
                {
                    if (movesX[i][j] <= movesX[i][j + 1]) //aktualni movesX je vice vlevo
                        connectedTwoPoints = connectThesePoints(movesX[i][j], movesY[i][j], movesX[i][j + 1], movesY[i][j + 1]) //jako prvni posilame ten bod, ktery je vice vlevo
                    else //nasledujici movesX je vice vlevo
                        connectedTwoPoints = connectThesePoints(movesX[i][j + 1], movesY[i][j + 1], movesX[i][j], movesY[i][j]) //jako prvni posilame ten bod, ktery je vice vpravo
                    helpArrayX += connectedTwoPoints[0]
                    helpArrayY += connectedTwoPoints[1]
                }
            }
        }
        connectedPoints.add(0, helpArrayX) //pridame body pro X
        connectedPoints.add(1, helpArrayY) //pridame body pro Y
        return connectedPoints
    }

    /**
     * spojuje konkretni 2 body
     * hodnoty vraci ve dvourozmernem poli [0] - x souradnice, [1] - y souradnice
     */
    private fun connectThesePoints(leftPointX: Short, leftPointY: Short, rightPointX: Short, rightPointY: Short) : Array<Array<Short>>
    {
        var biggerLengthOfX: Boolean //vetsi vzdalenost je mezi vzdalenostmi x nez y
        var ascending: Boolean //kdyz jdeme z leva do prava, tak funkce stoupa
        var numberOfElements: Double //pocet bodu, ktere musime minimalne doplnit do kazdeho
        var numberOfAdditionalElements: Int //pocet bodu, ktere musime navic doplnit

        var lengthX = rightPointX - leftPointX + 1 //+1 nam da delku vcetne bodu
        var lengthY = abs(rightPointY - leftPointY) + 1 //+1 nam da delku vcetne bodu

        //obecna rovnice - hodnoty a, b, c
        val a = rightPointY - leftPointY
        val b = -(rightPointX - leftPointX)
        val c = -(a * leftPointX + b * leftPointY)

        ascending = (rightPointY < leftPointY) //stoupani

        //dosadime podle delky os funkce
        if (lengthX > lengthY) //delsi x
        {
            biggerLengthOfX = true
            numberOfElements = truncate(lengthX.toDouble() / lengthY)
            numberOfAdditionalElements = lengthX % lengthY
            return addNewPoints(leftPointX, leftPointY, rightPointX, rightPointY, numberOfElements.toInt(), numberOfAdditionalElements, biggerLengthOfX, a, b, c, ascending)
        }
        else //delsi y
        {
            biggerLengthOfX = false
            numberOfElements = truncate(lengthY.toDouble() / lengthX)
            numberOfAdditionalElements = lengthY % lengthX
            return addNewPoints(leftPointY, leftPointX, rightPointY, rightPointX, numberOfElements.toInt(), numberOfAdditionalElements, biggerLengthOfX, a, b, c, ascending)
        }
    }

    /**
     * funkce vyplni mezery mezi body
     */
    private fun addNewPoints(leftPointLongerAxis: Short, leftPointShorterAxis: Short, rightPointLongerAxis: Short, rightPointShorterAxis: Short, numberOfElements: Int, numberOfAdditionalElements: Int, biggerLengthOfX: Boolean, a: Int, b: Int, c: Int, ascending: Boolean) : Array<Array<Short>>
    {
        //vzdy zaciname zleva
        var connectedTwoPoints = arrayOf<Array<Short>>()

        var distance: Int
        if((ascending) && (!biggerLengthOfX))
            distance = (numberOfElements * -1) + 1
        else
            distance = numberOfElements - 1

        //prvotni vyplneni pole, abychom jej pote mohli prepsat
        if((!biggerLengthOfX) && (ascending)) //podminky, kdy funkce klesa
        {
            for(i in 0..1)
            {
                var helpArray = arrayOf<Short>()
                for (j in 0..(leftPointLongerAxis - rightPointLongerAxis))
                    helpArray += 0
                connectedTwoPoints += helpArray
            }
        }
        else
        {
            for(i in 0..1)
            {
                var helpArray = arrayOf<Short>()
                for (j in 0..(rightPointLongerAxis - leftPointLongerAxis))
                    helpArray += 0
                connectedTwoPoints += helpArray
            }
        }

        var index = 0
        var startingPointLongerAxis = leftPointLongerAxis.toInt()
        var forStartingPointLongerAxis: Int
        var addedAdditionalElements = 0
        var startingPointShorterAxis = leftPointShorterAxis.toInt()
        var endingPointShorterAxis = rightPointShorterAxis.toInt()

        for(i in startingPointShorterAxis toward endingPointShorterAxis)
        {
            forStartingPointLongerAxis = startingPointLongerAxis //oprava for cyklu, otestovat doma
            for (j in forStartingPointLongerAxis toward (forStartingPointLongerAxis + distance))
            {
                if (biggerLengthOfX)
                {
                    connectedTwoPoints[0][index] = j.toShort() //x
                    connectedTwoPoints[1][index] = i.toShort() //y
                }
                else //biggerLengthOfX == false
                {
                    connectedTwoPoints[0][index] = i.toShort() //x
                    connectedTwoPoints[1][index] = j.toShort() //y
                }
                index++
                if((ascending) && (!biggerLengthOfX)) //podminky, kdy funkce klesa
                    startingPointLongerAxis--
                else
                    startingPointLongerAxis++
            }
            if ((numberOfAdditionalElements != 0) && (addedAdditionalElements < numberOfAdditionalElements))
            {
                if(biggerLengthOfX)
                {
                    if(addAdditionalPoint(a, b, c, startingPointLongerAxis, i))
                    {
                        connectedTwoPoints[0][index] = startingPointLongerAxis.toShort() //x
                        connectedTwoPoints[1][index] = i.toShort() //y
                        index++
                        addedAdditionalElements++
                        startingPointLongerAxis++
                    }
                }
                else //biggerLengthOfX == false
                {
                    if(addAdditionalPoint(a, b, c, i, startingPointLongerAxis))
                    {
                        connectedTwoPoints[0][index] = i.toShort() //x
                        connectedTwoPoints[1][index] = startingPointLongerAxis.toShort() //y
                        index++
                        addedAdditionalElements++
                        startingPointLongerAxis--
                    }
                }
            }
        }
        return connectedTwoPoints
    }
    private fun addAdditionalPoint(a: Int, b: Int, c: Int, actualX: Int, actualY: Int) : Boolean
    {
        var y: Double = round((a * actualX + c).toDouble() / -b)
        return y == actualY.toDouble() //vrati true, pokud se vypoctene Y rovna aktualnimu Y
    }

    /**
     * funkce, ktera dokaze vest cyklus for smerem od mensiho cisla k vetsimu, tak take od vetsiho k mensimu
     */
    private infix fun Int.toward(to: Int): IntProgression
    {
        val step = if (this > to) -1 else 1
        return IntProgression.fromClosedRange(this, to, step)
    }
}