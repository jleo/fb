package org

import com.mongodb.BasicDBList
import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import com.mongodb.Mongo

public class Latest {
    public static void main(String[] args) {
        Latest latest = new Latest()
        latest.latest()
    }

    public void latest() {
        def matches = new URL("http://odds2.zso8.com/html/match.html").getText("utf-8")
        def odds = new URL("http://odds2.zso8.com/html/fixed.html").getText("utf-8")
        def handicaps = new URL("http://odds2.zso8.com/html/handicap.html").getText("utf-8")

        def matcher = [:]
        def odd = [:]
        def handicap = [:]



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

        odds.eachLine { line ->
            if (line =~ /\.split\(','\)/) {
                def quote1 = line.indexOf("\"")
                line = line.substring(quote1 + 1, line.length() - 13)
                def split = line.split(",")
                odd.put(split[2], split)
            }
        }

        matches.eachLine { line ->
            if (line =~ /\.split\(','\)/) {
                def quote1 = line.indexOf("\"")
                line = line.substring(quote1 + 1, line.length() - 13)
                def split = line.split(",")
                matcher.put(split[0], split)
            }
        }

        handicaps.eachLine { line ->
            if (line =~ /\.split\(','\)/) {
                def quote1 = line.indexOf("\"")
                line = line.substring(quote1 + 1, line.length() - 13)
                def split = line.split(",")
                handicap.put(split[0] + "," + split[1], split)
            }
        }

        def finalResult = odd.collect { cmId, oddData ->
            def matchId = oddData[0]
            def cId = oddData[1]

            if (!matcher[matchId])
                return

            def abFlagInitial = null
            def abFlag = null


            if ([* matcher[matchId]][19])
                abFlag = [* matcher[matchId]][19] as int

            def handicapInfo = [* handicap[matchId + "," + cId]]

            def initHandicap = null
            def currentHandicap = null
            def h1 = null
            def h2 = null

            if (handicapInfo) {
                if (handicapInfo[6])
                    initHandicap = handicapInfo[6] as int
                if (handicapInfo[5]) {
                    currentHandicap = handicapInfo[5] as int

                    if (abFlag == 2)
                        currentHandicap = -(handicapInfo[5] as int)
                    else
                        currentHandicap = handicapInfo[5] as int
                }



                if (handicapInfo[3])
                    h1 = new BigDecimal(handicapInfo[3]).toDouble()

                if (handicapInfo[4])
                    h2 = new BigDecimal(handicapInfo[4]).toDouble()
            }

            def r = [* oddData, * matcher[matchId]]
            if (r[19] && r[3] && r[4] && r[5] && r[6] && r[7] && r[8] && r[34] && r[35] && r[36] && r[37] && r[38] && r[39])
                return [h1: h1, h2: h2, abFlagInitial: abFlagInitial, abFlag: abFlag, ih: initHandicap, ch: currentHandicap, "matchId": r[0], "cid": r[1], "w1": new BigDecimal(r[3]).toDouble(), 'p1': new BigDecimal(r[4]).toDouble(), 'l1': new BigDecimal(r[5]).toDouble(), 'w2': new BigDecimal(r[6]).toDouble(), 'p2': new BigDecimal(r[7]).toDouble(), 'l2': new BigDecimal(r[8]).toDouble(), "mtype": join(r[15], r[16], r[17]), 'time': Date.parse("yyyy-MM-dd HH:mm:ss", r[19]), 'tidA': r[20], 'tNameA': join(r[21], r[22], r[23]), 'tidB': r[24], 'tNameB': join(r[25], r[26], r[27]), "tRankA": r[28], "tRankB": r[29], "wa1": new BigDecimal(r[34]).toDouble(), "pa1": new BigDecimal(r[35]).toDouble(), "la1": new BigDecimal(r[36]).toDouble(), "wa2": new BigDecimal(r[37]).toDouble(), "pa2": new BigDecimal(r[38]).toDouble(), "la2": new BigDecimal(r[39]).toDouble()]
            else {
                return null
            }
        }

        println finalResult

        def db = new Mongo("rm4", 15000).getDB("fb");

        def cutoff = db.getCollection("cutoff")
        cutoff.drop()

        cutoff.insert(new BasicDBObject("time", new Date()))

        def save = db.getCollection("resultnew")
        save.drop()

        def converted = finalResult.collect {
            if (it)
                return new BasicDBObject(it)
            else
                return null
        }

        converted = converted.findAll {
            it != null
        }

        save.insert(converted)


        matcher = [:]
        odd = [:]
        handicap = [:]

        def start = new Date() - 3
        def end = new Date()


        def index = 0
        (start..end).each {
            it = it.clearTime()
            println it
            save = db.getCollection("result")

            def cal = new GregorianCalendar()
            cal.setTime(it)
            def year = cal.get(Calendar.YEAR)
            def month = cal.get(Calendar.MONTH) + 1
            def day = cal.get(Calendar.DAY_OF_MONTH)

            DBObject q = new BasicDBObject();
            def objects = new BasicDBList()
            objects.add(new BasicDBObject("time", new BasicDBObject("\$gte", it)))
            objects.add(new BasicDBObject("time", new BasicDBObject("\$lte", it + 1)))
            q.put("\$and", objects);
            save.remove(q)

            def urlOdds = new URL("http://odds2.zso8.com/api/odds/oddshistory/$year/b_$year-$month-${day}.html").text
            def urlMatches = new URL("http://odds2.zso8.com/api/odds/oddshistory/$year/m_$year-$month-${day}.html").text
            def urlHandicaps = new URL("http://odds2.zso8.com/api/odds/oddshistory/$year/a_$year-$month-${day}.html").text

            matcher = [:]
            odd = [:]
            handicap = [:]

            String html = urlOdds.replaceAll("\r\n,", ",")
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

            html = urlMatches.replaceAll("\r\n,", ",")
            if (html.indexOf("var AllMatchCount") != -1) {
                html.eachLine { line ->
                    if (line =~ /\.split\(','\)/) {
                        def quote1 = line.indexOf("\"")
                        try {
                            line = line.substring(quote1 + 1, line.length() - 13)
                        } catch (e) {
                            e.printStackTrace()
                        }
                        def split = line.split(",")
                        matcher.put(split[0], split)
                    }
                }
            }

            html = urlHandicaps.replaceAll("\r\n,", ",")
            if (html.indexOf("var hAllOddsCount") != -1) {
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
                        handicap.put(split[0] + "," + split[1], split)
                    }
                }
            }

