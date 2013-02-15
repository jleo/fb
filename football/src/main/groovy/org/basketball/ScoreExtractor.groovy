package org.basketball

import Util.MongoDBUtil
import com.mongodb.BasicDBObject
import org.bson.types.ObjectId

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-9
 * Time: 下午9:32
 * To change this template use File | Settings | File Templates.
 */
class ScoreExtractor {
    public static void main(String[] args) {
        MongoDBUtil mongoDBUtil = MongoDBUtil.getInstance("localhost", "27017", "bb")
        def c = mongoDBUtil.findAllCursor(new BasicDBObject("score", new BasicDBObject("\$exists", false)), new BasicDBObject().append("event", 1).append("_id", 1), "log")
        c.each { it ->
            String event = it.get("event")
            ObjectId id = it.get("_id")
            event = event.replaceAll("\\+[1-3]", "")
            def score = event.find("[0-9]*[0-9]*[0-9]-[0-9]*[0-9]*[0-9]")
            mongoDBUtil.update(new BasicDBObject("_id", id), new BasicDBObject().append("\$set", new BasicDBObject("score", score)), "log")
        }
    }
}
