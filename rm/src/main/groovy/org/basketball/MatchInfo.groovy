package org.basketball

import com.mongodb.BasicDBObject
import com.mongodb.Mongo

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-5-1
 * Time: 下午6:25
 * To change this template use File | Settings | File Templates.
 */

Mongo m = new Mongo("localhost")

def c = m.getDB("bb").getCollection("games")

HashSet<String> startup = new HashSet()
HashSet<String> reserve = new HashSet()

def gameStats = new File("/Users/jleo/Dropbox/nba/meta/gameInfo.txt")

def output = new File("/Users/jleo/Dropbox/nba/meta/list1980-2013.txt")
output.eachLine {
    String buffer = ""
    def matchId = "http://www.basketball-reference.com" + it
    c.find(["match": matchId] as BasicDBObject)?.each { cr ->
        buffer += cr.get("match") - "http://www.basketball-reference.com/boxscores/" - ".html" + ","
        buffer += cr.get("court") + ","
        buffer += cr.get("attendee") + ","
        buffer += (cr.get("timeAndDate") as String) + ","
        buffer += cr.get("time")
        gameStats.append(buffer + "\n")
        buffer = ""
    }
}
