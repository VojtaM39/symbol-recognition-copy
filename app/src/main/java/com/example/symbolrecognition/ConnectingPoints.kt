package com.example.symbolrecognition

import kotlin.math.abs
import kotlin.math.truncate
import kotlin.math.round

class ConnectingPoints
{
    private var movesX = mutableListOf<Array<Short>>()
    private var movesY = mutableListOf<Array<Short>>()
    private var connectedPoints = mutableListOf<Array<Short>>()
    private var thickness = mutableListOf<Array<Short>>()
    private val SQUARE_SIZE: Int

    constructor(movesX: MutableList<Array<Short>>, movesY: MutableList<Array<Short>>)
    {
        this.movesX = movesX
        this.movesY = movesY
        this.SQUARE_SIZE = Constants.SQUARE_SIZE
        this.connectedPoints = connectAllPoints(movesX, movesY, this.thickness, SQUARE_SIZE)
    }

    fun connectPoints(): MutableList<Array<Short>> //public
    {
        return connectedPoints
    }
    fun getThickness(): MutableList<Array<Short>>
    {
        return thickness
    }

    /**
     * metoda spojujici body
     * vystupem je dvojrozmerne pole se spojenymi body
     * */
    private fun connectAllPoints(movesX: MutableList<Array<Short>>, movesY: MutableList<Array<Short>>, thickness: MutableList<Array<Short>>, SQUARE_SIZE: Int): MutableList<Array<Short>>
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
                        connectedTwoPoints = connectThesePoints(movesX[i][j], movesY[i][j], movesX[i][j + 1], movesY[i][j + 1], thickness, SQUARE_SIZE) //jako prvni posilame ten bod, ktery je vice vlevo
                    else //nasledujici movesX je vice vlevo
                        connectedTwoPoints = connectThesePoints(movesX[i][j + 1], movesY[i][j + 1], movesX[i][j], movesY[i][j], thickness, SQUARE_SIZE) //jako prvni posilame ten bod, ktery je vice vpravo
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
    private fun connectThesePoints(leftPointX: Short, leftPointY: Short, rightPointX: Short, rightPointY: Short, thickness: MutableList<Array<Short>>, SQUARE_SIZE: Int) : Array<Array<Short>>
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
            return addNewPoints(leftPointX, leftPointY, rightPointX, rightPointY, numberOfElements.toInt(), numberOfAdditionalElements, biggerLengthOfX, a, b, c, ascending, thickness, SQUARE_SIZE)
        }
        else //delsi y
        {
            biggerLengthOfX = false
            numberOfElements = truncate(lengthY.toDouble() / lengthX)
            numberOfAdditionalElements = lengthY % lengthX
            return addNewPoints(leftPointY, leftPointX, rightPointY, rightPointX, numberOfElements.toInt(), numberOfAdditionalElements, biggerLengthOfX, a, b, c, ascending, thickness, SQUARE_SIZE)
        }
    }

    /**
     * funkce vyplni mezery mezi body
     */
    private fun addNewPoints(leftPointLongerAxis: Short, leftPointShorterAxis: Short, rightPointLongerAxis: Short, rightPointShorterAxis: Short, numberOfElements: Int, numberOfAdditionalElements: Int, biggerLengthOfX: Boolean, a: Int, b: Int, c: Int, ascending: Boolean, thickness: MutableList<Array<Short>>, SQUARE_SIZE: Int) : Array<Array<Short>>
    {
        //vzdy zaciname zleva
        var connectedTwoPoints = arrayOf<Array<Short>>()
        var thicknessPoints = mutableListOf<Array<Short>>()

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
                    addValueToArray(connectedTwoPoints, j.toShort(), i.toShort(), index)
                else //biggerLengthOfX == false
                    addValueToArray(connectedTwoPoints, i.toShort(), j.toShort(), index)
                index++
                if((ascending) && (!biggerLengthOfX)) //za teto podminky funkce klesa
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
                        addValueToArray(connectedTwoPoints, startingPointLongerAxis.toShort(), i.toShort(), index)
                        index++
                        addedAdditionalElements++
                        startingPointLongerAxis++
                    }
                }
                else //biggerLengthOfX == false
                {
                    if(addAdditionalPoint(a, b, c, i, startingPointLongerAxis))
                    {
                        addValueToArray(connectedTwoPoints, i.toShort(), startingPointLongerAxis.toShort(), index)
                        index++
                        addedAdditionalElements++
                        startingPointLongerAxis--
                    }
                }
            }
            //osetrime, abychom nemeli v poli navic hodnoty nul
            if(i == endingPointShorterAxis)
            {
                while(index <= (connectedTwoPoints[0].size - 1)) //cyklus while pojede, dokud bude index, ktery je narade mensi nebo roven delce connectedTwoPoints - 1
                {
                    if (biggerLengthOfX)
                        addValueToArray(connectedTwoPoints, startingPointLongerAxis.toShort(), i.toShort(), index)
                    else //biggerLengthOfX == false
                        addValueToArray(connectedTwoPoints, i.toShort(), startingPointLongerAxis.toShort(), index)
                    index++
                }
            }
        }
        //ziskame tloustku
        thicknessPoints.add(0, addThicknessPoints(connectedTwoPoints, biggerLengthOfX, ascending, SQUARE_SIZE)[0])
        thicknessPoints.add(1, addThicknessPoints(connectedTwoPoints, biggerLengthOfX, ascending, SQUARE_SIZE)[1])
        return connectedTwoPoints
    }

    /**
     * funkce priradi body X a Y do pole
     */
    private fun addValueToArray(connectedTwoPoints: Array<Array<Short>>, x: Short, y: Short, index: Int)
    {
        connectedTwoPoints[0][index] = x
        connectedTwoPoints[1][index] = y
    }

    /**
     * prida bod, pokud bod nalezi rovnici vektoru
     */
    private fun addAdditionalPoint(a: Int, b: Int, c: Int, actualX: Int, actualY: Int) : Boolean
    {
        var y: Double = round((a * actualX + c).toDouble() / -b)
        return y == actualY.toDouble() //vrati true, pokud se vypoctene Y rovna aktualnimu Y
    }

    /**
     * funkce prida do pole poli shortove souradnice X a Y bodu, ktere vytvori tloustku konkretnich krivek
     */
    private fun addThicknessPoints(connectedTwoPoints: Array<Array<Short>>, biggerLengthOfX: Boolean, ascending: Boolean, SQUARE_SIZE: Int) : Array<Array<Short>>
    {
        var addedThickness = arrayOf<Array<Short>>()
        var helpArrayX = arrayOf<Short>()
        var helpArrayY = arrayOf<Short>()
        var unitOfThickness = (SQUARE_SIZE / 10) - 3 //unitOfThickness = 2 //jedna se o jednotky tloustky
        if(connectedTwoPoints[0].size == 1) //pouze bod
        {

        }
        else
        {
            for(i in 0..(connectedTwoPoints[0].size - 1))
            {
                //pridani zakladnich bodu
                if(biggerLengthOfX)
                    addedThickness += addBasicPoints(connectedTwoPoints[1][i], connectedTwoPoints[0][i], unitOfThickness, biggerLengthOfX, SQUARE_SIZE)
                else
                    addedThickness += addBasicPoints(connectedTwoPoints[1][i], connectedTwoPoints[0][i], unitOfThickness, biggerLengthOfX, SQUARE_SIZE)
            }
        }
        return addedThickness
    }

    /**
     * funkce prida body k souradnicim nekrajnich bodu
     */
    private fun addBasicPoints(addingAxis: Short, stillAxis: Short, unitOfThickness: Int, biggerLengthOfX: Boolean, SQUARE_SIZE: Int) : Array<Array<Short>>
    {
        var addedBasicPoints = arrayOf<Array<Short>>()
        var helpArrayAdding = arrayOf<Short>()
        var helpArrayStill = arrayOf<Short>()

        //podminka pro vybehnuti z pole
        for(i in 1..unitOfThickness) {
            //pricitani
            if ((addingAxis + i) < SQUARE_SIZE) {
                helpArrayAdding += (addingAxis + i).toShort()
                helpArrayStill += stillAxis
            }
            //odecitani
            if ((addingAxis - i) >= 0)
            {
                helpArrayAdding += (addingAxis - i).toShort()
                helpArrayStill += stillAxis
            }
        }

        //prirazeni do dvourozmerneho pole
        if(biggerLengthOfX)
        {
            addedBasicPoints[0] += helpArrayStill
            addedBasicPoints[1] += helpArrayAdding
        }
        else
        {
            addedBasicPoints[0] += helpArrayAdding
            addedBasicPoints[1] += helpArrayStill
        }
        return addedBasicPoints
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