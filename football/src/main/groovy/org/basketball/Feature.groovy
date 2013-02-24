package org.basketball

import Util.MongoDBUtil
import com.mongodb.BasicDBObject
import org.Visual
import org.jfree.data.xy.XYSeries

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-23
 * Time: 上午11:54
 * To change this template use File | Settings | File Templates.
 */
class Feature {
    public static void main(String[] args) {
        MongoDBUtil mongoDBUtil = MongoDBUtil.getInstance("rm4", "15000", "bb");
        def output = new File("/Users/jleo/list.txt")
        def last

        def map = [:].withDefault { return 0 }
        def countMap = [:].withDefault { return 0 }
        def count = 0
        def featureName = "dr"

        output.eachLine { line ->
            if (count == 5000)
                return

            line = line.replaceAll("/boxscores/pbp/", "").replaceAll(".html", "")
            def cursor = mongoDBUtil.findAllCursor((new BasicDBObject([:]).append("sec", ['\$gte': 720] as BasicDBObject)).append("url", line), null, "log").sort([sec: 1] as BasicDBObject).limit(1)
            last = cursor.next()

//            def cursor2 = mongoDBUtil.findAllCursor((new BasicDBObject([:]).append("sec", ['\$gte': 2160] as BasicDBObject)).append("url", line), null, "log").sort([sec: 1] as BasicDBObject).limit(1)
//            def first = cursor2.next()
//
//            int firstQuarter = first.get("score").split("-").sum {
//                it as int
//            }
            def scoreAandB = (last.get("score") as String).split("-")
            def scoreA = scoreAandB[0] as int
            def scoreB = scoreAandB[1] as int
//
//            def sum = scoreA + scoreB

            def fa = last.get("ae").get(featureName)
            def assistA = fa == null ? 0 : fa as int

            def fb = last.get("be").get(featureName)
            def assistB = fb == null ? 0 : fb as int

//            def featureName2 = "mkls"
//
//            def fa2 = last.get("ae").get(featureName2)
//            def assistA2 = fa2 == null ? 0 : fa2 as int
//
//            def fb2 = last.get("be").get(featureName2)
//            def assistB2 = fb2 == null ? 0 : fb2 as int

            if (last.get("total") == null) {
                println "break"
                return
            }

            int score = last.get("score").split("-").sum {
                it as int
            }

            def x = Math.abs(scoreA - scoreB)
//            def y = assistA2 + assistB2

//            def x = score
//            if (y != 0)
//                x /= y
//            else
//                return

            map.put(x, map.get(x) + last.get("total") - score)
            countMap.put(x, countMap.get(x) + 1)

            if (count++ % 100 == 0) {
                println count
            }
        }

        def visual = new Visual()
        visual.test(featureName) { def collection, XYSeries dataSeries ->
            map.each { k, v ->
                dataSeries.add(k, v / countMap.get(k))
            }

        }
    }
}
