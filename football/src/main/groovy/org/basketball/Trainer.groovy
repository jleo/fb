package org.basketball

import Util.MongoDBUtil
import com.enigmastation.ml.bayes.Classifier
import com.mongodb.BasicDBList
import com.mongodb.BasicDBObject

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-13
 * Time: 上午10:08
 * To change this template use File | Settings | File Templates.
 */
class Trainer {

    public static void main(String[] args) {

        final Classifier classifier = new BasketballClassifier();
        MongoDBUtil mongoDBUtil = MongoDBUtil.getInstance(args[0], args[1], "bb")

        int index = 0
        mongoDBUtil.findAllCursor(["\$or": [["ae": ["\$exists": true] as BasicDBObject] as BasicDBObject, ["be": ["\$exists": true] as BasicDBObject] as BasicDBObject] as BasicDBList] as BasicDBObject, null, "log").each {
            index++
            if (!it.get("total"))
                return
            int score = it.get("total") as int
            classifier.train(it, score)
            if (index % 100 == 0) {
                println index
            }
        }
        println "done"
    }
}
