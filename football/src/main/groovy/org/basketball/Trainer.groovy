package org.basketball

import Util.MongoDBUtil
import com.enigmastation.ml.bayes.Classifier
import com.mongodb.BasicDBList
import com.mongodb.BasicDBObject

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-13
 * Time: 上午10:08
 * To change this template use File | Settings | File Templates.
 */
class Trainer {

    public static void main(String[] args) {

        final BlockingQueue tasks = new ArrayBlockingQueue<>(3000);

        final Classifier classifier = new BasketballClassifier();
        MongoDBUtil mongoDBUtil = MongoDBUtil.getInstance(args[0], args[1], "bb")

        int index = 0
        Thread.start {
            mongoDBUtil.findAllCursor(["\$or": [["ae": ["\$exists": true] as BasicDBObject] as BasicDBObject, ["be": ["\$exists": true] as BasicDBObject] as BasicDBObject] as BasicDBList] as BasicDBObject, null, "log").each {
                index++
                if (!it.get("total"))
                    return

                tasks.put(it)

                if (index % 100 == 0) {
                    println index
                }
            }
        }


        int cpu = Runtime.getRuntime().availableProcessors()
        ExecutorService executorService = Executors.newFixedThreadPool(cpu + 1);
        cpu.times {
            executorService.submit(new Runnable() {

                @Override
                void run() {
                    while (true) {
                        def task = tasks.poll(30, TimeUnit.SECONDS)
                        int score = task.get("total") as int
                        classifier.train(task, score)
                    }
                }
            })
        }
        executorService.shutdown()

        println "done"
    }
}
