package org

import com.mongodb.BasicDBObject
import com.mongodb.Mongo
/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-1
 * Time: 下午7:38
 * Let's RocknRoll
 */
class RecommendChecker {

    public static void main(String[] args) {
        def mongo = new Mongo("rm4", 15000)
        def db = mongo.getDB("fb")
        def recommendMatch = db.getCollection("recommendMatch")
        def resultNew = db.getCollection("resultnew")

        resultNew.find().each {rn->
            def matchId = rn.get("matchId")
            def rm = recommendMatch.findOne([matchId:matchId] as BasicDBObject)
            double rate = rm.get("rate")
            int betOn = rm.get("betOn")

            def abFlag = rn.get("abFlag")

            def h1 = rn.get("h1")
            def h2 = rn.get("h2")
            if (abFlag != 1){
                def temp = h1
                h1 = h2
                h2 = temp
            }

            def toCompare = h2

            if (betOn == 0){
                toCompare = h1
            }

            if(toCompare > rate){

            }
        }

    }
}
