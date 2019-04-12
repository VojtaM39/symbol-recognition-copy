package com.ovapp.symbolrecognition

class StartingPointsFinder {

    public fun findStartingPoints(points : MutableList<Array<Short>>) : MutableList<Array<Int>> {
        //cyklus prochazi tahy
        var isStarting : Boolean
        var startingPoints = mutableListOf<Array<Int>>()
        for(arr in points) {
            //startingPoints daneho tahu (indexy)
            var startingPointsArr = arrayOf<Int>()
            //prochazi jednlotlive body
            for(i in arr.indices) {
                isStarting = true
                //zjistim jestli body na obe strany od daneho bodu jsou vic vlevo(nahore), pokud jsou oba vic vpravo, je dany bod starting point
                if(i-1 >= 0) {
                    if(arr[i-1] < arr[i])
                        isStarting = false
                }
                if(i+1 <= arr.size-1) {
                    if(arr[i+1] < arr[i])
                        isStarting = false
                }
                if(isStarting) {
                    startingPointsArr += i
                }

            }
            startingPoints.add(startingPointsArr)
        }
        return startingPoints
    }


    public fun findEndingPoints(points : MutableList<Array<Short>>) : MutableList<Array<Int>> {
        //cyklus prochazi tahy
        var isEnding : Boolean
        var endingPoints = mutableListOf<Array<Int>>()
        for(arr in points) {
            //endingPoints daneho tahu (indexy)
            var endingPointsArr = arrayOf<Int>()
            //prochazi jednlotlive body
            for(i in arr.indices) {
                isEnding = true
                //zjistim jestli body na obe strany od daneho bodu jsou vic vlevo(nahore), pokud jsou oba vic vpravo, je dany bod starting point
                if(i-1 >= 0) {
                    if(arr[i-1] > arr[i])
                        isEnding = false
                }
                if(i+1 <= arr.size-1) {
                    if(arr[i+1] > arr[i])
                        isEnding = false
                }
                if(isEnding) {
                    endingPointsArr += i
                }

            }
            endingPoints.add(endingPointsArr)
        }
        return endingPoints
    }

}