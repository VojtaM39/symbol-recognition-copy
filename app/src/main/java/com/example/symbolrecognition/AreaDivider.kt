package com.example.symbolrecognition

class AreaDivider {
    private var movesX = mutableListOf<Array<Short>>()
    private var movesY = mutableListOf<Array<Short>>()
    constructor(movesX : MutableList<Array<Short>>, movesY : MutableList<Array<Short>>) {
        this.movesX = movesX
        this.movesY = movesY
    }

}