package org

import org.bson.types.ObjectId



class Match {

    ObjectId id
    Double returnRate
    Double wr
    Double pr
    Double lr
    String matchId
    String cid
    float w1
    float p1
    float l1
    float w2
    float p2
    float l2
    String mtype
    Date time
    String tidA
    String tNameA
    String tidB
    String tNameB
    String tRankA
    String tRankB
    int resultRA
    int resultRB
    String resultPA
    String resultPB
    float wa1
    float pa1
    float la1
    float wa2
    float pa2
    float la2
    Double h1
    Double h2
    Integer ch
    int status

    static constraints = {
        returnRate(nullable:true)
        wr(nullable:true)
        pr(nullable:true)
        lr(nullable:true)
        matchId(nullable:true)
        cid(nullable:true)
        w1(nullable:true)
        p1(nullable:true)
        l1(nullable:true)
        w2(nullable:true)
        p2(nullable:true)
        l2(nullable:true)
        mtype(nullable:true)
        time(nullable:true)
        tidA(nullable:true)
        tNameA(nullable:true)
        tidB(nullable:true)
        tNameB(nullable:true)
        tRankA(nullable:true)
        tRankB(nullable:true)
        resultRA(nullable:true)
        resultRB(nullable:true)
        resultPA(nullable:true)
        resultPB(nullable:true)
        wa1(nullable:true)
        pa1(nullable:true)
        la1(nullable:true)
        wa2(nullable:true)
        pa2(nullable:true)
        la2(nullable:true)
    }

    static mapping = {
        collection "result"
    }

    static mapWith = "mongo"
}
