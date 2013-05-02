package org.basketball

import com.mongodb.BasicDBObject
import com.mongodb.Mongo

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-3-13
 * Time: 上午10:09
 * Let's RocknRoll
 */
Mongo mongo = new Mongo("localhost", 27017)
def stat = mongo.getDB("bb").getCollection("stat")
boolean odd = true


def file = [:]
(1990..2013).each { year ->
    def result = new File("/Users/jleo/statByYear", "${year}.csv")
    if (result.exists())
        result.delete()
    result.createNewFile()

    file.put(year, result)
}

def find = stat.find([] as BasicDBObject, [] as BasicDBObject)
if (find.count() == 0)
    println "not found........"

find.each { s ->
    def yearTeam = s.get("match").replaceAll("http://www.basketball-reference.com/boxscores/", "").replaceAll(".html", "")

    def c = new GregorianCalendar()
    c.setTime(Date.parse("yyyyMMdd", yearTeam[0..7]))
    def year = c.get(Calendar.YEAR)
    def month = c.get(Calendar.MONTH)

    if (month > 8)
        year++

    file.get(year).append(year + "\t" + (month + 1) + "\t" + yearTeam + "\t" + s.get("team") + "\t" + s.get("name") + "\t" + s.get("MP") + "\t" + s.get("FG") + "\t" + s.get("FGA") + "\t" + s.get("FG%") + "\t" + s.get("3P") + "\t" + s.get("3PA") + "\t" + s.get("3P%") + "\t" + s.get("FT") + "\t" + s.get("FTA") + "\t" + s.get("FT%") + "\t" + s.get("ORB") + "\t" + s.get("DRB") + "\t" + s.get("TRB") + "\t" + s.get("AST") + "\t" + s.get("STL") + "\t" + s.get("BLK") + "\t" + s.get("TOV") + "\t" + s.get("PF") + "\t" + s.get("PTS") + "\t" + s.get("+/-") + "\t" + s.get("team") + "\t" + s.get("TS%") + "\t" + s.get("eFG%") + "\t" + s.get("ORB%") + "\t" + s.get("DRB%") + "\t" + s.get("TRB%") + "\t" + s.get("AST%") + "\t" + s.get("STL%") + "\t" + s.get("BLK%") + "\t" + s.get("TOV%") + "\t" + s.get("USG%") + "\t" + s.get("ORtg") + "\t" + s.get("DRtg") + "\t" + s.get("rawASPM") + "\n")
}