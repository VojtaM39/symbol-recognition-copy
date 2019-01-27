package com.example.symbolrecognition

import kotlin.math.abs
import kotlin.math.truncate

class ConnectingPoints
{
    private var movesX = mutableListOf<Array<Short>>()
    private var movesY = mutableListOf<Array<Short>>()
    private var connectedPoints: Array<Array<Short>>

    constructor(movesX: MutableList<Array<Short>>, movesY: MutableList<Array<Short>>)
    {
        this.movesX = movesX
        this.movesY = movesY
        this.connectedPoints = connectAllPoints(movesX, movesY)
    }

    fun connectPoints(): Array<Array<Short>> //public
    {
        return connectedPoints
    }

    /**
     * metoda spojujici body
     * vystupem je dvojrozmerne pole se spojenymi body
     * */
    private fun connectAllPoints(movesX: MutableList<Array<Short>>, movesY: MutableList<Array<Short>>): Array<Array<Short>>
    {
        var connectedPoints = ArrayOf<Array<Short>>() //connectedPoints[0] - x souradnice, [1] - y souradnice
        var connectedTwoPoints = ArrayOf<Array<Short>>() //connectedTwoPoints[0] - x souradnice, [1] - y souradnice

        for (i in 0..movesX.size - 1)
        {
            for (j in 0..movesX[i].size - 1 - 1) //-1 za delku pole a -1 protoze posledni souradnici uz nemame s cim spojovat
            {
                if (movesX[i][j] <= movesX[i][j + 1]) //aktualni movesX je vice vlevo
                    connectedTwoPoints = connectThesePoints(movesX[i][j], movesY[i][j], movesX[i][j + 1], movesY[i][j + 1])
                else //nasledujici movesX je vice vlevo
                    connectedTwoPoints = connectThesePoints(movesX[i][j + 1], movesY[i][j + 1], movesX[i][j], movesY[i][j])
            }
        }
        //zde vlozit hodnoty z connectedTwoPoints do connectedPoints
        return connectedPoints
    }

    /**
     * spojuje konkretni 2 body
     * hodnoty vraci ve dvourozmernem poli [0] - x souradnice, [1] - y souradnice
     */
    private fun connectThesePoints(leftPointX: Short, leftPointY: Short, rightPointX: Short, rightPointY: Short) : Array<Array<Short>>
    {
        //pojistit, aby metoda fungovala i pro x = y
        var biggerLengthOfX: Boolean
        var numberOfElements: Double
        var numberOfAdditionalElements: Int

        var lengthX = rightPointX - leftPointX + 1 //+1 nam da delku vcetne bodu
        var lengthY = abs(rightPointY - leftPointY) + 1 //+1 nam da delku vcetne bodu

        numberOfElements = truncate(lengthY.toDouble() / lengthX)
        numberOfAdditionalElements = lengthY % lengthX

        if (lengthX > lengthY) //delsi x
        {
            biggerLengthOfX = true
            return addNewPoints(leftPointX, leftPointY, rightPointX, rightPointY, numberOfElements.toInt(), numberOfAdditionalElements, biggerLengthOfX)
        }
        else //delsi y
        {
            biggerLengthOfX = false
            return addNewPoints(leftPointY, leftPointX, rightPointY, rightPointX, numberOfElements.toInt(), numberOfAdditionalElements, biggerLengthOfX)
        }
    }

    /**
     * funkce vyplni mezery mezi body
     */
    private fun addNewPoints(leftPointLongerAxis: Short, leftPointShorterAxis: Short, rightPointLongerAxis: Short, rightPointShorterAxis: Short, numberOfElements: Int, numberOfAdditionalElements: Int, biggerLengthOfX: Boolean) : Array<Array<Short>>
    {
        var connectedTwoPoints = ArrayOf<Array<Short>>()
        var index: Short = 0
        var startingPoint = leftPointLongerAxis //upravit cyklus for, chceme aby druhy for cyklus vzdy pokracoval tam, kde skoncil + 1

        for(leftPointShorterAxis in leftPointShorterAxis..rightPointShorterAxis)
        {
            //pozn. otestovat druhy for cyklus
            for (i in startingPoint..(startingPoint + numberOfElements - 1)) //odecitame 1, abychom dochazeli k cislu primo odpovidajicimu numberOfElements
            {
                if (biggerLengthOfX)
                {
                    connectedTwoPoints[0][index] = leftPointLongerAxis
                    connectedTwoPoints[1][index] = leftPointShorterAxis
                    index++
                }
                else //biggerLengthOfX = false
                {
                    connectedTwoPoints[0][index] = leftPointShorterAxis
                    connectedTwoPoints[1][index] = leftPointLongerAxis
                    index++
                }
            }
            startingPoint++
            //zde pridat metodu pro urcovani obecne rovnice vektoru
        }

        return connectedTwoPoints
    }

}