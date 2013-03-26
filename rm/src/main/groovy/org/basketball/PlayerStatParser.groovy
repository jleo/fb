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
class PlayerStatParser {
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
        PlayerStatParser gameLogParser = new PlayerStatParser()

        Thread.start {
            def output = new File("/Users/jleo/players.txt")
            output.eachLine {
                tasks.put([url: it])
            }
        }

        ExecutorService executorService = Executors.newFixedThreadPool(11);
        15.times {
            executorService.submit(new Runnable() {

                @Override
                void run() {
                    while (true) {
                        def task = tasks.poll(30, TimeUnit.SECONDS)
                        def url = task.url

                        try {
                            gameLogParser.parse(url)
                        } catch (e) {
                            e.printStackTrace()
                        }
                    }
                }
            })
        }
        executorService.shutdown()
    }

    PlayerStatParser() {
        this.mongoDBUtil = MongoDBUtil.getInstance("rm4", "15000", "bb")
    }

    public void parse(String url) {
        println url
        def text = new URL(url).text
        def html = asHTML(text)


        def t = [:]
        def fName = ""
        text.eachMatch("<h1>.*?</h1>") { fName = it[4..-6] }

        html.breadthFirst().each { node ->
            if (node.@id in ["totals", "advanced"])
                t.put(node.@id, node)
        }
        def map = [:].withDefault {
            [:]
        }
        def name = url[url.lastIndexOf("/") + 1..-1]
        t.each { abbr, node ->
            int countBasic = 0
            int countAdvanced = 0
            node.tbody.tr.each { c ->
                if (node.@id.toString().contains("totals")) {
                    countBasic++
                    def season = ""
                    ['Season', 'Age', 'Tm', 'Lg', 'Pos', 'G', 'GS', 'MP', 'FG', 'FGA', 'FG%', '3P', '3PA', '3P%', 'FT', 'FTA', 'FT%', 'ORB', 'DRB', 'TRB', 'AST', 'STL', 'BLK', 'TOV', 'PF', 'PTS'].eachWithIndex { stat, idx ->
                        if (stat == 'Lg')
                            return

                        def s = c[0].children()[idx].children()[0]
                        if (stat in ["Tm", "Season"]) {
                            if (!(s instanceof String))
                                s = s.text()
                        } else {
                            s = s?.toString()
                        }
                        if (stat == "Season") {
                            season = s
                        } else if (stat in ["Tm", 'Pos']) {
                            s = s as String
                        } else {
                            if (s)
                                s = s as float
                            else
                                s = 0
                        }

                        map.get(countBasic).put(stat, s)

                    }
                } else {//advanced
                    countAdvanced++
                    def season = ""
                    ['Season', 'Age', 'Tm', 'Lg', 'Pos', 'G', 'MP', 'PER', 'TS%', 'eFG%', 'ORB%', 'DRB%', 'TRB%', 'AST%', 'STL%', 'BLK%', 'TOV%', 'USG%', 'ORtg', 'DRtg', 'OWS', 'DWS', 'WS', 'WS/48'].eachWithIndex { stat, idx ->
                        if (stat == 'Lg')
                            return

                        def s = c[0].children()[idx].children()[0]

                        if (stat in ["Tm", "Season"]) {
                            if (!(s instanceof String))
                                s = s.text()
                        } else {
                            s = s?.toString()
                        }
                        if (stat == "Season") {
                            season = s
                        } else if (stat in ["Tm", 'Pos']) {
                            s = s as String
                        } else {
                            if (s)
                                s = s as float
                            else
                                s = 0
                        }

                        map.get(countAdvanced).put(stat, s)
                    }
                }
            }
        }
        map.each { season, stats ->
            mongoDBUtil.insert((stats as BasicDBObject).append("name", name).append("fname", fName), "player")
        }
    }
}
