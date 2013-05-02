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
class BoxInfoParser {
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
        BoxInfoParser gameLogParser = new BoxInfoParser()

        Thread.start {
            def output = new File("/Users/jleo/Dropbox/nba/meta/list1980-2013.txt")
            output.eachLine {
                def url = "http://www.basketball-reference.com" + it
                tasks.put([url: url, date: Date.parse("yyyyMMdd", it.replaceAll("/boxscores/", "")[0..7])])
            }
        }

        ExecutorService executorService = Executors.newFixedThreadPool(11);
        1.times {
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

    BoxInfoParser() {
        this.mongoDBUtil = MongoDBUtil.getInstance("localhost", "27017", "bb")
    }

    public void parse(String url, date) {
        if (date < Date.parse("yyyyMMdd", "19971110"))
            return

        def text = new URL(url).text
        def html = asHTML(text)

        def list = []


        def refrees = []
        def attendee = 0
        def time = ""

        html.breadthFirst().each { node ->

            if (node.name() == "table" && node.@class == "margin_top small_text") {
                int refreeCount = (node[0].children()[0].children()[1].children().size() + 1) / 2
                refrees = (1..refreeCount).collect { n ->
                    node[0].children()[0].children()[1].children()[(n-1)*2].attributes.href - "/referees/" - ".html"
                }
                attendee = (node[0].children()[1].children()[1].children()[0] - ",") as int
                if (node[0].children().size() >= 3)
                    time =  node[0].children()[2].children()[1].children()[0].toString()
            }
        }


        mongoDBUtil.upsert([match: url] as BasicDBObject, new BasicDBObject("\$set", ([refrees: refrees as BasicDBList] as BasicDBObject).append("attendee", attendee).append("time", time)), true, "games")
    }
}
