package com.example.symbolrecognition

class Point {
    var id: Int? = null
    var gesture_id: Long? = null
    var move_number: Int? = null
    var point_x: Short? = null
    var point_y: Short? = null

    constructor(id: Int, gesture_id : Long, move_number : Int, point_x: Short, point_y : Short)
    {
        this.id = id
        this.gesture_id = gesture_id
        this.move_number = move_number
        this.point_x = point_x
        this.point_y = point_y
    }
}