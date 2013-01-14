package org

import com.mongodb.BasicDBObject
import com.mongodb.Mongo

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-1-14
 * Time: 下午2:12
 * Let's RocknRoll
 */

def handicap = { type, scoreA, scoreB, abFlag, betOn ->
    if (abFlag == 1) {
        def temp = scoreA
        scoreA = scoreB
        scoreB = temp
    }

    if (betOn == 1) {
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

db = new Mongo("rm4", 15000).getDB("fb");

def betCollection = db.getCollection("bet")

betCollection.find(new BasicDBObject().append("status", "new")).each { it ->
    String matchId = it.get("matchId")
    String cid = it.get("cid")
    int clientId = it.get("clientId") as int
    float bet = it.get("bet") as float
    int betOn = it.get("betOn")    //0主1客

    def matchInfo = db.getCollection("result").findOne(new BasicDBObject().append("matchId", matchId))
    int type = matchInfo.get("ch") as int

    float h1 = matchInfo.get("h1") as float
    float h2 = matchInfo.get("h2") as float

    int resultRA = matchInfo.get("resultRA") as int
    int resultRB = matchInfo.get("resultRB") as int

    int abFlag = matchInfo.get("abFlag") as int  //0主 1客

    float result = handicap(type, resultRA, resultRB, abFlag, betOn)

    def transactionCollection = db.getCollection("transaction")

    def delta = null;

    if (result == 0) {
        delta = 0
    }

    if (result > 0) {
        delta = bet * result * (betOn == 1 ? h1 : h2)
    }

    if (result < 0) {
        delta = bet * result
    }

    transactionCollection.save(new BasicDBObject()
            .append("matchId", matchId)
            .append("bet", bet)
            .append("delta", delta)
            .append("clientId", clientId)

    )

    betCollection.update(it, new BasicDBObject().append("\$set",new BasicDBObject("status","processed")))
}
