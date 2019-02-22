package com.example.symbolrecognition

class Constants {
    companion object {
        val SQUARE_SIZE = 50

        /**
        Database tables
         */
        val CONTACTS_TABLE = "Contacts"
        val GESTURES_TABLE = "Gestures"
        val POINTS_TABLE = "Points"
        val RATIOS_TABLE = "Ratios"
        val LINES_TABLE = "Lines"

        //Contacts

        val CONTACTS_ID = "Id"
        val CONTACTS_NAME = "Name"
        val CONTACTS_PHONE_NUMBER = "PhoneNumber"

        val CONTACTS_COLUMNS = listOf(CONTACTS_ID, CONTACTS_NAME, CONTACTS_PHONE_NUMBER)

        //Gestures
        val GESTURES_ID = "Id"
        val GESTURES_CONTACT_ID = "contact_id"

        val GESTURES_COLUMNS = listOf(GESTURES_ID, GESTURES_CONTACT_ID)

        //Points
        val POINTS_ID = "Id"
        val POINTS_GESTURE_ID = "gesture_id"
        val POINTS_MOVE_NUMBER = "move_number"
        val POINTS_X = "point_x"
        val POINTS_Y = "point_y"

        val POINTS_COLUMNS = listOf(POINTS_ID, POINTS_GESTURE_ID, POINTS_MOVE_NUMBER, POINTS_X, POINTS_Y)

        //Ratios
        val RATIOS_ID = "Id"
        val RATIOS_GESTURE_ID = "gesture_id"
        val RATIOS_X = "x_ratio"
        val RATIOS_Y = "y_ratio"

        val RATIOS_COLUMNS = listOf(RATIOS_ID, RATIOS_GESTURE_ID, RATIOS_X, RATIOS_Y)

        //Lines
        val LINES_ID = "Id"
        val LINES_GESTURE_ID = "gesture_id"
        val LINES_X1 = "x1"
        val LINES_Y1 = "y1"
        val LINES_X2 = "x2"
        val LINES_Y2 = "y2"

        val LINES_COLUMNS = listOf(LINES_ID, LINES_GESTURE_ID, LINES_X1, LINES_Y1, LINES_X2, LINES_Y2)
    }
}