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
        MongoDBUtil mongoDBUtil = MongoDBUtil.getInstance("localhost", "27017", "bb")
        def c = mongoDBUtil.findAllCursor(new BasicDBObject(), new BasicDBObject().append("url", 1).append("_id", 1), "log")

        c.each { it ->
            String url = it.get("url")
            def id = it.get("_id")
            url = url.replaceAll("http://www.basketball-reference.com/boxscores/pbp/", "").replaceAll(".html", "")
            mongoDBUtil.update(new BasicDBObject("_id", id), new BasicDBObject("\$set", new BasicDBObject("url", url)), "log")
        }
    }
}
