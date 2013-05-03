package org.basketball

import com.mongodb.BasicDBObject
import com.mongodb.Mongo

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-5-2
 * Time: 下午2:45
 * Let's RocknRoll
 */
class DumpEventsByMatchup {
    public static void main(String[] args) {
        Mongo m = new Mongo("58.215.141.166", 15000)
        def log = m.getDB("bb").getCollection("log")
        def attendee = m.getDB("bb").getCollection("attendee")

        def matchup = new Matchup()

        def gameList = new File("/Users/jleo/Dropbox/nba/meta/gameList-1996.txt")
        gameList.eachLine { game ->
            def teamPlayer = [:].withDefault {
                return []
            }
            def c = attendee.find([match: game, startup: "true"] as BasicDBObject)
            c.each { BasicDBObject att ->
                def playerId = att.getString("playerId")
                def team = att.getString("team")
                def name = att.getString("name")
//                def startup = att.getString("startup") as boolean
                def player = new Player(playerId: playerId, shortName: name.split(" ")[0][0] + ". " + name.split(" ")[1])
                teamPlayer.get(team) << player
            }
            c.close()
//            log.find([url: game] as BasicDBObject).sort([sec: -1] as BasicDBObject).each {
//
//            }

            matchup << teamPlayer
        }
    }
}
