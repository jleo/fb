package org.basketball

import Util.MongoDBUtil
import com.mongodb.BasicDBObject

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-9
 * Time: 下午9:32
 * To change this template use File | Settings | File Templates.
 */
class Fix {
    public static void main(String[] args) {
        MongoDBUtil mongoDBUtil = MongoDBUtil.getInstance("rm4", "15000", "bb")
        def c = mongoDBUtil.findAllCursor(new BasicDBObject(), new BasicDBObject().append("_id", 1).append("quarter", 1).append("time", 1).append("_id", 1), "log")

        c.each { it ->
            String time = it.get("time")

            if (time.indexOf("Overtime") != -1)
                return

            int quarter = it.get("quarter") as int
            def split = time.split(":")

            int minute = split[0] as int
            int second = (split[1] as double) as int


            int sec = (4 - quarter) * 12 * 60 + minute * 60 + second
            def id = it.get("_id")
            mongoDBUtil.update(new BasicDBObject("_id", id), new BasicDBObject("\$set", new BasicDBObject("sec", sec)), "log2",false)
        }
    }
}
