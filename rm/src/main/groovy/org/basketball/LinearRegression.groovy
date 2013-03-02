package org.basketball

import com.mongodb.BasicDBObject
import com.mongodb.DB
import com.mongodb.Mongo
import org.apache.commons.math.stat.regression.OLSMultipleLinearRegression

import java.util.concurrent.*

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
        Mongo mongo = new Mongo("rm4", 15000)
        DB db = mongo.getDB("bb")


        Thread.start {
            db.getCollection("regression").find().sort([hit5: -1] as BasicDBObject).skip(args[0] as int).limit(args[1] as int).each { c ->
                [* (24..31)].subsequences().each { s ->
                    def column = c.get('column')
                    column.addAll(s)
                    tasks << column
                }
            }
        }
        int cpu = Runtime.getRuntime().availableProcessors()
        println cpu + " cpu"
        ExecutorService executorService = Executors.newFixedThreadPool(cpu);
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

                        task = task as List
                        if (!db.getCollection("regression2").findOne([s: task.join("-")] as BasicDBObject))
                            run(task, allTraining, allReal, allTraining2, allReal2, db)
                    }
                }
            })
        }
        executorService.shutdown()

    }

    private static void run(columns, allTraining, allReal, allTraining2, allReal2, db) {
        try {
            println columns

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

            double[] beta = null;

            try {
                beta = olsMultipleLinearRegression.estimateRegressionParameters();
            } catch (e) {
                BasicDBObject basicDBObject = new BasicDBObject()
                basicDBObject.append("column", columns)
                basicDBObject.append("s", columns.join("-"))

                db.getCollection("regression2").insert(basicDBObject)
                return
            }



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

            BasicDBObject basicDBObject = new BasicDBObject()
            basicDBObject.append("column", columns)
            basicDBObject.append("s", columns.join("-"))
            basicDBObject.append("hit0", (hit0 / count * 100) as double)
            basicDBObject.append("hit5", (hit5 / count * 100) as double)
            basicDBObject.append("hit10", (hit10 / count * 100) as double)

            db.getCollection("regression2").insert(basicDBObject)
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
