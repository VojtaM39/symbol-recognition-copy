package com.example.symbolrecognition

import kotlin.math.abs
import kotlin.math.truncate
import kotlin.math.round

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
        var connectedPoints = arrayOf<Array<Short>>() //connectedPoints[0] - x souradnice, [1] - y souradnice
        var connectedTwoPoints = arrayOf<Array<Short>>() //connectedTwoPoints[0] - x souradnice, [1] - y souradnice

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

        //obecna rovnice - hodnoty a, b, c
        val a = rightPointY - leftPointY
        val b = -(rightPointX - leftPointX)
        val c = -(a * leftPointX + b * leftPointY)


        if (lengthX > lengthY) //delsi x
        {
            biggerLengthOfX = true
            return addNewPoints(leftPointX, leftPointY, rightPointX, rightPointY, numberOfElements.toInt(), numberOfAdditionalElements, biggerLengthOfX, a, b, c)
        }
        else //delsi y
        {
            biggerLengthOfX = false
            return addNewPoints(leftPointY, leftPointX, rightPointY, rightPointX, numberOfElements.toInt(), numberOfAdditionalElements, biggerLengthOfX, a, b, c)
        }
    }

    /**
     * funkce vyplni mezery mezi body
     */
    private fun addNewPoints(leftPointLongerAxis: Short, leftPointShorterAxis: Short, rightPointLongerAxis: Short, rightPointShorterAxis: Short, numberOfElements: Int, numberOfAdditionalElements: Int, biggerLengthOfX: Boolean, a: Int, b: Int, c: Int) : Array<Array<Short>>
    {
        var connectedTwoPoints = arrayOf<Array<Short>>()
        var index: Int = 0
        var startingPointLongerAxis = leftPointLongerAxis.toInt()
        var addedAdditionalElements = 0

        //obecna rovnice vektoru


        for(i in leftPointShorterAxis..rightPointShorterAxis)
        {
            for (j in startingPointLongerAxis..(startingPointLongerAxis + numberOfElements - 1)) //odecitame 1, abychom dochazeli k cislu primo odpovidajicimu numberOfElements
            {
                if (biggerLengthOfX)
                {
                    connectedTwoPoints[0][index] = j.toShort() //x
                    connectedTwoPoints[1][index] = i.toShort() //y
                    index++
                }
                else //biggerLengthOfX == false
                {
                    connectedTwoPoints[0][index] = i.toShort() //x
                    connectedTwoPoints[1][index] = j.toShort() //y
                    index++
                }
                startingPointLongerAxis++
                if ((numberOfAdditionalElements != 0) && (addedAdditionalElements < numberOfAdditionalElements))
                {
                    if(biggerLengthOfX)
                    {
                        if(addAdditionalPoint(a, b, c, j, i))
                        {
                            connectedTwoPoints[0][index] = j.toShort() //x
                            connectedTwoPoints[1][index] = i.toShort() //y
                            index++
                            addedAdditionalElements++
                            startingPointLongerAxis++
                        }
                    }
                    else //biggerLengthOfX == false
                    {
                        if(addAdditionalPoint(a, b, c, i, j))
                        {
                            connectedTwoPoints[0][index] = i.toShort() //x
                            connectedTwoPoints[1][index] = j.toShort() //y
                            index++
                            addedAdditionalElements++
                            startingPointLongerAxis++
                        }
                    }
                }
            }

        }
        return connectedTwoPoints
    }
    private fun addAdditionalPoint(a: Int, b: Int, c: Int, actualX: Int, actualY: Int) : Boolean
    {
        var y: Double = round((a * actualX + c).toDouble() / -b)
        if (y == actualY.toDouble())
            return true
        else
            return false
    }

}