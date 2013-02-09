package org.basketball

import Util.MongoDBUtil
import com.mongodb.BasicDBObject
import org.ccil.cowan.tagsoup.AutoDetector

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

    public static void main(String[] args) {
        GameLogParser gameLogParser = new GameLogParser()

        def output = new File("/Users/jleo/list.txt")
        output.eachLine {
            def url = "http://www.basketball-reference.com" + it
            gameLogParser.parse(url, Date.parse("yyyyMMdd", it.replaceAll("/boxscores/pbp/", "")[0..7]))
        }
    }

    GameLogParser() {
        this.mongoDBUtil = MongoDBUtil.getInstance("localhost", "27017", "bb")
    }

    public void parse(String url, date) {
        println url
        def text = new URL(url).text
        def html = asHTML(text)

        /////*[@id="q1"]/th
        def table = html.depthFirst().find {
            it.@class == "no_highlight stats_table"
        }


        def teamA, teamB

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
            def events = null
            if (it.children().size() >= 2)
                events = it.children()[1..-1]
            else
                events = it.children()[1]

            def eventsString = ""
            events.each { eventNode ->
                eventsString += eventNode.text()
            }

            def doc = new BasicDBObject().append("time", timeLeft).append("quarter", currentQuarter).append("event", eventsString).append("date", date)
            mongoDBUtil.insert(doc, "log")
        }
    }
}

