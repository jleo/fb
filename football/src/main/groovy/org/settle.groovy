package org

import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import com.mongodb.Mongo
import org.bson.types.ObjectId

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-1-14
 * Time: 下午2:12
 * Let's RocknRoll
 */

public class Settle {
    def mongo

    static def GoalCn = new String[41];
    static {
        GoalCn[0] = "平手";
        GoalCn[1] = "平手/半球";
        GoalCn[2] = "半球";
        GoalCn[3] = "半球/一球";
        GoalCn[4] = "一球";
        GoalCn[5] = "一球/球半";
        GoalCn[6] = "球半";
        GoalCn[7] = "球半/两球";
        GoalCn[8] = "两球";
        GoalCn[9] = "两球/两球半";
        GoalCn[10] = "两球半";
        GoalCn[11] = "两球半/三球";
        GoalCn[12] = "三球";
        GoalCn[13] = "三球/三球半";
        GoalCn[14] = "三球半";
        GoalCn[15] = "三球半/四球";
        GoalCn[16] = "四球";
        GoalCn[17] = "四球/四球半";
        GoalCn[18] = "四球半";
        GoalCn[19] = "四球半/五球";
        GoalCn[20] = "五球";
        GoalCn[21] = "五球/五球半";
        GoalCn[22] = "五球半";
        GoalCn[23] = "五球半/六球";
        GoalCn[24] = "六球";
        GoalCn[25] = "六球/六球半";
        GoalCn[26] = "六球半";
        GoalCn[27] = "六球半/七球";
        GoalCn[28] = "七球";
        GoalCn[29] = "七球/七球半";
        GoalCn[30] = "七球半";
        GoalCn[31] = "七球半/八球";
        GoalCn[32] = "八球";
        GoalCn[33] = "八球/八球半";
        GoalCn[34] = "八球半";
        GoalCn[35] = "八球半/九球";
        GoalCn[36] = "九球";
        GoalCn[37] = "九球/九球半";
        GoalCn[38] = "九球半";
        GoalCn[39] = "九球半/十球";
        GoalCn[40] = "十球";
    }

    def handicap = { type, scoreA, scoreB, betOn ->
        def result = null

        def absType = Math.abs(type)
        if (type >= 0) {
            if (absType % 4 == 0) {
                def handicap = absType / 4
                if (scoreA - handicap == scoreB) {
                    result = 0
                } else if (scoreA - handicap > scoreB) {
                    result = 1
                } else if (scoreA - handicap < scoreB) {
                    result = -1
                }
            } else if (absType % 4 == 1) {
                def handicap = absType / 4
                if (scoreA - (int) handicap == scoreB) {
                    result = -0.5
                } else if (scoreA - handicap > scoreB) {
                    result = 1
                } else if (scoreA - handicap < scoreB) {
                    result = -1
                }
            } else if (absType % 4 == 2) {
                def handicap = absType / 4
                if (scoreA - handicap > scoreB) {
                    result = 1
                } else if (scoreA - handicap < scoreB) {
                    result = -1
                }

            } else if (absType % 4 == 3) {
                def handicap = absType / 4 + 1
                if (scoreA - (int) handicap == scoreB) {
                    result = 0.5
                } else if (scoreA - handicap > scoreB) {
                    result = 1
                } else if (scoreA - handicap < scoreB) {
                    result = -1
                }
            }

            if (betOn == 0) {
                return result
            } else {
                return result * -1
            }
        } else {
            if (absType % 4 == 0) {
                def handicap = absType / 4
                if (scoreB - handicap == scoreA) {
                    result = 0
                } else if (scoreB - handicap > scoreA) {
                    result = -1
                } else if (scoreB - handicap < scoreA) {
                    result = 1
                }
            } else if (absType % 4 == 1) {
                def handicap = absType / 4
                if (scoreB - (int) handicap == scoreA) {
                    result = 0.5
                } else if (scoreB - handicap > scoreA) {
                    result = -1
                } else if (scoreB - handicap < scoreA) {
                    result = 1
                }
            } else if (absType % 4 == 2) {
                def handicap = absType / 4
                if (scoreB - handicap > scoreA) {
                    result = -1
                } else if (scoreB - handicap < scoreA) {
                    result = 1
                }
            } else if (absType % 4 == 3) {
                def handicap = absType / 4 + 1
                if (scoreB - (int) handicap == scoreA) {
                    result = -0.5
                } else if (scoreB - handicap > scoreA) {
                    result = -1
                } else if (scoreB - handicap < scoreA) {
                    result = 1
                }
            }
            if (betOn == 0)
                return result
            else
                return result * -1
        }
    }

