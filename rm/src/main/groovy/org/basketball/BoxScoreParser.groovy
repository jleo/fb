package org.basketball

import Util.MongoDBUtil
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
            def output = new File("/Users/jleo/list.txt")
            output.eachLine {
                def url = "http://www.basketball-reference.com" + it
                tasks.put([url: url, date: Date.parse("yyyyMMdd", it.replaceAll("/boxscores/pbp/", "")[0..7])])
            }
        }

        ExecutorService executorService = Executors.newFixedThreadPool(11);
        15.times {
            executorService.submit(new Runnable() {

                @Override
                void run() {
                    while (true) {
                        def task = tasks.poll(30, TimeUnit.SECONDS)
                        def url = task.url.replaceAll("pbp/", "")
                        def date = task.date

                        gameLogParser.parse(url, date)
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
        println url
        def text = new URL(url).text
        def html = asHTML(text)

        def list = []
        text.eachMatch("div_.*?_basic") {
            list << it.replaceAll("div_|_basic", "")
        }

        def t = [:]
        html.breadthFirst().each { node ->
            list.each { abbr ->
                if (node.@id in [abbr + "_basic", abbr + "_advanced"])
                    t.put(node.@id, node)
            }
        }
        def map = [:].withDefault {
            [:]
        }

        t.each { abbr, node ->
            int count = 0
            node.tbody.tr.each { c ->
                count++
                if (count != 6) {
                    def name = c[0].children()[0].children()[0].children()[0].toString()

                    if (node.@id.toString().contains("basic")) {
                        ['MP', 'FG', 'FGA', 'FG%', '3P', '3PA', '3P%', 'FT', 'FTA', 'FT%', 'ORB', 'DRB', 'TRB', 'AST', 'STL', 'BLK', 'TOV', 'PF', 'PTS', '+/-'].eachWithIndex { stat, idx ->
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

                            map.get(name).put(stat, s)

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

                            map.get(name).put(stat, s)
                        }
                    }
                    map.get(name).put("team", abbr.toString().split("_")[0])
                }
            }
        }

        map.each { player, stats ->
            mongoDBUtil.upsert([match: url, name: player] as BasicDBObject, new BasicDBObject("\$set", (stats as BasicDBObject).append("name", player).append("match", url)), true, "stat")
            println "saved " + url
        }
    }
}