            int progress = 0;
            println "total:" + odd.size()
            finalResult = odd.collect { cmId, oddData ->
                progress++;
                if (progress % 50 == 0)
                    println progress
                def matchId = oddData[0]
                def cId = oddData[1]

                def abFlag = null
                def abFlagInitial = null

                if (!matcher[matchId])
                    return null

                if ([* matcher[matchId]][18])
                    abFlagInitial = [* matcher[matchId]][18] as int

                if ([* matcher[matchId]][19])
                    abFlag = [* matcher[matchId]][19] as int

                println abFlag
                def handicapInfo = [* handicap[matchId + "," + cId]]

                def initHandicap = null
                def currentHandicap = null
                def h1 = null
                def h2 = null

                if (handicapInfo) {
                    if (handicapInfo[6])
                        initHandicap = handicapInfo[6] as int

                    if (handicapInfo[5]) {
                        if (abFlag == 2)
                            currentHandicap = -(handicapInfo[5] as int)
                        else
                            currentHandicap = handicapInfo[5] as int
                    }

                    if (handicapInfo[3])
                        h1 = new BigDecimal(handicapInfo[3]).toDouble()

                    if (handicapInfo[4])
                        h2 = new BigDecimal(handicapInfo[4]).toDouble()
                }

                def r = [* oddData, * matcher[matchId], * handicap[matchId + "," + cId]]
                def rr = toReturnRate(r[3], r[4], r[5])

                if (r[19]) {
                    def parse = null
                    try {
                        parse = Date.parse("yyyy-MM-dd HH:mm:ss", r[19])
                    } catch (e) {
                        parse = Date.parse("yyyy-MM-dd", r[19])
                    }
                    if (r[19] && r[3] && r[4] && r[5] && r[6] && r[7] && r[8] && r[40] && r[41] && r[42] && r[43] && r[44] && r[45])
                        return [h1: h1, h2: h2, abFlag: abFlag, abFlagInitial: abFlagInitial, ih: initHandicap, ch: currentHandicap, "matchId": r[0], "cid": r[1], "w1": new BigDecimal(r[3]).toDouble(), 'p1': new BigDecimal(r[4]).toDouble(), 'l1': new BigDecimal(r[5]).toDouble(), 'w2': new BigDecimal(r[6]).toDouble(), 'p2': new BigDecimal(r[7]).toDouble(), 'l2': new BigDecimal(r[8]).toDouble(), "mtype": join(r[15], r[16], r[17]), 'time': parse, 'tidA': r[20], 'tNameA': join(r[21], r[22], r[23]), 'tidB': r[24], 'tNameB': join(r[25], r[26], r[27]), "tRankA": r[28], "tRankB": r[29], "resultRA": r[33] as int, "resultRB": r[34] as int, "resultPA": r[35] as int, "resultPB": r[36] as int, "wa1": new BigDecimal(r[40]).toDouble(), "pa1": new BigDecimal(r[41]).toDouble(), "la1": new BigDecimal(r[42]).toDouble(), "wa2": new BigDecimal(r[43]).toDouble(), "pa2": new BigDecimal(r[44]).toDouble(), "la2": new BigDecimal(r[45]).toDouble()]
                    return null
                } else
                    return null
            }


            converted = finalResult.collect {
                if (it)
                    return new BasicDBObject(it)
                else
                    return null
            }
            converted = converted.findAll {
                it != null
            }

            save.insert(converted)
        }
    }
}

