package org.basketball

import com.mongodb.BasicDBList
import com.mongodb.BasicDBObject
import com.mongodb.Mongo
/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-5-2
 * Time: 下午10:43
 * To change this template use File | Settings | File Templates.
 */

Mongo m = new Mongo("rm4",15000)
def games = m.getDB("bb").getCollection("games")
def attendee = m.getDB("bb").getCollection("attendee")


games.find().each { BasicDBObject dbObject ->
    homePlayerStart = []
    homePlayerReserve = []
    awayPlayerStart = []
    awayPlayerReserve = []

    matchId = dbObject.get("match") - "http://www.basketball-reference.com/boxscores/" - ".html"
    home = matchId[9..11]
    attendee.find([date: matchId] as BasicDBObject).each { att ->
        playerId = att.get("playerId")
        team = att.get("team")
        startup = att.get("startup") == "true"

        if (home == team && startup) {
            homePlayerStart << playerId
        }

        if (home != team && startup) {
            awayPlayerStart << playerId
        }

        if (home != team && !startup) {
            awayPlayerReserve << playerId
        }

        if (home == team && !startup) {
            homePlayerReserve << playerId
        }


    }
    dbObject.put("homePlayerStart", homePlayerStart as BasicDBList)
    dbObject.put("awayPlayerStart", awayPlayerStart as BasicDBList)
    dbObject.put("awayPlayerReserve", awayPlayerReserve as BasicDBList)
    dbObject.put("homePlayerReserve", homePlayerReserve as BasicDBList)

    games.save(dbObject)
}