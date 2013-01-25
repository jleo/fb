package org

import org.bson.types.ObjectId

class Transaction {
    ObjectId id
   String matchId
int bet
    float delta
    int clientId
    int resultRA
    int resultRB
    String betInfo

    static constraints = {
    }

    static mapping = {
        collection "transaction"
    }

    static mapWith = "mongo"
}
