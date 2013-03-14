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
Mongo mongo = new Mongo("rm4", 15000)
def stat = mongo.getDB("bb").getCollection("stat")
boolean odd = true
def result = new File("/Users/jleo/result.txt")
if (result.exists())
    result.delete()

result.createNewFile()

new File("/Users/jleo/games.txt").eachLine { line ->
    odd = !odd
    def match
    if (odd) {
        def split = line.split("\t")
        def team = split[2]

        match = "http://www.basketball-reference.com/boxscores/" + Date.parse("yy-MM-dd", split[0]).format("yyyyMMdd") + "0" + split[2] + ".html"
        println match
        def teamA = []
        def teamB = []
        def find = stat.find([match: match] as BasicDBObject, [team: 1, name: 1] as BasicDBObject)
        if (find.count() == 0)
            println "not found........"
        find.each { s ->
            if (s.get("team").toString() != team) {
                teamA << s.get("name")
            } else {
                teamB << s.get("name")
            }
        }

        result.append(teamA.join("\t") + "\n")
        result.append(teamB.join("\t") + "\n")
    }

}
