package org.basketball

import org.apache.commons.math.stat.regression.OLSMultipleLinearRegression

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-13
 * Time: 上午11:38
 * To change this template use File | Settings | File Templates.
 */

OLSMultipleLinearRegression olsMultipleLinearRegression = new OLSMultipleLinearRegression();
olsMultipleLinearRegression.newSampleData([10,11,12,13] as double[], [[4],[7],[7],[8]] as double[][])

println olsMultipleLinearRegression.estimateRegressionParameters()
println olsMultipleLinearRegression.calculateRSquared()