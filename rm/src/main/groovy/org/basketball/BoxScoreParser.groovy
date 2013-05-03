package org.basketball

import Util.MongoDBUtil
import com.mongodb.BasicDBList
import com.mongodb.BasicDBObject
import org.ccil.cowan.tagsoup.AutoDetector

import java.util.concurrent.*

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-3-12
 * Time: 下午7:11
 * Let's RocknRoll
 */
class BoxScoreParser {
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
        BoxScoreParser gameLogParser = new BoxScoreParser()

        Thread.start {
            def output = new File("/Users/jleo/Dropbox/nba/meta/list1980-2013.txt")
            output.eachLine {
                def url = "http://www.basketball-reference.com" + it
                tasks.put([url: url, date: Date.parse("yyyyMMdd", it.replaceAll("/boxscores/", "")[0..7])])
            }
        }

        ExecutorService executorService = Executors.newFixedThreadPool(11);
        20.times {
            executorService.submit(new Runnable() {

                @Override
                void run() {
                    while (true) {
                        def task = tasks.poll(30, TimeUnit.SECONDS)
                        def url = task.url.replaceAll("pbp/", "")
                        def date = task.date

                        try {
                            gameLogParser.parse(url, date)
                        } catch (e) {
                            println url
                            e.printStackTrace()
                        }
                    }
                }
            })
        }
        executorService.shutdown()
    }

    BoxScoreParser() {
        this.mongoDBUtil = MongoDBUtil.getInstance("rm4", "15000", "bb")
    }

    public void parse(String url, date) {
        if (date < Date.parse("yyyyMMdd", "19961001"))
            return

        def text = new URL(url).text
        def html = asHTML(text)

        def list = []
        text.eachMatch("div_.*?_basic") {
            list << it.replaceAll("div_|_basic", "")
        }

        def t = [:]

        def refrees = []
        def attendee = 0
        def matchTime = ""
        def timeAndDate = ""
        def court = ""

        def q1a, q2a, q3a, q4a, q1b, q2b, q3b, q4b, q5b, q5a, fa, fb, oa, ob
        html.breadthFirst().each { node ->
            list.each { abbr ->
                if (node.@id in [abbr + "_basic", abbr + "_advanced"])
                    t.put(node.@id, node)
            }

            if (node.name() == "table" && node.@class == "margin_top small_text") {
                int refreeCount = (node[0].children()[0].children()[1].children().size() + 1) / 2
                refrees = (1..refreeCount).collect { n ->
                    node[0].children()[0].children()[1].children()[(n - 1) * 2].attributes.href - "/referees/" - ".html"
                }
                if (node[0].children().size() >= 2)
                    attendee = (node[0].children()[1].children()[1].children()[0] - ",") as int

                if (node[0].children().size() >= 3)
                    matchTime = node[0].children()[2].children()[1].children()[0].toString()
            }

            if (node.name() == "td" && node.@class == "align_center padding_bottom small_text") {
                if (node[0].children().size() == 3) {
                    timeAndDate = node[0].children()[0]
                    court = node[0].children()[2]
                }
            }

            if (node.name() == "table" && node.@class == "nav_table stats_table" && node.@id.toString() == "") {
                q1b = node[0].children()[2].children()[1].children()[0].toString() as int
                q2b = node[0].children()[2].children()[2].children()[0].toString() as int
                q3b = node[0].children()[2].children()[3].children()[0].toString() as int
                q4b = node[0].children()[2].children()[4].children()[0].toString() as int
                q5b = node[0].children()[2].children()[5].children()[0].toString() as int

                fb = q1b + q2b + q3b + q4b

                q1a = node[0].children()[3].children()[1].children()[0].toString() as int
                q2a = node[0].children()[3].children()[2].children()[0].toString() as int
                q3a = node[0].children()[3].children()[3].children()[0].toString() as int
                q4a = node[0].children()[3].children()[4].children()[0].toString() as int
                q5a = node[0].children()[3].children()[5].children()[0].toString() as int

                fa = q1a + q2a + q3a + q4a
            }
        }
        def map = [:].withDefault {
            [:]
        }

        try {
            mongoDBUtil.upsert([match: url] as BasicDBObject, new BasicDBObject("\$set", ([refrees: refrees as BasicDBList] as BasicDBObject)
                    .append("q1a", q1a)
                    .append("q2a", q2a)
                    .append("q3a", q3a)
                    .append("q4a", q4a)
                    .append("q1b", q1b)
                    .append("q2b", q2b)
                    .append("q3b", q3b)
                    .append("q4b", q4b)
                    .append("fa", fa)
                    .append("fb", fb)
                    .append("court", court).append("timeAndDate", timeAndDate).append("attendee", attendee).append("time", matchTime)), true, "games")

            t.each { abbr, node ->
                int count = 0
                node.tbody.tr.each { c ->
                    count++
                    if (count != 6) {
                        boolean startup = false;
                        if (count < 6)
                            startup = true
                        def name = c[0].children()[0].children()[0].children()[0].toString()
                        def playerId = c[0].children()[0].children()[0].attributes.href
                        playerId = playerId[playerId.lastIndexOf("/") + 1..-1] - ".html"

                        map.get(playerId).put("name", name)
                        map.get(playerId).put("startup", startup)
                        if (node.@id.toString().contains("basic")) {
                            ['MP', 'FG', 'FGA', 'FG%', '3P', '3PA', '3P%', 'FT', 'FTA', 'FT%', 'ORB', 'DRB', 'TRB', 'AST', 'STL', 'BLK', 'TOV', 'PF', 'PTS', '+/-'].eachWithIndex { stat, idx ->
                                if (!c[0].children()[idx + 1])
                                    return

                                def s = c[0].children()[idx + 1]?.children()[0]?.toString()
                                if (stat == "MP") {
                                    def time = s.split(":")
                                    s = (time[0] as int) * 60 + (time[1] as int)
                                } else {
                                    if (s)
                                        s = s as float
                                    else
                                        s = 0
                                }

                                map.get(playerId).put(stat, s)
                            }
                        } else {//advanced
                            ['MP', 'TS%', 'eFG%', 'ORB%', 'DRB%', 'TRB%', 'AST%', 'STL%', 'BLK%', 'TOV%', 'USG%', 'ORtg', 'DRtg'].eachWithIndex { stat, idx ->
                                def s = c[0].children()[idx + 1].children()[0]?.toString()
                                if (stat == "MP") {
                                    def time = s.split(":")
                                    s = (time[0] as int) * 60 + (time[1] as int)
                                } else {
                                    if (s)
                                        s = s as float
                                    else
                                        s = 0
                                }

                                map.get(playerId).put(stat, s)
                            }
                        }
                        map.get(playerId).put("team", abbr.toString().split("_")[0])
                    }
                }
            }
        } catch (e) {
            e.printStackTrace()
        }

        map.each { playerId, stats ->
            mongoDBUtil.upsert([match: url, playerId: playerId] as BasicDBObject, new BasicDBObject("\$set", (stats as BasicDBObject).append("playerId", playerId).append("match", url)), true, "stat")
            println "saved " + url + ", " + playerId
        }
    }
}
