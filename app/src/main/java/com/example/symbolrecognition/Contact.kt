package com.example.symbolrecognition

class Contact
{
    var gesturesId: Int? = null
    var contactName: String? = null

    constructor(gesturesId: Int?, contactName: String?)
    {
        this.gesturesId = gesturesId
        this.contactName = contactName
    }
}