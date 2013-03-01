package org.basketball

import Util.MongoDBUtil
import com.mongodb.BasicDBObject

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-3-1
 * Time: 下午6:02
 * Let's RocknRoll
 */
class CrossValidation {
    public static void main(String[] args) {
        boolean cross = false
        MongoDBUtil mongoDBUtil = MongoDBUtil.getInstance("rm4", "15000", "bb");

        def path = "/Users/jleo/list2.txt"

        def output = new File(path)

        int count = output.readLines().size()

        def allTraining = new double[count][JooneScoreTrend.inputSize]
        def allReal = new double[count][JooneScoreTrend.outputSize]
        int idx = 0

        output.eachLine { line ->
            int startFrom = 0
            def last = ["ae": [:].withDefault { 0 }, "be": [:].withDefault { 0 }, "score": 0]
            line = line.replaceAll("/boxscores/pbp/", "").replaceAll(".html", "")
            def cursor = mongoDBUtil.findAllCursor(new BasicDBObject("url", line), null, "end12").sort([order: 1] as BasicDBObject).limit(1)
            JooneScoreTrend.add(cursor, allReal, 0, allTraining, false, true, idx, last)

            startFrom += JooneScoreTrend.numberOfFeature * 2 + 1
            cursor = mongoDBUtil.findAllCursor(new BasicDBObject("url", line), null, "end22").sort([order: 1] as BasicDBObject).limit(1)
            JooneScoreTrend.add(cursor, allReal, startFrom, allTraining, false, true, idx, last)

            startFrom += JooneScoreTrend.numberOfFeature * 2 + 1
            cursor = mongoDBUtil.findAllCursor(new BasicDBObject("url", line), null, "end32").sort([order: 1] as BasicDBObject).limit(1)
            JooneScoreTrend.add(cursor, allReal, startFrom, allTraining, false, false, idx, last)

            cursor = mongoDBUtil.findAllCursor(new BasicDBObject("url", line), null, "end42").sort([order: 1] as BasicDBObject).limit(1)
            JooneScoreTrend.add(cursor, allReal, 0, allTraining, true, true, idx, last)

            idx++
            if (idx % 100 == 0)
                println idx
        }

        FileOutputStream stream = new FileOutputStream("crosstrain");
        ObjectOutputStream out = new ObjectOutputStream(stream);
        out.writeObject(allTraining);
        out.close()

        stream = new FileOutputStream("crossreal");
        out = new ObjectOutputStream(stream);
        out.writeObject(allReal);
        out.close()
    }
}
