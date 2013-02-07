package org

import com.mongodb.BasicDBObject
import com.mongodb.DBCollection
import com.mongodb.Mongo
import org.ccil.cowan.tagsoup.AutoDetector

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-1-27
 * Time: 下午2:08
 * To change this template use File | Settings | File Templates.
 */

public class ParseTrends {
    final autoDetectorPropertyName = 'http://www.ccil.org/~cowan/tagsoup/properties/auto-detector'

    public static void main(String[] args) {
        def db = new Mongo("rm4", 15000).getDB("fb");
        ParseTrends trends = new ParseTrends()
        def result = db.getCollection("result")
        def collection = db.getCollection("handicap")

        def c = result.find([cid: "18", "time": ["\$gte": new Date() - (args[0] as int), "\$lt": new Date()]] as BasicDBObject)
        def count = c.count()
        def index = 0
        c.each {
            index++
            def abFlag = it.get("abFlag")
            if (abFlag == null || abFlag == 0) {
                return
            }

            if (it.get("ch") == null)
                return

            def queryId = it.get("queryId") as long
            def matchId = it.get("matchId")

            trends.fetchHandicapHistory(abFlag, queryId, collection, matchId)
            println index + "/" + count
        }
    }

    def asHTML(content, encoding = "GB2312") {
        org.ccil.cowan.tagsoup.Parser parser = new org.ccil.cowan.tagsoup.Parser()
        parser.setProperty(autoDetectorPropertyName, [autoDetectingReader: { inputStream ->
            new InputStreamReader(inputStream, encoding)
        }
        ] as AutoDetector)
        new XmlSlurper(parser).parseText(content)
    }

    public void fetchHandicapHistory(abFlag, long queryId, DBCollection collection, matchId) {
        def handicap = collection

        def body = new URL("http://odds2.zso8.com/app/midDetailA.asp?a=" + abFlag + "&id=" + queryId + "&cid=18").getText("utf-8")
        println "http://odds2.zso8.com/app/midDetailA.asp?a=" + abFlag + "&id=" + queryId + "&cid=18"
        def html = asHTML(body)

        handicap.remove([queryId: queryId] as BasicDBObject)

        try {
            def map = [:]
            if (!body.startsWith("<script ")) {
                def size = html[0].children()[1].children[0].children[0].children.size() - 1
                if (size <= 3)
                    return

                html[0].children()[1].children[0].children[0].children[3..size].each {
                    String time = it.children[0].children[0].replaceAll(new String((char) 160), "")
                    double h1 = it.children[1].children[0].children[0].children[0] as double
                    double h2 = it.children[3].children[0].children[0].children[0] as double
                    def ch = it.children[2].children[0]

                    boolean reverse = false
                    if (ch.indexOf("受让") != -1) {
                        reverse = true
                    }
                    ch = Settle.GoalCn.findIndexOf {
                        it == ch.replaceAll("受让", "")
                    } as int
                    if (ch == -1) {
                        println ch + "not found"
                    }

                    if (reverse) {
                        ch = -ch
                    }
                    map.time = time
                    map.h1 = h1
                    map.h2 = h2
                    map.ch = ch
                    map.matchId = matchId
                    map.queryId = queryId
                    handicap.update(new BasicDBObject("matchId", matchId).append("time", time), map as BasicDBObject, true, true)
                }
            } else {
                def path = "single/" + (((queryId / 100000) as int) + 1) + "/" + queryId + ".xml";
                def xml = new XmlSlurper().parseText(new URL("http://api.zso8.com/odds/oddsdetail/" + path).getText("utf-8"))
                xml.m2.p.each {
                    String nodeText = it.text()
                    def infos = nodeText.split(",")
                    def ch = infos[0] as int
                    if (abFlag == 2)
                        ch = -ch

                    def h1 = infos[1] as double
                    def h2 = infos[2] as double
                    def time = Date.parse("yyyy-MM-dd HH:mm:ss", infos[3])

                    map.time = time
                    map.h1 = h1
                    map.h2 = h2
                    map.ch = ch
                    map.matchId = matchId
                    map.queryId = queryId
                    handicap.update(new BasicDBObject("matchId", matchId).append("time", time), map as BasicDBObject, true, true)
                }
            }


        } catch (e) {
            e.printStackTrace()
            println "c"
        }
        finally {
            println "cena ---------" + queryId
        }
    }
}








