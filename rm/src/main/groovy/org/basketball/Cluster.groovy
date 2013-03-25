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
        def raw = new String[7547 - 256]
        int i = 0
        new File("/Users/jleo/Desktop/players2").eachLine {
            def splitted = it.split(split)
            raw[i] = it
            playerRow[i] = (splitted[8..12] as double[])
            playerNameYear[i] = splitted[3] + " " + splitted[1]
            playerName << splitted[3]
            i++
        }

        def results = new KMeans(7).start(playerRow)
        results.eachWithIndex { result, index ->
            println "classify $index"
            def file = new File("/Users/jleo/cluster/c${index}.txt")
            result.points.eachWithIndex { EuclideanDoublePoint point, pi ->
                file.append(playerNameYear[point.index] + "\n")
            }

            def file2 = new File("/Users/jleo/splitted/all.txt")
            result.points.eachWithIndex { EuclideanDoublePoint point, pi ->
                file2.append(raw[point.index])
                7.times { time ->
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
