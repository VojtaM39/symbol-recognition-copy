package com.example.symbolrecognition

class Gesture {
    var id: Int? = null
    var contact_id: Long? = null

    constructor(id: Int, contact_id : Long)
    {
        this.id = id
        this.contact_id = contact_id
    }
}