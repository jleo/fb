package org.basketball

import org.apache.commons.math.stat.regression.OLSMultipleLinearRegression

import java.util.concurrent.*

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-26
 * Time: 下午4:49
 * Let's RocknRoll
 */
class SingleLinearRegression {
    final static BlockingQueue tasks = new ArrayBlockingQueue<>(30);

    public static void main(String[] args) {
        Thread.start {
            [[1, 2, 8, 9, 13, 16, 17, 20, 21, 22, 26, 27, 30, 31]].each {
                tasks << it
            }
        }
        int cpu = Runtime.getRuntime().availableProcessors()
        println cpu + " cpu"
        ExecutorService executorService = Executors.newFixedThreadPool(9);
        cpu.times {
            executorService.submit(new Runnable() {

                @Override
                void run() {
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
                        run(task, allTraining, allReal, allTraining2, allReal2, null)
                    }
                }
            })
        }
        executorService.shutdown()

    }

    private static void run(columns, allTraining, allReal, allTraining2, allReal2, db) {
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

            System.out.println("Estimates the regression parameters b:");

            double[] beta = null;

            beta = olsMultipleLinearRegression.estimateRegressionParameters();



            def all = 0
            def all2 = 0

            def special = 0

            def all3
            (allReal2, all3) = filter(allReal2, allTraining2, 158, 3.5, columns)

            all3.eachWithIndex { it, idx ->
                def prediction = 0;
                for (int i = 0; i < beta.length; i++) {
                    prediction += beta[i] * it[i];
                }
                all += Math.abs(prediction - allReal2[idx])
                all2 += prediction - allReal2[idx]

                def expected = allReal2[idx]

                prediction = Math.round(prediction)
//                prediction = 47
                println "predict:" + prediction + ", actual:" + allReal2[idx] + ", " + allTraining2[idx][37] + ", " + allTraining2[idx][36];


                if (prediction >= (55) || prediction <= (45)) {
                    special++
                    if (Math.abs(expected - prediction) == 0)
                        hit0_55++

                    if (Math.abs(expected - prediction) <= 2)
                        hit5_55++

                    if (Math.abs(expected - prediction) <= 10)
                        hit10_55++

                    if (Math.abs(expected - prediction) <= 15)
                        hit15_55++

                }
                if (Math.abs(expected - prediction) == 0)
                    hit0++

                if (Math.abs(expected - prediction) <= 2)
                    hit5++

                if (Math.abs(expected - prediction) <= 10)
                    hit10++

                if (Math.abs(expected - prediction) <= 15)
                    hit15++
            }
            def count = allReal2.length

            println("hit0:" + ((hit0 / count * 100) as double))
            println("hit3:" + ((hit5 / count * 100) as double))
            println("hit10:" + ((hit10 / count * 100) as double))

//            println("hit0_s:" + ((hit0_55 / count * 100) as double))
//            println("hit5_s:" + ((hit5_55 / count * 100) as double))
//            println("hit10_s:" + ((hit10_55 / count * 100) as double))

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
