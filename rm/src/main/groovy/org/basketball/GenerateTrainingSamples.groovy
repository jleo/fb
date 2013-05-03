package org.basketball

import com.mongodb.BasicDBObject
import com.mongodb.Mongo

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-5-2
 * Time: 下午11:33
 * To change this template use File | Settings | File Templates.
 */
class GenerateTrainingSamples {
    public static void main(String[] args) {
        int seasonFrom = 2009
        int seasonTo = 2011
        Mongo mongo = new Mongo("rm4", 15000)

        def allStartupHome = new TreeSet()
        def allReserveHome = new TreeSet()

        def allStartupAway = new TreeSet()
        def allReserveAway = new TreeSet()

        def allPlayers = new TreeSet()
        (seasonFrom..seasonTo).each {season->
            def games = mongo.getDB("bb").getCollection("games").find([date:["\$gt":season+"0930","\$lt":season+1+"0425"]] as BasicDBObject).each {
                if(it.get("homePlayerStart") == null)
                    println "a"
                allStartupHome.addAll(it.get("homePlayerStart"))
                allReserveHome.addAll(it.get("homePlayerReserve"))
                allStartupAway.addAll(it.get("awayPlayerStart"))
                allReserveAway.addAll(it.get("awayPlayerReserve"))
            }
        }

        allPlayers.addAll(allStartupHome)
        allPlayers.addAll(allReserveHome)
        allPlayers.addAll(allStartupAway)
        allPlayers.addAll(allReserveAway)

        println allPlayers.size()
    }
}
