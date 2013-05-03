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
        (1..4).each { quarter ->
            int second = 2880 - quarter * 720
            MongoDBUtil mongoDBUtil = MongoDBUtil.getInstance("rm4", "15000", "bb");
            def output = new File("/Users/jleo/Dropbox/nba/meta/gameList-1996.txt")
            def last

            def count = 0

            output.eachLine { line ->
                if (mongoDBUtil.findOne([match: line] as BasicDBObject, "end" + quarter))
                    return

                def cursor = mongoDBUtil.findAllCursor((new BasicDBObject(['score': ['\$exists': true] as BasicDBObject]).append("sec", ['\$gte': second] as BasicDBObject)).append("url", line), null, "log").sort([sec: 1] as BasicDBObject).limit(1)
                last = cursor.next()
                last.put("order", count)

                mongoDBUtil.update([match: line] as BasicDBObject, last, "end" + quarter + "", true)
                count++
            }
        }
    }
}
