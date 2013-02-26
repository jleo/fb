package org.basketball

import org.apache.commons.math.stat.regression.OLSMultipleLinearRegression

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-26
 * Time: 下午4:49
 * Let's RocknRoll
 */
class LinearRegression {
    public static void main(String[] args) {
        OLSMultipleLinearRegression olsMultipleLinearRegression = new OLSMultipleLinearRegression();

        FileInputStream stream = new FileInputStream("train");
        ObjectInputStream out = new ObjectInputStream(stream);
        double[][] allTraining = out.readObject() as double[][]
        out.close()

        stream = new FileInputStream("real");
        out = new ObjectInputStream(stream);
        def allReal = out.readObject()
        out.close()

        def size = allReal.length
        double[] real = new double[size]

        allReal.eachWithIndex { it, idx ->
            real[idx] = it[0]
        }
        olsMultipleLinearRegression.newSampleData(real, allTraining)

        double[] betaHat = olsMultipleLinearRegression.estimateRegressionParameters();
        System.out.println("Estimates the regression parameters b:");
        println betaHat

        double[] residuals = olsMultipleLinearRegression.estimateResiduals();
        println residuals

//        println olsMultipleLinearRegression.estimateRegressionParametersStandardErrors()

        double[] beta = olsMultipleLinearRegression.estimateRegressionParameters();
        println beta

        FileInputStream stream2 = new FileInputStream("test");
        ObjectInputStream out2 = new ObjectInputStream(stream2);
        def allTraining2 = out2.readObject()
        out2.close()

        stream = new FileInputStream("testreal");
        out = new ObjectInputStream(stream);
        def allReal2 = out.readObject()
        out.close()

        def all = 0
        allTraining2.eachWithIndex { it, idx ->
            def prediction = beta[0];
            for (int i = 1; i < beta.length; i++) {
                prediction += beta[i] * it[i];
            }
            println "predict:" + prediction + ", actual:" + allReal2[idx];
            all += Math.abs(prediction - allReal2[idx][0])
        }
        println all
    }
}
