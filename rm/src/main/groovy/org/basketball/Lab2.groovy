package org.basketball

import org.apache.commons.math.stat.regression.OLSMultipleLinearRegression

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-26
 * Time: 下午4:48
 * Let's RocknRoll
 */

def a = [[1],[2],[3],[4]] as double[][]
def b = [10,24,32,49] as double[]
OLSMultipleLinearRegression olsMultipleLinearRegression = new OLSMultipleLinearRegression();
olsMultipleLinearRegression.newSampleData(b,a)

println olsMultipleLinearRegression.estimateRegressionParameters()
println olsMultipleLinearRegression.estimateResiduals()
println olsMultipleLinearRegression.estimateRegressionParametersStandardErrors()
println olsMultipleLinearRegression.estimateRegressandVariance()
//println olsMultipleLinearRegression.