    Settle(mongo) {
        this.mongo = mongo
    }

    public void settle(String gurateen, byDate, reverse) {
        def db = mongo.getDB("fb");

        def betCollection = db.getCollection(byDate ? "betDate" : "bet")

        def transactionCollection = db.getCollection(byDate ? "transactionDate" : "transaction")

        betCollection.find(byDate ? (gurateen ? new BasicDBObject("aid", gurateen) : new BasicDBObject()) : new BasicDBObject()).each { it ->
            String matchId = it.get("matchId")

            if (!byDate) {
                DBObject q = new BasicDBObject();
                q.put("matchId", matchId)
                transactionCollection.remove(q)
            }

            String clientId = it.get("clientId") as String
            float bet = it.get("bet") as float
            int betOn = it.get("betOn")    //0主1客
            if (reverse) {
                betOn = betOn == 0 ? 1 : 0
            }
            Integer betType = it.get("betType")    //0亚1欧

            def matchInfo = db.getCollection("result").findOne(new BasicDBObject().append("matchId", matchId).append("cid", "18"))

            if (!matchInfo) {
//                throw new RuntimeException(matchId + " not found")
                println matchId + " not started yet"
                return
            }

            int resultRA = matchInfo.get("resultRA") as int
            int resultRB = matchInfo.get("resultRB") as int
            Date matchTime = matchInfo.get("time") as Date

            if(it.get("abFlag")==null){
                println "abFlag is null, skip"
                return
            }
            int abFlag = it.get("abFlag") as int
//            if (abFlag == 0) {
//                println "abFlag is 0, skip"
//                return
//            }
            def delta = null;

            ObjectId oid = it.get("_id")
            if (betType == 0) {
                if (it.get("ch") == null) {
                    throw new RuntimeException(matchId + " ch not found")
                    return
                }

                int type = it.get("ch") as int

                float h1 = it.get("h1") as float
                float h2 = it.get("h2") as float

                if (abFlag == 2) {//swap
                    float temp = h1;
                    h1 = h2;
                    h2 = temp;
                }
                float result = handicap(type, resultRA, resultRB, betOn)
                if (result == 0) {
                    delta = 0
                }

                if (result > 0) {
                    delta = bet * result * ((betOn == 0 ? h1 : h2))
                }

                if (result < 0) {
                    delta = bet * result
                }
                def prefix = (type >= 0 ? "" : "受让")

                it.removeField("_id")
                it.removeField("cid")
                it.betOnDisplay = betOn == 0 ? "主" : "客"
                it.typeDispaly = prefix + GoalCn[Math.abs(type)]
            } else {
                float w2 = matchInfo.get("w2") as float
                float p2 = matchInfo.get("p2") as float
                float l2 = matchInfo.get("l2") as float

                if (resultRA == resultRB && betOn == 0) {
                    delta = bet * (p2 - 1)
                } else if (resultRA > resultRB && betOn == 1) {
                    delta = bet * (w2 - 1)
                } else if (resultRA < resultRB && betOn == -1) {
                    delta = bet * (l2 - 1)
                } else {
                    delta = -bet
                }
            }

            transactionCollection.save(new BasicDBObject()
                    .append("matchId", matchId)
                    .append("bet", bet)
                    .append("delta", delta)
                    .append("clientId", clientId)
                    .append("resultRA", resultRA)
                    .append("resultRB", resultRB)
                    .append("betInfo", it)
                    .append("matchTime", matchTime)
            )

//            betCollection.update(new BasicDBObject("_id", new ObjectId(oid.toString())), new BasicDBObject().append("\$set", new BasicDBObject("status", "processed")))
        }

//        if (byDate) {
//            def t = transactionCollection.find(new BasicDBObject("betInfo.aid", gurateen))
//
//            def sum = 0
//            def delta = 0
//            def count = 0
//
//            t.each {
//                delta += it.get("delta") as double
//                sum += it.get("bet") as int
//                count++
//            }
//
//            println "total: " + count
//            println "net: " + delta
//            if (sum != 0)
//                println "profit: " + delta * 100 / sum + "%"
//        }
    }

    public static void main(String[] args) {
        def mongo = new Mongo("rm4", 15000)
        Settle settle = new Settle(mongo)
        settle.settle(args[0], Boolean.valueOf(args[1]), Boolean.valueOf(args[2]))
    }
}