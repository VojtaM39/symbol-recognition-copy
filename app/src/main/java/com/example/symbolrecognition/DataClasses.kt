package com.example.symbolrecognition

import kotlin.math.absoluteValue

data class Line(val x1 : Short, val y1 : Short, val x2 : Short,val y2 : Short) {
    val angle : Float
    val shiftCoefficientX : Short
    val shiftCoefficientY : Short
    init {
        angle = getAngle(x1,y1,x2,y2)
        shiftCoefficientX = getShiftCoefficient(x1,y1,x2,y2)
        shiftCoefficientY = getShiftCoefficient(y1,x1,y2,x2)
    }
    /**
    Metoda vraci nutny posun, ktery je nutny aby cara prochazela bodem 0,0, pokud by se protahla na primku
    V pripade ze je uhel cary vetsi nez 0,5 (spise horizontalni), posouvame na ose y
    Pro uhel -0,5 - 0,5 (spise vertikalni), posouvame na ose x
    Timto se vyhneme cislum blizicim se nekonecnu
    Jako primary vzdy davame body na ose, na ktere se budeme posouvat
     */
    private fun getShiftCoefficient(primary1 : Short,secondary1: Short,primary2: Short,secondary2: Short) : Short{
        val deltaPrimary = primary1 - primary2
        val deltaSecondary = secondary1 - secondary2
        val ratio : Float
        //Pokud se jedna o presnou caru (vertikalni/horizontalni) bude vysledek 0, protoze se takovy shift coef stejne nebude pouzivat
        if(deltaSecondary!=0) {
            ratio = deltaPrimary.toFloat()/deltaSecondary.toFloat()
        }
        else{
            ratio = 0f
        }

        //Target point, je bod ktery se nachazi posunuty od prvniho bodu ve smeru hlavni osy tak, aby v pripade spojeni s pocatkem soustavy tvoril rovnobezku k care
        val targetPointSecondary = secondary1
        val targetPointPrimary : Short = (targetPointSecondary * ratio).toShort()

        //Vysledek je vzdalenost k targetPoint na dane ose
        val result = (primary1-targetPointPrimary).toShort()
        return result
    }
    /**
    Metoda vraci uhel dane line
    Pomer x/(x+y)
    Pokud je line nasmerovana zleva nahore - dolu doprava => zaporna hodnota
     */

    private fun getAngle(x1 : Short,y1: Short,x2: Short,y2: Short) : Float{
        val xDiff = (x1 - x2).absoluteValue
        val yDiff = (y1 - y2).absoluteValue
        var angle : Float = xDiff.toFloat()/(xDiff.toFloat()+yDiff.toFloat())
        //Pokud je bod, ktery je vic nahore i vic vpravo (line smeruje doleva a dolu) => zaporna hodnota
        if(((x1 < x2) && (y1 < y2)) || ((x2 < x1) && (y2 < y1))) {
            angle *=-1
        }
        return angle

    }


}