package org

import org.bson.types.ObjectId

class Bet {

    ObjectId id
    String clientId
    String matchId
    String cid
    int betOn
    float bet
    String aid
    Date matchTime
    String teamA
    String teamB
    String mtype
    int ch
    double h1
    double h2

    static constraints = {
    }

    static mapping = {
        collection "bet"
    }

    static mapWith = "mongo"
}
