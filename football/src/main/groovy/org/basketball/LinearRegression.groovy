package org.basketball

import org.apache.commons.math.stat.regression.OLSMultipleLinearRegression

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-26
 * Time: 下午4:49
 * Let's RocknRoll
 */
class LinearRegression {
    final static BlockingQueue tasks = new ArrayBlockingQueue<>(30);

    public static void main(String[] args) {

        Thread.start {
            [* (0..(args[0] as int))].subsequences().each {
                tasks << it
            }
        }
        int cpu = Runtime.getRuntime().availableProcessors()
        println cpu +" cpu"
        ExecutorService executorService = Executors.newFixedThreadPool(9);
        cpu.times {
            executorService.submit(new Runnable() {

                @Override
                void run() {
                    def file = new File(Thread.currentThread().name + ".log")
                    file.createNewFile()



                    FileInputStream stream = new FileInputStream("train");
                    ObjectInputStream out = new ObjectInputStream(stream);
                    double[][] allTraining = out.readObject() as double[][]
                    out.close()

                    stream = new FileInputStream("real");
                    out = new ObjectInputStream(stream);
                    def allReal = out.readObject()
                    out.close()

                    FileInputStream stream2 = new FileInputStream("test");
                    ObjectInputStream out2 = new ObjectInputStream(stream2);
                    def allTraining2 = out2.readObject()
                    out2.close()

                    stream = new FileInputStream("testreal");
                    out = new ObjectInputStream(stream);
                    def allReal2 = out.readObject()
                    out.close()

                    while (true) {
                        def task = tasks.poll(30, TimeUnit.SECONDS)
                        if (task == null)
                            break
                        run(task, file, allTraining, allReal, allTraining2, allReal2)
                    }
                }
            })
        }
        executorService.shutdown()

    }

    private static void run(columns, file, allTraining, allReal, allTraining2, allReal2) {
        try {
            int hit0 = 0;
            int hit5 = 0;
            int hit10 = 0;
            int hit15 = 0;

            int hit0_55 = 0;
            int hit5_55 = 0;
            int hit10_55 = 0;
            int hit15_55 = 0;

            def size = allReal.length

            double[] real
            def filtered

            OLSMultipleLinearRegression olsMultipleLinearRegression = new OLSMultipleLinearRegression();

            (allReal, allTraining) = filter(allReal, allTraining, 158, 3.5, columns)

            olsMultipleLinearRegression.newSampleData(allReal, allTraining)

            double[] betaHat = olsMultipleLinearRegression.estimateRegressionParameters();
            System.out.println("Estimates the regression parameters b:");

            double[] beta = olsMultipleLinearRegression.estimateRegressionParameters();



            def all = 0
            def all2 = 0

            def special = 0


            (allReal2, allTraining2) = filter(allReal2, allTraining2, 158, 3.5, columns)

            allTraining2.eachWithIndex { it, idx ->
                def prediction = 0;
                for (int i = 0; i < beta.length; i++) {
                    prediction += beta[i] * it[i];
                }
                //            println "predict:" + prediction + ", actual:" + allReal2[idx];
                all += Math.abs(prediction - allReal2[idx])
                all2 += prediction - allReal2[idx]

                def expected = allReal2[idx]

                prediction = Math.round(prediction)


                if (expected >= (60) || expected <= (35)) {
                    special++
                    if (Math.abs(expected - prediction) == 0)
                        hit0_55++

                    if (Math.abs(expected - prediction) <= 5)
                        hit5_55++

                    if (Math.abs(expected - prediction) <= 10)
                        hit10_55++

                    if (Math.abs(expected - prediction) <= 15)
                        hit15_55++

                }
                if (Math.abs(expected - prediction) == 0)
                    hit0++

                if (Math.abs(expected - prediction) <= 5)
                    hit5++

                if (Math.abs(expected - prediction) <= 10)
                    hit10++

                if (Math.abs(expected - prediction) <= 15)
                    hit15++
            }
            def count = allReal2.length
            String result = "overview:" + "\n" + hit0 / count * 100 + "%" + "\n" + hit5 / count * 100 + "%" + "\n" + hit10 / count * 100 + "%"
            file.append(result + "\n")
        } catch (e) {
            e.printStackTrace()
        }
//        println hit15 / count * 100 + "%"

//        println "special:"
//        println hit0_55 / special * 100 + "%"
//        println hit5_55 / special * 100 + "%"
//        println hit10_55 / special * 100 + "%"
//        println hit15_55 / special * 100 + "%"

//        println special / count * 100
    }

    public static List filter(allReal, double[][] allTraining, lte, gte, columns) {
        def id = []
        allReal.eachWithIndex { it, idx ->
            if (it[0] <= lte && it[0] >= gte)
                id << idx
        }

        def size = id.size()
        double[] real = new double[size]

        def filtered = new double[size][]

        new double[id.size()][]
        def index = 0;
        allReal.eachWithIndex { it, idx ->
            if (it[0] <= lte && it[0] >= gte) {
                real[index] = it[0]

                filtered[index] = new double[columns.size()]

                int i = 0
                columns.each { c ->
                    filtered[index][i++] = allTraining[idx][c]

                }
                index++
            }
        }
        [real, filtered]
    }
}
