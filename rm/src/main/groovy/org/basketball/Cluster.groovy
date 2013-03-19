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
        def playerRow = new double[7547][]
        int i = 0
        new File("/Users/jleo/Desktop/players").eachLine {
            playerRow[i++] (it.split(split)[8..-1] as double[])
        }

        new KMeans(10).start(playerRow)
    }
}
