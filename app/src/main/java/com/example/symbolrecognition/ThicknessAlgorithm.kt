package com.example.symbolrecognition

class ThicknessAlgorithm
{
    private var pointsX: Array<Short>
    private var pointsY: Array<Short>
    private var movesX = mutableListOf<Array<Short>>()
    private var movesY = mutableListOf<Array<Short>>()

    constructor(pointsX:Array<Short>, pointsY : Array<Short>, movesX : MutableList<Array<Short>>, movesY : MutableList<Array<Short>>)
    {
        this.pointsX = pointsX
        this.pointsY = pointsY
        this.movesX = movesX
        this.movesY = movesY
    }
}