package com.ovapp.symbolrecognition

class Contact
{
    var gesturesId: Int? = null
    var contactName: String? = null
    var movesX: MutableList<Array<Short>>? = null
    var movesY: MutableList<Array<Short>>? = null

    constructor(gesturesId: Int?, contactName: String?, movesX : MutableList<Array<Short>>?, movesY : MutableList<Array<Short>>?)
    {
        this.gesturesId = gesturesId
        this.contactName = contactName
        this.movesX = movesX
        this.movesY = movesY
    }
}