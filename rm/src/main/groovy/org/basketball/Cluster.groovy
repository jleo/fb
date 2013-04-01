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
        int numberOfRow = 6919
        def playerRow = new double[numberOfRow][]
        def playerNameYear = new String[numberOfRow]
        def playerFullName = new String[numberOfRow]
        def playerName = new HashSet()
        def raw = new String[numberOfRow]
        int i = 0
        new File("/Users/jleo/toClusterNew.txt").eachLine {
            def splitted = it.split(split)
            raw[i] = it
            playerRow[i] = (splitted[2, 3, 6, 7, 8, 9, 10, 11, 14] as double[])
            playerNameYear[i] = splitted[0]
            playerFullName[i] = splitted[1]
            playerName << splitted[1]
            i++
        }
        def c = 13
        def results = new KMeans(c).start(playerRow)
        results.eachWithIndex { result, index ->
            println "classify $index"
            def file = new File("/Users/jleo/cluster/c${index}.txt")
            result.points.eachWithIndex { EuclideanDoublePoint point, pi ->
                file.append(playerNameYear[point.index] + "\t" + playerFullName[point.index] + "\n")
            }

            def file2 = new File("/Users/jleo/cluster/all.txt")
            result.points.eachWithIndex { EuclideanDoublePoint point, pi ->
                file2.append(raw[point.index])
                c.times { time ->
                    if (time == index)
                        file2.append("\t" + 1)
                    else
                        file2.append("\t" + 0)
                }
                file2.append("\n")
            }
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
