package com.example.symbolrecognition

class Contact
{
    var gesturesId: Int? = null
    var gesturesContactId: Int? = null

    constructor(gesturesId: Int?, gesturesContactId: Int?)
    {
        this.gesturesId = gesturesId
        this.gesturesContactId = gesturesContactId
    }
}