package org.basketball

import Util.MongoDBUtil
import com.mongodb.BasicDBObject

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-23
 * Time: 上午11:54
 * To change this template use File | Settings | File Templates.
 */
class Feature2 {
    public static void main(String[] args) {
        int second = args[0] as int
        int quarter = args[1] as int
        MongoDBUtil mongoDBUtil = MongoDBUtil.getInstance("rm4", "15000", "bb");
        def output = new File("/Users/jleo/list.txt")
        def last

        def map = [:].withDefault { return 0 }
        def countMap = [:].withDefault { return 0 }
        def count = 0
        def featureName = "dr"

        output.eachLine { line ->

            line = line.replaceAll("/boxscores/pbp/", "").replaceAll(".html", "")
            def cursor = mongoDBUtil.findAllCursor((new BasicDBObject([:]).append("sec", ['\$gte': second] as BasicDBObject)).append("url", line), null, "log").sort([sec: 1] as BasicDBObject).limit(1)
            last = cursor.next()
            last.put("order", count)

            mongoDBUtil.insert(last, "end"+quarter)
            count++
        }
    }
}
