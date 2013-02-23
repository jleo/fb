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
        output.eachLine { line ->
            if (count == 5000)
                return

            line = line.replaceAll("/boxscores/pbp/", "").replaceAll(".html", "")
            def cursor = mongoDBUtil.findAllCursor((new BasicDBObject([:]).append("sec", ['\$gte': 1440] as BasicDBObject)).append("url", line), null, "log").sort([sec: 1] as BasicDBObject).limit(1)
            last = cursor.next()

//            def featureName = "2st"
//
//            def fa = last.get("ae").get(featureName)
//            def assistA = fa == null ? 0 : fa as int
//
//            def fb = last.get("be").get(featureName)
//            def assistB = fb == null ? 0 : fb as int

            if (last.get("total") == null) {
                println "break"
                return
            }
            def scoreAandB = last.get("score").split("-")
            int scoreA = scoreAandB[0] as int
            int scoreB = scoreAandB[1] as int

            int score = scoreA - scoreB

//            def x = assistA + assistB
            def x = score
            map.put(x, map.get(x) + last.get("total"))
            countMap.put(x, countMap.get(x) + 1)

            if (count++ % 100 == 0) {
                println count
            }
        }

        def visual = new Visual()
        visual.test() { def collection, XYSeries dataSeries ->
            map.each { k, v ->
                dataSeries.add(k, v / countMap.get(k))
            }

        }
    }
}
