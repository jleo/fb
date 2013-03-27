package org.basketball;

import org.apache.commons.math.stat.clustering.Clusterable;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 12-9-11
 * Time: 下午2:56
 * To change this template use File | Settings | File Templates.
 */
public class EuclideanDoublePoint implements Clusterable<EuclideanDoublePoint>, Serializable {

    /**
     * Serializable version identifier.
     */
    private static final long serialVersionUID = 3946024775784901369L;

    /**
     * Point coordinates.
     */
    private final double[] point;
    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public EuclideanDoublePoint(int i, double[] doubles) {
        this.index = i;
        this.point = doubles;
    }

    /**
     * Get the n-dimensional point in integer space.
     *
     * @return a reference (not a copy!) to the wrapped array
     */
    public double[] getPoint() {
        return point;
    }

    /**
     * {@inheritDoc}
     */
    public double distanceFrom(final EuclideanDoublePoint p) {
        double sumA = 0.0f;
        for (int i = 0; i < point.length; i++) {
            sumA += point[i] * point[i];
        }

        double sumB = 0.0f;
        for (int i = 0; i < p.point.length; i++) {
            sumB += p.point[i] * p.point[i];
        }

        double multiply = 0.0f;
        for (int i = 0; i < point.length; i++) {
            multiply += p.point[i] * point[i];
        }
        return 1 - multiply / (Math.sqrt(sumA) * Math.sqrt(sumB));
//        return MathUtils.distanceInf(point, p.getPoint());
    }

    /**
     * {@inheritDoc}
     */
    public EuclideanDoublePoint centroidOf(final Collection<EuclideanDoublePoint> points) {
        double[] centroid = new double[getPoint().length];
        for (EuclideanDoublePoint p : points) {
            for (int i = 0; i < centroid.length; i++) {
                centroid[i] += p.getPoint()[i];
            }
        }
        for (int i = 0; i < centroid.length; i++) {
            centroid[i] /= points.size();
        }
        return new EuclideanDoublePoint(index, centroid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof EuclideanDoublePoint)) {
            return false;
        }
        final double[] otherPoint = ((EuclideanDoublePoint) other).getPoint();
        if (point.length != otherPoint.length) {
            return false;
        }
        for (int i = 0; i < point.length; i++) {
            if (point[i] != otherPoint[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hashCode = 0;
        for (Double i : point) {
            hashCode += i.hashCode() * 13 + 7;
        }
        return hashCode;
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.1
     */
    @Override
    public String toString() {
        final StringBuilder buff = new StringBuilder("(");
        final double[] coordinates = getPoint();
        for (int i = 0; i < coordinates.length; i++) {
            buff.append(coordinates[i]);
            if (i < coordinates.length - 1) {
                buff.append(",");
            }
        }
        buff.append(")");
        return buff.toString();
    }
}
