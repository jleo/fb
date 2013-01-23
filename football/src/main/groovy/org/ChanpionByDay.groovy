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

def winCount = [:].withDefault {
    return 0
}
(1..20).each {
    int rank = 1
    c.find(["_id.day": it, "value.count": ["\$gt": 1]] as BasicDBObject, ["_id.aid": 1] as BasicDBObject).sort(["value.profitAverage": -1] as BasicDBObject).each {
        if (rank <= 0)
            return

        winCount.put(it.get("_id").get("aid"), winCount.get(it.get("_id").get("aid")) + rank++)
    }
}

println winCount