package org.basketball
/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-3-19
 * Time: 下午6:09
 * Let's RocknRoll
 */
class Cluster {
    public static void main(String[] args) {
        def split = "\t"
        def playerRow = new double[7547 - 256][]
        def playerNameYear = new String[7547 - 256]
        def playerName = new HashSet()

        int i = 0
        new File("/Users/jleo/Desktop/players").eachLine {
            def splitted = it.split(split)
            playerRow[i] = (splitted[8..-1] as double[])
            playerNameYear[i] = splitted[3] + " " + splitted[1]
            playerName << splitted[3]
            i++
        }

        def results = new KMeans(150).start(playerRow)
        results.eachWithIndex { result, index ->
            println "classify $index"
            result.points.eachWithIndex { EuclideanDoublePoint point, pi ->
                println playerNameYear[point.index]
            }
            println "---------------------------------------------"
        }


    }

    static int index(results, name, playerNameYear) {
        return results.sum { result ->
            return result.points.sum { EuclideanDoublePoint point ->
                return playerNameYear[point.index].contains(name) ? 1 : 0
            } > 0 ? 1 : 0
        }
    }
}
