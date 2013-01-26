package org

import groovy.json.JsonSlurper
import org.bson.types.ObjectId

class Transaction {
    ObjectId id
    String matchId
    int bet
    String cid
    float delta
    String clientId
    int resultRA
    int resultRB
    String betInfo
    def jsonBetInfo

    public void setBetInfo(String betInfo){
        jsonBetInfo = new JsonSlurper().parseText(betInfo)
    }

    static constraints = {
    }

    static mapping = {
        collection "transaction"
    }

    static mapWith = "mongo"
}
