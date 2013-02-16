package org.basketball

import Util.MongoDBUtil
import com.mongodb.BasicDBObject

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-13
 * Time: 下午12:33
 * To change this template use File | Settings | File Templates.
 */
class Converter {

    public static void main(String[] args) {

        MongoDBUtil mongoDBUtil = MongoDBUtil.getInstance("rm4", "15000", "bb")
        if (args[0] == "1")
            updateQuarter(mongoDBUtil, 2160, 2880, 1)


        if (args[0] == "2")
            updateQuarter(mongoDBUtil, 1440, 2160, 2)

        if (args[0] == "3")
            updateQuarter(mongoDBUtil, 720, 1440, 3)

        if (args[0] == "4")
            updateQuarter(mongoDBUtil, 0, 720, 4)
    }

    private static void updateQuarter(mongoDBUtil, to, from, quarter) {
        def output = new File("/Users/jleo/list.txt")
        output.eachLine { line ->
            int scoreA = 0
            int scoreB = 0

            line = line.replaceAll("/boxscores/pbp/", "").replaceAll(".html", "")
            mongoDBUtil.findAllCursor([url: line, sec: ["\$gte": to, "\$lt": from]] as BasicDBObject, new BasicDBObject("diffA", 1).append("diffB", 1), "log").sort(["sec": -1] as BasicDBObject).each { c ->
                String diffA = c.get("diffA")
                String diffB = c.get("diffB")

                if (diffA.indexOf("+") != -1) {
                    diffA = diffA - "+"
                    scoreA += diffA as int
                }

                if (diffB.indexOf("+") != -1) {
                    diffB = diffB - "+"
                    scoreB += diffB as int
                }

                println scoreA
                println scoreB

                println "------------"
            }

            mongoDBUtil.update([url: line, sec: to] as BasicDBObject, ['\$set': ["q${quarter}a": scoreA, "q${quarter}b": scoreB]] as BasicDBObject, "log", true)
        }
    }
}
