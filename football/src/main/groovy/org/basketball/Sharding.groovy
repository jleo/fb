package org.basketball

import Util.MongoDBUtil
import com.mongodb.BasicDBObject
import org.bson.types.ObjectId

import java.util.concurrent.*

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-9
 * Time: 下午3:45
 * To change this template use File | Settings | File Templates.
 */
class Sharding {
    private MongoDBUtil mongoDBUtil


    final static BlockingQueue tasks = new ArrayBlockingQueue<>(30);

    public static void main(String[] args) {
        Sharding gameLogParser = new Sharding()

        Thread.start {
            def output = new File("/Users/jleo/list.txt")
            output.eachLine {
                def url = "http://www.basketball-reference.com" + it
                tasks.put([url: url, date: Date.parse("yyyyMMdd", it.replaceAll("/boxscores/pbp/", "")[0..7])])
            }
        }

        ExecutorService executorService = Executors.newFixedThreadPool(11);
        10.times {
            executorService.submit(new Runnable() {

                @Override
                void run() {
                    while (true) {
                        def task = tasks.poll(30, TimeUnit.SECONDS)
                        def url = task.url
                        def date = task.date

                        parse(url, date)
                    }
                }
            })
        }
        executorService.shutdown()
    }

    Sharding() {
        this.mongoDBUtil = MongoDBUtil.getInstance("localhost", "15000", "bb")
    }

    static def parse = { url, date ->
        statA = [:].withDefault {
            return 0
        }
        statB = [:].withDefault {
            return 0
        }

        def urlShorten = url.replaceAll("/boxscores/pbp/", "").replaceAll(".html", "")
        def events = []

        def c = mongoDBUtil.findAllCursor(new BasicDBObject("url", urlShorten), null, "log").sort(new BasicDBObject("sec": -1))
        int scoreA, scoreB
//            int lastScoreTotal = 0

        c.each { it ->
            String event = it.get("eventBoth")
            String eventA = it.get("eventA")
            String eventB = it.get("eventB")
            int quarter = it.get("quarter")
            String time = it.get("time")

            ObjectId id = it.get("_id")

//                String score = it.get("score")
//                if (score) {
//                    def splitted = score.split("-")
//                    scoreA = splitted[0] as int
//                    scoreB = splitted[1] as int
//                    lastScoreTotal = scoreA + scoreB
//                }

            def found = keyEvent.findResult { ke ->
                def found = null

                if (eventA && eventA?.indexOf(ke) != -1) {
                    found = [ke: keyEventAbbr.get(ke), team: "A"]
                }
                if (eventB && eventB?.indexOf(ke) != -1) {
                    found = [ke: keyEventAbbr.get(ke), team: "B"]
                }
                if (!found)
                    return null
                else
                    return found
            }

            if (time.indexOf("Overtime") != -1)
                return

//                def split = time.split(":")

//                int minute = split[0] as int
//                int second = (split[1] as double) as int

//                int sec = (4 - quarter) * 12 * 60 + minute * 60 + second
            if (found) {
//                    Event f = new Event()
//                    f.quarter = quarter
//
//                    f.seconds = minute * 60 + second
//
//                    f.keyEvent = found.ke
//                    f.team = found.team
//                    events << f

                if (found.team == "A")
                    statA.put(found.ke, statA.get(found.ke) + 1)
                else
                    statB.put(found.ke, statB.get(found.ke) + 1)
            }

            if (event) {

            } else {
                mongoDBUtil.update(new BasicDBObject("_id", id), new BasicDBObject("\$set", new BasicDBObject("ae": new BasicDBObject(statA)).append("be", new BasicDBObject(statB))), "log", true)
//                    mongoDBUtil.update(new BasicDBObject("_id", id), new BasicDBObject("\$set", new BasicDBObject("sec", sec)), "log", true)
            }
        }
    }
}

