package org.basketball

import com.mongodb.BasicDBObject
import com.mongodb.Mongo

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-3-26
 * Time: 下午2:10
 * Let's RocknRoll
 */

Mongo mongo = new Mongo("rm4", 15000)
def database = mongo.getDB("bb")
def collection = database.getCollection("player")

def allPlayer = new File("/Users/jleo/Downloads/allPlayers.txt")
def file = new File("/Users/jleo/Downloads/clusterlist.txt")
file.eachLine {
    def year = it[0..3] as int
    def playerId = it[4..-1]

    def yearBefore = year - 1

    def shorten = (year - 1900) < 100 ? (year - 1900) : (year - 2000) >= 10 ? (year - 2000) : "0" + (year - 2000)

    def result = yearBefore + "-" + shorten

    def player = collection.findOne([Season: result, name: playerId + ".html"] as BasicDBObject)
    if (player) {
        allPlayer.append(it + "\t" + player.get("fname") + "\t" + player.get("TS%") + "\t" + player.get("eFG%") + "\t" + player.get("ORB%") + "\t" + player.get("DRB%") + "\t" + player.get("TRB%") + "\t" + player.get("AST%") + "\t" + player.get("STL%") + "\t" + player.get("BLK%") + "\t" + player.get("TOV%") + "\t" + player.get("USG%")+"\t"+player.get("FG")+"\t"+player.get("FGA")+"\t"+player.get("FG%")+"\t"+player.get("3P")+"\t"+player.get("3PA")+"\t"+player.get("3P%")+"\n")
    }
}
