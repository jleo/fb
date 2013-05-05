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

    static private def allStartupHome

    static private def allReserveHome

    static private def allStartupAway

    static private def allReserveAway

    static private def allReferee
    static private def allTeamMate
    static private def allOpponent

    static private Combination comb

    public static void main(String[] args) {
        int seasonFrom = 2000
        int seasonTo = 2007

        allStartupHome = new TreeSet()
        allReserveHome = new TreeSet()

        allStartupAway = new TreeSet()
        allReserveAway = new TreeSet()
        allTeamMate = new HashSet()
        allOpponent = new HashSet()

        allReferee = new TreeSet()

        def allPlayers = new TreeSet()

        comb = new Combination()
        (seasonFrom..seasonTo).each { season ->
            mongo.getDB("bb").getCollection("games").find([date: ["\$gt": season + "0930", "\$lt": season + 1 + "0425"]] as BasicDBObject).each {
                allStartupHome.addAll(it.get("homePlayerStart"))
                allReserveHome.addAll(it.get("homePlayerReserve"))
                allStartupAway.addAll(it.get("awayPlayerStart"))
                allReserveAway.addAll(it.get("awayPlayerReserve"))
                allReferee.addAll(it.get("refrees"))

                List home = (it.get("homePlayerStart") as List) + (it.get("homePlayerReserve") as List)
                List away = (it.get("awayPlayerStart") as List) + (it.get("awayPlayerReserve") as List)

                List startup = (it.get("homePlayerStart") as List) + (it.get("awayPlayerStart") as List)

                def homesub = comb.mn(home as String[], 2).getCombList()
                def awaysub = comb.mn(away as String[], 2).getCombList()
                allOpponent.addAll(comb.mn(startup as String[], 2).getCombList() - homesub - awaysub)

                allTeamMate.addAll(homesub)
                allTeamMate.addAll(awaysub)

//                allOpponent.addAll((home + away).subsequences() - homesub - awaysub)
            }
        }
//        allTeamMate.findAll{
//            it.contains("mingya01")
//        }.each {
//            println it
//        }
        allPlayers.addAll(allStartupHome)
        allPlayers.addAll(allReserveHome)
        allPlayers.addAll(allStartupAway)
        allPlayers.addAll(allReserveAway)

        allStartupHome = allStartupHome.toList()
        allReserveHome = allReserveHome.toList()
        allStartupAway = allStartupAway.toList()
        allReserveAway = allReserveAway.toList()
        allReferee = allReferee.toList()
        allTeamMate = allTeamMate.toList()
        allOpponent = allOpponent.toList()

        println(allTeamMate.size())
        println(allOpponent.size())



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
            int[] record = new int[allStartupHome.size() + allReserveHome.size() + allStartupAway.size() + allReserveAway.size() + allReferee.size() + allTeamMate.size() * 2 + allOpponent.size() + 2]

            int total = (it.get("fa") as int) + (it.get("fb") as int)
            record[0] = total
            record[1] = 1
//            record[1] = it.get("attendee") as int

            def homePlayerStart = it.get("homePlayerStart")
            def homePlayerReserve = it.get("homePlayerReserve")
            def awayPlayerStart = it.get("awayPlayerStart")
            def awayPlayerReserve = it.get("awayPlayerReserve")
            def referees = it.get("refrees")

            List home = (it.get("homePlayerStart") as List) + (it.get("homePlayerReserve") as List)
            List away = (it.get("awayPlayerStart") as List) + (it.get("awayPlayerReserve") as List)

            List startup = (it.get("homePlayerStart") as List) + (it.get("awayPlayerStart") as List)

            def homesub = comb.mn(home as String[], 2).getCombList()
            def awaysub = comb.mn(away as String[], 2).getCombList()
            def opponent = comb.mn(startup as String[], 2).getCombList() - homesub - awaysub

            homePlayerStart.each { p ->
                def ofp = allStartupHome.indexOf(p)
                record[2 + ofp] = 1

//                homePlayerStart.each { op ->
//                    record[1 + size * 4 + size * ofp + players.indexOf(op)] = 1
//                }
//                homePlayerReserve.each { op ->
//                    record[1 + size * 4 + size * ofp + players.indexOf(op)] = 1
//                }
//                awayPlayerStart.each { op ->
//                    record[1 + size * 4 + size * size + size * ofp + players.indexOf(op)] = 1
//                }
//                awayPlayerReserve.each { op ->
//                    record[1 + size * 4 + size * size + size * ofp + players.indexOf(op)] = 1
//                }
            }

            homePlayerReserve.each { p ->
                def ofp = allReserveHome.indexOf(p)
                record[2 + allStartupHome.size() + ofp] = 1

//                homePlayerStart.each { op ->
//                    record[1 + size * 4 + size * ofp + players.indexOf(op)] = 1
//                }
//                homePlayerReserve.each { op ->
//                    record[1 + size * 4 + size * ofp + players.indexOf(op)] = 1
//                }
//                awayPlayerStart.each { op ->
//                    record[1 + size * 4 + size * size + size * ofp + players.indexOf(op)] = 1
//                }
//                awayPlayerReserve.each { op ->
//                    record[1 + size * 4 + size * size + size * ofp + players.indexOf(op)] = 1
//                }
            }

            awayPlayerStart.each { p ->
                def ofp = allStartupAway.indexOf(p)
                record[2 + allStartupHome.size() + allReserveHome.size() + ofp] = 1

//                homePlayerStart.each { op ->
//                    record[1 + size * 4 + size * size + size * ofp + players.indexOf(op)] = 1
//                }
//                homePlayerReserve.each { op ->
//                    record[1 + size * 4 + size * size + size * ofp + players.indexOf(op)] = 1
//                }
//                awayPlayerStart.each { op ->
//                    record[1 + size * 4 + size * ofp + players.indexOf(op)] = 1
//                }
//                awayPlayerReserve.each { op ->
//                    record[1 + size * 4 + size * ofp + players.indexOf(op)] = 1
//                }
            }

            awayPlayerReserve.each { p ->
                def ofp = allReserveAway.indexOf(p)
                record[2 + allStartupHome.size() + allReserveHome.size() + allStartupAway.size() + ofp] = 1

//                homePlayerStart.each { op ->
//                    record[1 + size * 4 + size * size + size * ofp + players.indexOf(op)] = 1
//                }
//                homePlayerReserve.each { op ->
//                    record[1 + size * 4 + size * size + size * ofp + players.indexOf(op)] = 1
//                }
//                awayPlayerStart.each { op ->
//                    record[1 + size * 4 + size * ofp + players.indexOf(op)] = 1
//                }
//                awayPlayerReserve.each { op ->
//                    record[1 + size * 4 + size * ofp + players.indexOf(op)] = 1
//                }
            }

            referees.each { r ->
                def ofr = allReferee.indexOf(r)
                record[2 + allStartupHome.size() + allReserveHome.size() + allStartupAway.size() + allReserveAway.size() + ofr] = 1
            }

            homesub.each { combo ->
                def homeCombo = allTeamMate.indexOf(combo)
                record[2 + allStartupHome.size() + allReserveHome.size() + allStartupAway.size() + allReserveAway.size() + allReferee.size() + homeCombo] = 1
            }

            awaysub.each { combo ->
                def awayCombo = allTeamMate.indexOf(combo)
                record[2 + allStartupHome.size() + allReserveHome.size() + allStartupAway.size() + allReserveAway.size() + allReferee.size() + allTeamMate.size() + awayCombo] = 1
            }

            opponent.each { combo ->
                def awayCombo = allOpponent.indexOf(combo)
                record[2 + allStartupHome.size() + allReserveHome.size() + allStartupAway.size() + allReserveAway.size() + allReferee.size() + allTeamMate.size()*2 + awayCombo] = 1
            }
            
            file.append(record.toString()[1..-2].replaceAll(",","") + "\n")
        }
    }
}
