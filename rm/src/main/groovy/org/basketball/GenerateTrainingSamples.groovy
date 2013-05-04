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
    static private Mongo mongo = new Mongo("rm4", 15000)

    static private List players

    public static void main(String[] args) {
        int seasonFrom = 2000
        int seasonTo = 2007

        def allStartupHome = new TreeSet()
        def allReserveHome = new TreeSet()

        def allStartupAway = new TreeSet()
        def allReserveAway = new TreeSet()

        def allPlayers = new TreeSet()
        (seasonFrom..seasonTo).each { season ->
            mongo.getDB("bb").getCollection("games").find([date: ["\$gt": season + "0930", "\$lt": season + 1 + "0425"]] as BasicDBObject).each {
                if (it.get("homePlayerStart") == null)
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

        players = allPlayers.toList()

        def file = new File("/Users/jleo/Dropbox/nba/meta/training.txt")
        if (file.exists())
            file.delete()

        file.createNewFile()
        (seasonFrom..seasonTo - 1).each { season ->
            generate(season, file)
        }

        def fileTesting = new File("/Users/jleo/Dropbox/nba/meta/testing.txt")
        if (fileTesting.exists())
            fileTesting.delete()

        fileTesting.createNewFile()
        generate(seasonTo, fileTesting)
    }

    private static void generate(int season, file) {
        mongo.getDB("bb").getCollection("games").find([date: ["\$gt": season + "0930", "\$lt": season + 1 + "0425"]] as BasicDBObject).each {
            int size = players.size()
            int[] record = new int[4 * size + 1]

            int total = (it.get("fa") as int) + (it.get("fb") as int)
            record[0] = total
//            record[1] = it.get("attendee") as int

            def homePlayerStart = it.get("homePlayerStart")
            def homePlayerReserve = it.get("homePlayerReserve")
            def awayPlayerStart = it.get("awayPlayerStart")
            def awayPlayerReserve = it.get("awayPlayerReserve")

            homePlayerStart.each { p ->
                record[1 + players.indexOf(p)] = 1
            }

            homePlayerReserve.each { p ->
                record[1 + size + players.indexOf(p)] = 1
            }

            awayPlayerStart.each { p ->
                record[1 + size * 2 + players.indexOf(p)] = 1
            }

            awayPlayerReserve.each { p ->
                record[1 + size * 3 + players.indexOf(p)] = 1
            }

            file.append(record.toString()[1..-2] + "\n")
        }
    }
}
