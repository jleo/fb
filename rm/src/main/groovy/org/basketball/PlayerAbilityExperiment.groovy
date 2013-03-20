package org.basketball

import ch.usi.inf.sape.hac.experiment.Experiment

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-3-20
 * Time: 下午2:34
 * Let's RocknRoll
 */
class PlayerAbilityExperiment implements Experiment {
    private List playerRows
    private List playerNameYear
    private ArrayList<EuclideanDoublePoint> points

    public void load(year) {
        def split = "\t"
        playerRows = new ArrayList()
        playerNameYear = new ArrayList()
        def playerName = new HashSet()

        int i = 0
        new File("/Users/jleo/Desktop/players2").eachLine {
            def splitted = it.split(split)
            if ((splitted[1] as int) >year) {
                playerRows << (splitted[8..23, 25..77] as double[])
                playerNameYear << splitted[3] + " " + splitted[1]
                playerName << splitted[3]
                i++
            }
        }

        List<EuclideanDoublePoint> points = new ArrayList<EuclideanDoublePoint>();
        for (int j = 0; j < playerRows.size(); j++) {
            double[] doubles = playerRows[j];
            this.points = points
            this.points.add(new EuclideanDoublePoint(j, doubles));
        }
    }

    public def getId(int index) {
        playerNameYear[index]
    }

    public def getStat(int index) {
        playerRows[index]
    }

    public def getObservations(int index) {
        return points[index]
    }

    @Override
    int getNumberOfObservations() {
        return points.size()  //To change body of implemented methods use File | Settings | File Templates.
    }
}
