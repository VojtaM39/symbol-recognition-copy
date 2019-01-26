package com.example.symbolrecognition

class ConnectingPoints
{
    private var movesX = mutableListOf<Array<Short>>()
    private var movesY = mutableListOf<Array<Short>>()
    private var connectedPoints: Array<Array<Short>>

    constructor(movesX: MutableList<Array<Short>>, movesY: MutableList<Array<Short>>) {
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
        var connectedPoints: Array<Array<Short>> //connectedPoints[0] - x souradnice, [1] - y souradnice
        var connectedTwoPoints: Array<Array<Short>> //connectedTwoPoints[0] - x souradnice, [1] - y souradnice

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
        //vytvorit podminku, pokud jsou stejne
        var connectedTwoPoints: Array<Array<Short>>



        return connectedTwoPoints
    }
}