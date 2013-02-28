package org.basketball
import Util.MongoDBUtil
import com.mongodb.BasicDBList
import com.mongodb.BasicDBObject
/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-14
 * Time: 下午4:30
 * To change this template use File | Settings | File Templates.
 */
class BPNNTrainer {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        def numOfFeature = Sharding.getKeyEventAbbr().values().asList()[0..12].size() * 2

        BP bp = new BP(numOfFeature, 10, 28);


        int number = 0;
        MongoDBUtil mongoDBUtil = MongoDBUtil.getInstance(args[0], args[1], "bb");
        1.times {
            mongoDBUtil.findAllCursor((["\$or": [["ae": ["\$exists": true] as BasicDBObject] as BasicDBObject, ["be": ["\$exists": true] as BasicDBObject] as BasicDBObject] as BasicDBList] as BasicDBObject).append("url", ['\$lte': '200103300CLE'] as BasicDBObject).append("sec", 720), null, "log").each {
                if (!it.get("total"))
                    return

                double[] stats = new double[numOfFeature];
                int index = 0
                Sharding.keyEventAbbr.values().asList()[0..12].each { abbr ->
                    def countA = it.get("ae").get(abbr)
                    if (countA) {
                        countA = countA as int
                        stats[index] += countA
                    } else {
                        stats[index] = 0
                    }
                    index++
                    def countB = it.get("be").get(abbr)
                    if (countB) {
                        countB = countB as int
                        stats[index] += countB
                    } else {
                        stats[index] = 0
                    }
                    index++
                }
//            stats[index] = (it.get("sec") as int) / 10

                def scores = it.get("score").split("-")
                int current = (scores[0] as int) + (scores[1] as int)
//            stats[index] = current

//            index++

                int result = (it.get("total") - current) / 3
                println result
                double[] real = new double[28];
                real[result] = 1
                bp.train(stats, real);
                number++

                if (number % 1000 == 0)
                    println number
            }
        }
        System.out.println("训练完毕");

//        def tests = mongoDBUtil.findAllCursor((["\$or": [["ae": ["\$exists": true] as BasicDBObject] as BasicDBObject, ["be": ["\$exists": true] as BasicDBObject] as BasicDBObject] as BasicDBList] as BasicDBObject).append("url", ['\$gt': '201210300CLE'] as BasicDBObject).append("sec", 720), null, "log")
////        def tests = mongoDBUtil.findAllCursor((["\$or": [["ae": ["\$exists": true] as BasicDBObject] as BasicDBObject, ["be": ["\$exists": true] as BasicDBObject] as BasicDBObject] as BasicDBList] as BasicDBObject).append("url", ['\$lte': '200103300CLE'] as BasicDBObject).append("sec", 720), null, "log")
//        tests = tests.sort(new BasicDBObject(url: 1).append("sec", -1))
//        def count = tests.count()
//        def hit = new AtomicInteger()
//        tests.each {
//            double[] stats = new double[numOfFeature];
//            int index = 0
//            Sharding.keyEventAbbr.values().asList()[0..12].each { abbr ->
//                def countA = it.get("ae").get(abbr)
//                if (countA) {
//                    countA = countA as int
//                    stats[index] += countA
//                } else {
//                    stats[index] = 0
//                }
//                index++
//                def countB = it.get("be").get(abbr)
//                if (countB) {
//                    countB = countB as int
//                    stats[index] += countB
//                } else {
//                    stats[index] = 0
//                }
//                index++
//            }
////            stats[index] = (it.get("sec") as int) / 10
//
//            def scores = it.get("score").split("-")
//            int current = (scores[0] as int) + (scores[1] as int)
////            stats[index] = current
////
////            index++
//
//            double[] result = bp.test(stats)
//
//            double max = -1;
//            int maxIndex = 0;
//            for (int i = 0; i < result.length; i++) {
//                double c = result[i];
//                if (c > max) {
//                    max = c
//                    maxIndex = i;
//                }
//            }
//            println it.get("url")
//            println it.get("sec")
//            int total = it.get("total")
//            println maxIndex
//            println "result:" + ((maxIndex * 3) + current) + ", actual:" + total
//
//            if (maxIndex * 5 + 100 < total && maxIndex * 5 + 105 > total) {
//                hit++
//            }
//        }
//
//        println hit / count * 100
    }

}
