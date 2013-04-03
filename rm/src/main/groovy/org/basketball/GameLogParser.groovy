package org.basketball

import Util.MongoDBUtil
import com.mongodb.BasicDBObject
import org.ccil.cowan.tagsoup.AutoDetector

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-9
 * Time: 下午3:45
 * To change this template use File | Settings | File Templates.
 */
class GameLogParser {
    private MongoDBUtil mongoDBUtil

    static final autoDetectorPropertyName = 'http://www.ccil.org/~cowan/tagsoup/properties/auto-detector'

    static def asHTML(content, encoding = "utf-8") {
        org.ccil.cowan.tagsoup.Parser parser = new org.ccil.cowan.tagsoup.Parser()
        parser.setProperty(autoDetectorPropertyName, [autoDetectingReader: { inputStream ->
            new InputStreamReader(inputStream, encoding)
        }
        ] as AutoDetector)
        new XmlSlurper(parser).parseText(content)
    }

    final static BlockingQueue tasks = new ArrayBlockingQueue<>(30);

    public static void main(String[] args) {
        GameLogParser gameLogParser = new GameLogParser()

        Thread.start {
            def output = new File("/Users/jleo/list2.txt")
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

                        gameLogParser.parse(url, date)
                    }
                }
            })
        }
        executorService.shutdown()
    }

    GameLogParser() {
        this.mongoDBUtil = MongoDBUtil.getInstance("rm4", "15000", "bb")
    }

    public void parse(String url, date) {
        println url
        def text = new URL(url).text
        def html = asHTML(text)

        /////*[@id="q1"]/th
        def table = html.depthFirst().find {
            it.@class == "no_highlight stats_table"
        }


        def teamA = null, teamB = null

        def currentQuarter = 0
        def skip = false
        table[0].children().each { it ->
            if (skip) {
                skip = false
                teamA = it.children()[1].text()
                teamB = it.children()[3].text()
                return
            }

            if (it.attributes.id in ['q1', 'q2', 'q3', 'q4']) {
                currentQuarter++
                skip = true
                return
            }

            def timeLeft = it.children()[0].children()[0]
            def eventsString = ""
            def doc = new BasicDBObject()

            if (it.children().size() == 2) {
                it.children()[1..-1].each { eventNode ->
                    eventsString += eventNode.text()
                }
                doc.append("eventBoth", eventsString)
            } else if (it.children().size() == 4) {
                return
            } else if (it.children().size() == 6) {
                def events = it.children()[1..-1]
                def eventA = events[0].text()
                def eventB = events[4].text()
                def scoreDiffA = events[1].text()
                def scoreDiffB = events[3].text()
                def score = events[2].text()

                doc.append("eventA", eventA)
                        .append("eventB", eventB)
                        .append("diffA", scoreDiffA)
                        .append("diffB", scoreDiffB)
                        .append("score", score)
            }

            int quarter = currentQuarter as int
            def split = timeLeft.split(":")

            int minute = split[0] as int
            int second = (split[1] as double) as int


            int sec = (4 - quarter) * 12 * 60 + minute * 60 + second

            doc.append("time", timeLeft)
            doc.append("quarter", currentQuarter)
                    .append("date", date).append("url", url.replaceAll("http://www.basketball-reference.com/boxscores/pbp/", "").replaceAll(".html", ""))
                    .append("teamA", teamA)
                    .append("teamB", teamB)
                    .append("sec", sec)

            mongoDBUtil.insert(doc, "log2")
        }
    }
}

