package org
/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 12-12-29
 * Time: 下午2:10
 * To change this template use File | Settings | File Templates.
 */
import com.mongodb.BasicDBObject
import com.mongodb.Mongo



db = new Mongo("rm4", 15000).getDB("fb");

def collection = db.getCollection("fbraw")

def start = Date.parse("yyyy-MM-dd", "2013-01-07")
def end = Date.parse("yyyy-MM-dd", "2013-01-10")

def toReturnRate = { w1, p1, l1 ->
    return Double.parseDouble(w1) * Double.parseDouble(p1) * Double.parseDouble(l1) / (Double.parseDouble(w1) * Double.parseDouble(p1) + Double.parseDouble(w1) * Double.parseDouble(l1) + Double.parseDouble(l1) * Double.parseDouble(p1))
}

def join = { Object... obj ->
    def sum = ""
    obj.each {
        if (obj) {
            sum += obj
        } else {
            sum += ""
        }
    }
}
def index = 0
(start..end).each { date ->
    println date
    def c = collection.find(new BasicDBObject("token", date.format("yyyy-MM-dd")))

    if (c.count() == 0)
        return

    def matcher = [:]
    def odd = [:]
    c.each { it ->
        String html = it.get("html").replaceAll("\r\n,", ",")
        if (html.indexOf("var fAllOddsCount") != -1) {
            html.eachLine { line ->
                if (line =~ /\.split\(','\)/) {
                    def quote1 = line.indexOf("\"")
                    line = line.substring(quote1 + 1, line.length() - 13)
                    def split = line.split(",")
                    odd.put(split[2], split)
                }
            }
        }
        if (html.indexOf("var AllMatchCount") != -1) {
            html.eachLine { line ->
                if (line =~ /\.split\(','\)/) {
                    def quote1 = line.indexOf("\"")
                    try {
                        line = line.substring(quote1 + 1, line.length() - 13)
                    } catch (e) {
                        println html
                        e.printStackTrace()
                    }
                    def split = line.split(",")
                    matcher.put(split[0], split)
                }
            }
        }
    }

    def finalResult = odd.collect { cmId, oddData ->
        def matchId = oddData[0]
        def r = [* oddData, * matcher[matchId]]
        def rr = toReturnRate(r[3], r[4], r[5])
        return ["returnRate": rr,
                "wr": rr / Double.parseDouble(r[3]),
                "pr": rr / Double.parseDouble(r[4]),
                "lr": rr / Double.parseDouble(r[5]),
                "matchId": r[0],
                "cid": r[1],
                "w1": r[3] as float,
                'p1': r[4] as float,
                'l1': r[5] as float,
                'w2': r[6] as float,
                'p2': r[7] as float,
                'l2': r[8] as float,
                "mtype": join(r[15], r[16], r[17]),
                'time': r[19],
                'tidA': r[20],
                'tNameA': join(r[21], r[22], r[23]),
                'tidB': r[24],
                'tNameB': join(r[25], r[26], r[27]),
                "tRankA": r[28],
                "tRankB": r[29],
                "resultRA": r[33] as int,
                "resultRB": r[34] as int,
                "resultPA": r[35] as int,
                "resultPB": r[36] as int,
                "wa1": r[40] as float,
                "pa1": r[41] as float,
                "la1": r[42] as float,
                "wa2": r[43] as float,
                "pa2": r[44] as float,
                "la2": r[45] as float]
    }

    def save = db.getCollection("result")
    def converted = finalResult.collect {
        new BasicDBObject(it)
    }
    save.insert(converted)
}




