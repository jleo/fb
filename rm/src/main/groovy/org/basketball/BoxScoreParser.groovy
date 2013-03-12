package org.basketball

import Util.MongoDBUtil
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

    BoxScoreParser() {
        this.mongoDBUtil = MongoDBUtil.getInstance("localhost", "27017", "bb")
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
        println t
    }
}
