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

        def map = [:].withDefault { return 0 }
        def countMap = [:].withDefault { return 0 }
        def count = 0
        def featureName = "mkcs"

        def cursor = mongoDBUtil.findAllCursor(new BasicDBObject(), null, "end3").sort([order: 1] as BasicDBObject)
        def countX = [:].withDefault { return 0 }
        cursor.each { last ->
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

            int score = scoreA + scoreB

//            def x = Math.abs(scoreA - scoreB)*3
            def x = assistA + assistB
//            def y = assistA2 + assistB2
//            x = (x-30)*(x-30)
//            def x = score
//            if (y != 0)
//                x /= y
//            else
//                return

            countX.put(x, countX.get(x) + 1)

            map.put(x, map.get(x) + last.get("total") - score)
            countMap.put(x, countMap.get(x) + 1)

            if (count++ % 100 == 0) {
                println count
            }
        }
        def count1 = cursor.count()
        countX.keySet().sort().each { k ->
            println k + "," + countX.get(k) / count1 * 100
        }

        def visual = new Visual()
        visual.test(featureName) { def collection, XYSeries dataSeries ->
            map.each { k, v ->
                dataSeries.add(k, v / countMap.get(k))
            }

        }
    }
}
