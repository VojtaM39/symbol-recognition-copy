package com.example.symbolrecognition

class Constants {
    companion object {
        val SQUARE_SIZE = 50

        val ACCURACY_DEFAULT_VALUE = 0.7f

        /**
         * Id akcí, při vyhodnocení
         */
        val ACTION_CONTACT : Short = 0
        val ACTION_CALL : Short = 1
        val ACTION_SMS : Short = 2


        /**
        Database tables
         */
        val CONTACTS_TABLE = "Contacts"
        val GESTURES_TABLE = "Gestures"
        val POINTS_TABLE = "Points"
        val POINTS_PREDEFINED_TABLE = "PointsPredefined"
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

        //Predefined gestures
        val PREDEFINED_GESTURES_X = listOf<Array<Short>>(
            arrayOf(25, 24, 23, 21, 19, 16, 13, 9, 4, 1, 0, 0, 2, 7, 14, 26, 39, 49, 50),
            arrayOf(0, 5, 13, 22, 31, 41, 50)

        )

        val PREDEFINED_GESTURES_Y = listOf<Array<Short>>(
            arrayOf(11, 9, 7, 5, 3, 2, 2, 4, 10, 16, 23, 30, 37, 43, 46, 47, 46, 42, 42),
            arrayOf(25, 25, 25, 25, 25, 25, 25)


        )
    }
}