import kotlin.math.abs
import kotlin.math.truncate
import kotlin.math.round

class ConnectingPointsWithList
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

    fun connectPoints(): mutableList<Array<Short>> //public
    {
        println("Vracim connectedPoints")
        return connectedPoints
    }

    /**
     * metoda spojujici body
     * vystupem je dvojrozmerne pole se spojenymi body
     * */
    private fun connectAllPoints(movesX: MutableList<Array<Short>>, movesY: MutableList<Array<Short>>): MutableList<Array<Short>>
    {
        println("Vstuptuju do connectAllPoints")
        var connectedPoints = arrayOf<Array<Short>>() //connectedPoints[0] - x souradnice, [1] - y souradnice
        var connectedTwoPoints = arrayOf<Array<Short>>() //connectedTwoPoints[0] - x souradnice, [1] - y souradnice
        var helpArray = arrayOf<Short>()

        for (i in 0..(movesX.size - 1))
        {
            if (movesX[i].size == 1 ) //pokud se v poli nachazi 1 bod, vrat toto pole
            {
                connectedPoints += movesX
                connectedPoints += movesY
            }
            else
            {
                for (j in 0..(movesX[i].size - 1 - 1)) //-1 za delku pole a -1 protoze posledni souradnici uz nemame s cim spojovat
                {
                    if (movesX[i][j] <= movesX[i][j + 1]) //aktualni movesX je vice vlevo
                        connectedTwoPoints = connectThesePoints(movesX[i][j], movesY[i][j], movesX[i][j + 1], movesY[i][j + 1])
                    else //nasledujici movesX je vice vlevo
                        connectedTwoPoints = connectThesePoints(movesX[i][j + 1], movesY[i][j + 1], movesX[i][j], movesY[i][j])
                }
                println("Pozor, toto je ono")
                for(i in 0..(connectedTwoPoints[0].size - 1))
                    helpArray += 0
                connectedPoints += helpArray
                connectedPoints += helpArray
                connectedPoints = connectedTwoPoints
            }
        }
        return connectedPoints
    }

    /**
     * spojuje konkretni 2 body
     * hodnoty vraci ve dvourozmernem poli [0] - x souradnice, [1] - y souradnice
     */
    private fun connectThesePoints(leftPointX: Short, leftPointY: Short, rightPointX: Short, rightPointY: Short) : MutableList<Array<Short>>
    {
        println("Nachazim se v connectThesePoints")
        //pojistit, aby metoda fungovala i pro x = y
        var biggerLengthOfX: Boolean
        var numberOfElements: Double
        var numberOfAdditionalElements: Int
        var ascending: Boolean

        var lengthX = rightPointX - leftPointX + 1 //+1 nam da delku vcetne bodu
        var lengthY = abs(rightPointY - leftPointY) + 1 //+1 nam da delku vcetne bodu

        //obecna rovnice - hodnoty a, b, c
        val a = rightPointY - leftPointY
        val b = -(rightPointX - leftPointX)
        val c = -(a * leftPointX + b * leftPointY)

        if(rightPointY < leftPointY)
            ascending = true
        else
            ascending = false

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
    private fun addNewPoints(leftPointLongerAxis: Short, leftPointShorterAxis: Short, rightPointLongerAxis: Short, rightPointShorterAxis: Short, numberOfElements: Int, numberOfAdditionalElements: Int, biggerLengthOfX: Boolean, a: Int, b: Int, c: Int, ascending: Boolean) : MutableList<Array<Short>>
    {
        //vzdy zaciname zleva
        println("Nachazim se v addNewPoints")
        var connectedTwoPoints = arrayOf<Array<Short>>()
        var connectedTwoPointsList = mutableListOf<Array<Short>>()

        var distance: Int
        if((ascending) && (!biggerLengthOfX))
            distance = (numberOfElements * -1) + 1
        else
            distance = numberOfElements - 1



        //prvotni vyplneni pole, abychom jej pote mohli prepsat
        if(biggerLengthOfX)
        {
            for(i in 0..1)
            {
                var helpArray = arrayOf<Short>()
                for (j in 0..(numberOfElements * leftPointShorterAxis + numberOfAdditionalElements - 1))
                    helpArray += 0
                connectedTwoPoints += helpArray
            }
        }
        else
        {
            for(i in 0..1)
            {
                var helpArray = arrayOf<Short>()
                for (j in 0..(numberOfElements * rightPointShorterAxis + numberOfAdditionalElements - 1))
                    helpArray += 0
                connectedTwoPoints += helpArray
            }
        }

        var index: Int = 0
        var startingPointLongerAxis = leftPointLongerAxis.toInt()
        var forStartingPointLongerAxis: Int
        var addedAdditionalElements = 0
        var startingPointShorterAxis = leftPointShorterAxis.toInt()
        var endingPointShorterAxis = rightPointShorterAxis.toInt()

        for(i in startingPointShorterAxis toward endingPointShorterAxis)
        {
            forStartingPointLongerAxis = startingPointLongerAxis //oprava for cyklu, otestovat doma
            for (j in forStartingPointLongerAxis toward (forStartingPointLongerAxis + distance)) //odecitame 1, abychom dochazeli k cislu primo odpovidajicimu numberOfElements
            {
                println("Jsem ve for cyklu j: ${j}")
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
                startingPointLongerAxis = forStartingPointLongerAxis
            }
            if ((numberOfAdditionalElements != 0) && (addedAdditionalElements < numberOfAdditionalElements))
            {
                println("jsem v podmince")
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
            println("Vybelh jsem z for cyklu")
        }
        return connectedTwoPoints
    }
    private fun addAdditionalPoint(a: Int, b: Int, c: Int, actualX: Int, actualY: Int) : Boolean
    {
        println("Nachazim se v addAdditionalPoint")
        var y: Double = round((a * actualX + c).toDouble() / -b)
        if (y == actualY.toDouble()) {
            println("Hura, pouzil jsem vektorovou rovnici")
            return true
        }
        else {
            println("Nevyuzil jsem vektorovou rovnici")
            return false
        }
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