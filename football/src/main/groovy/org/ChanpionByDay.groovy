package org

import com.mongodb.BasicDBObject
import com.mongodb.Mongo

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-1-23
 * Time: 下午10:05
 * Let's RocknRoll
 */


def mongo = new Mongo("rm4", 15000)
def c = mongo.getDB("fb").getCollection("summaryDateByDate")

def dayAidRank = [:].withDefault {
    return [:]
}

def aidDayRank = [:].withDefault {
    return [:]
}
(1..20).each { day ->
    int rank = 10
    c.find(["_id.day": day, "value.count": ["\$gt": 1]] as BasicDBObject, ["_id.aid": 1] as BasicDBObject).sort(["value.profitAverage": -1] as BasicDBObject).each {
        if (rank <= 0)
            return

        dayAidRank.get(day).putAll(["${it.get("_id").get("aid")}": rank])
        def rankList = aidDayRank.get(it.get("_id").get("aid")).get(day)
        if (!rankList)
            aidDayRank.get(it.get("_id").get("aid")).put(day, [])

        aidDayRank.get(it.get("_id").get("aid")).get(day) << rank
        rank--
    }
}

(1..20).each { day ->
    println ""
    println ""
    println ""
    println ""
    println "2013-1-$day"
    println "-----------------------------------"
    println dayAidRank.get(day)
}

def aidSum = new TreeMap<>().withDefault {
    return 0
}

aidDayRank.each { aid, dayRank ->
        println dayRank.get(day).sum()/dayRank.get(day).size()
        aidSum.put(aid, aidSum.get(aid) + ((dayRank.get(day) == null) ? 0 : dayRank.get(day)))
}
println aidSum
