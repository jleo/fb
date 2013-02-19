package org.basketball

import Util.MongoDBUtil
import com.mongodb.BasicDBObject

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-13
 * Time: 下午12:33
 * To change this template use File | Settings | File Templates.
 */
class Converter {

    final static BlockingQueue tasks = new ArrayBlockingQueue<>(30);
    final static MongoDBUtil mongoDBUtil = MongoDBUtil.getInstance("rm4", "15000", "bb")

    public static void main(String[] args) {
        def output = new File("/Users/jleo/list.txt")

        Thread.start {
            output.eachLine { line ->
                line = line.replaceAll("/boxscores/pbp/", "").replaceAll(".html", "")
                tasks.put(line)
            }
        }

        ExecutorService executorService = Executors.newFixedThreadPool(11);
        (args[0] as int).times {
            executorService.submit(new Runnable() {

                @Override
                void run() {
                    while (true) {
                        def task = tasks.poll(30, TimeUnit.SECONDS)
                        println task

                        try {
                            updateQuarter(task, 2160, 2880, 1)
                            updateQuarter(task, 1440, 2160, 2)
                            updateQuarter(task, 720, 1440, 3)
                            updateQuarter(task, 0, 720, 4)
                        } catch (e) {
                            e.printStackTrace()
                        }
                    }
                }
            })
        }
        executorService.shutdown()
        executorService.awaitTermination(10, TimeUnit.HOURS);
    }

    private static void updateQuarter(line, to, from, quarter) {
        int scoreA = 0
        int scoreB = 0
//
//        def stats = [:].withDefault {
//            0
//        }

        mongoDBUtil.findAllCursor([url: line, sec: ["\$gte": to, "\$lt": from]] as BasicDBObject, null, "log").sort(["sec": -1] as BasicDBObject).each { c ->
//            Sharding.keyEventAbbr.values().asList()[0..Joone.numberOfFeature - 1].each { abbr ->
//                def countA = c.get("ae").get(abbr)
//
//                if (countA) {
//                    countA = countA as int
//                    stats[abbr + 'A'] = (countA as int) + (stats[abbr + 'A'] as int)
//                } else {
//                    stats[abbr + 'A'] = 0
//                }
//                def countB = c.get("be").get(abbr)
//                if (countB) {
//                    countB = countB as int
//                    stats[abbr + 'B'] = (countB as int) + (stats[abbr + 'B'] as int)
//                } else {
//                    stats[abbr + 'B'] = 0
//                }
//            }

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
        }
        mongoDBUtil.insert(([url: line, quarter: quarter, scoreA: scoreA, scoreB: scoreB]) as BasicDBObject, "quarter")
    }
}
