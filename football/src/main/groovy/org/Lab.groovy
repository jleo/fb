package org

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-1-7
 * Time: 下午6:19
 * To change this template use File | Settings | File Templates.
 */
//1全赢
//-1全输
//0打平
//-0.5输一半
//0.5赢一半
def handicap = { type, scoreA, scoreB, abFlag ->
    if (abFlag == 2){
        def temp = scoreA
        scoreA = scoreB
        scoreB = temp
    }

    if (type % 4 == 0) {
        def handicap = type / 4
        if (scoreA - handicap == scoreB)
            return 0;
        if (scoreA - handicap > scoreB)
            return 1
        if (scoreA - handicap < scoreB)
            return -1
    }
    if (type % 4 == 1) {
        def handicap = type / 4
        if (scoreA - handicap == scoreB)
            return -0.5;
        if (scoreA - handicap > scoreB)
            return 1
        if (scoreA - handicap < scoreB)
            return -1
    }
    if (type % 4 == 2) {
        def handicap = type / 4
        if (scoreA - handicap == scoreB)
            return -1;
        if (scoreA - handicap > scoreB)
            return 1
        if (scoreA - handicap < scoreB)
            return -1
    }

    if (type % 4 == 3) {
        def handicap = type / 4 + 1
        if (scoreA - handicap == scoreB)
            return 0.5;
        if (scoreA - handicap > scoreB)
            return 1
        if (scoreA - handicap < scoreB)
            return -1
    }

}

