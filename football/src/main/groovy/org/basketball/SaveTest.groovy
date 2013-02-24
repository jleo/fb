package org.basketball

import Util.MongoDBUtil
import com.mongodb.BasicDBObject

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-18
 * Time: 下午3:22
 * Let's RocknRoll
 */
class SaveTest {
    public static void main(String[] args) {
        MongoDBUtil mongoDBUtil = MongoDBUtil.getInstance("rm4", "15000", "bb");
        JooneScoreTrend joone = new JooneScoreTrend();



        def output = new File("/Users/jleo/list.txt")
        int total = output.readLines().size()
        def count = output.readLines().findIndexOf {
            it == "/boxscores/pbp/201211040OKC.html"
        }
        def idx = count
        def allTraining = new double[total - count][JooneScoreTrend.inputSize]
        def allReal = new double[total - count][JooneScoreTrend.outputSize]
        int scanned = 0
        output.eachLine { line ->
            if (scanned >= count) {
                int startFrom = 0
                line = line.replaceAll("/boxscores/pbp/", "").replaceAll(".html", "")

                def last = ["ae": [:].withDefault { 0 }, "be": [:].withDefault { 0 }, "score": 0]

                def cursor = mongoDBUtil.findAllCursor((new BasicDBObject([:]).append("sec", ['\$gte': 2160] as BasicDBObject)).append("url", line), null, "log").sort([sec: 1] as BasicDBObject).limit(1)
                joone.add(cursor, allReal, 0, allTraining, false, true, scanned - count, last)

                startFrom += joone.numberOfFeature * 2 + 1
                cursor = mongoDBUtil.findAllCursor((new BasicDBObject([:]).append("sec", ['\$gte': 1440] as BasicDBObject)).append("url", line), null, "log").sort([sec: 1] as BasicDBObject).limit(1)
                joone.add(cursor, allReal, startFrom, allTraining, false, true, scanned - count, last)

                startFrom += joone.numberOfFeature * 2 + 1
                cursor = mongoDBUtil.findAllCursor((new BasicDBObject([:]).append("sec", ['\$gte': 720] as BasicDBObject)).append("url", line), null, "log").sort([sec: 1] as BasicDBObject).limit(1)
                joone.add(cursor, allReal, startFrom, allTraining, false, false, scanned - count, last)

                cursor = mongoDBUtil.findAllCursor((new BasicDBObject([:]).append("sec", ['\$gte': 0] as BasicDBObject)).append("url", line), null, "log").sort([sec: 1] as BasicDBObject).limit(1)
                joone.add(cursor, allReal, 0, allTraining, true, true, scanned - count, last)

                idx++
                if (idx % 100 == 0)
                    println idx
            }
            scanned++
        }
        FileOutputStream stream = new FileOutputStream("test");
        ObjectOutputStream out = new ObjectOutputStream(stream); out.writeObject(allTraining);
        out.close()

        stream = new FileOutputStream("testreal");
        out = new ObjectOutputStream(stream); out.writeObject(allReal);
        out.close()

//
    }
}
