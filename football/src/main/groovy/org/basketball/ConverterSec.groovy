package org.basketball

import Util.MongoDBUtil
import com.mongodb.BasicDBObject

import java.util.concurrent.*

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-13
 * Time: 下午12:33
 * To change this template use File | Settings | File Templates.
 */
class ConverterSec {

    final static BlockingQueue tasks = new ArrayBlockingQueue<>(30);
    final static MongoDBUtil mongoDBUtil = MongoDBUtil.getInstance("rm4", "15000", "bb")

    public static void main(String[] args) {
        def output = new File("/Users/jleo/list2.txt")

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

                        def lastScore = [0, 0]
                        try {
                            int sec = 2880
                            int order = 0
                            while (sec - 5 != 0) {
                                lastScore = updateQuarter(task, sec - 5, sec, order++, lastScore)
                                sec -= 5
                            }

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

    private static def updateQuarter(line, to, from, order, lastScore) {
        int scoreA = lastScore[0]
        int scoreB = lastScore[1]

        boolean toSave = false
        mongoDBUtil.findAllCursor([url: line, sec: ["\$gte": to, "\$lt": from]] as BasicDBObject, new BasicDBObject("diffA", 1).append("diffB", 1), "log").sort(["sec": -1] as BasicDBObject).each { c ->
            String diffA = c.get("diffA")
            String diffB = c.get("diffB")

            if (diffA.indexOf("+") != -1) {
                diffA = diffA - "+"
                scoreA += diffA as int
                toSave = true
            }

            if (diffB.indexOf("+") != -1) {
                diffB = diffB - "+"
                scoreB += diffB as int
                toSave = true
            }
        }
        if (toSave)
            mongoDBUtil.insert([url: line, to: to, scoreA: scoreA, scoreB: scoreB] as BasicDBObject, "second")
        else
            mongoDBUtil.insert([url: line, to: to, scoreA: lastScore[0], scoreB: lastScore[1]] as BasicDBObject, "second")


        return [scoreA, scoreB]
        //mongoDBUtil.update([url: line, sec: to] as BasicDBObject, ['\$set': ["q${order}a": scoreA, "q${order}b": scoreB]] as BasicDBObject, "log", true)
    }
}
