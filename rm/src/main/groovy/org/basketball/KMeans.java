package org.basketball;


import org.apache.commons.math.stat.clustering.Cluster;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 12-9-7
 * Time: 上午10:40
 * To change this template use File | Settings | File Templates.
 */
public class KMeans {
    private int numberOfCategory;

    KMeans(int numberOfCategory) {
        this.numberOfCategory = numberOfCategory;
    }

    public Object start(double[][] tfIdfMatrix) {
        List<EuclideanDoublePoint> points = new ArrayList<EuclideanDoublePoint>();
        for (int i = 0; i < tfIdfMatrix.length; i++) {
            double[] doubles = tfIdfMatrix[i];
            points.add(new EuclideanDoublePoint(i, doubles));
        }

        MyKMeansPlusPlusClusterer kppc = new MyKMeansPlusPlusClusterer(new Random(System.currentTimeMillis()), MyKMeansPlusPlusClusterer.EmptyClusterStrategy.IGNORE);
        List<Cluster> result = kppc.cluster(points, numberOfCategory, 1000);

        System.out.println(result);
        return null;
    }
}
