package org.basketball

import Util.MongoDBUtil
import com.enigmastation.ml.bayes.Classifier
import com.mongodb.BasicDBList
import com.mongodb.BasicDBObject
import org.vertx.groovy.core.Vertx
import org.vertx.groovy.core.buffer.Buffer
import org.vertx.groovy.core.http.RouteMatcher
import org.vertx.java.core.json.JsonObject

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
        long t1 = System.currentTimeMillis()
        final BlockingQueue tasks = new ArrayBlockingQueue<>(30000);

        final Classifier classifier = new BasketballClassifier();
        MongoDBUtil mongoDBUtil = MongoDBUtil.getInstance(args[0], args[1], "bb")

        int index = 0
        Thread.start {
            mongoDBUtil.findAllCursor((["\$or": [["ae": ["\$exists": true] as BasicDBObject] as BasicDBObject, ["be": ["\$exists": true] as BasicDBObject] as BasicDBObject] as BasicDBList] as BasicDBObject).append("url", ['\$lte': '201210300CLE'] as BasicDBObject), null, "log").each {
                index++
                if (!it.get("total"))
                    return

                tasks.put(it)

                if (index % 10000 == 0) {
                    println index
                }
            }
        }


        int cpu = Runtime.getRuntime().availableProcessors()
        println "cpu number is " + cpu
        ExecutorService executorService = Executors.newFixedThreadPool(cpu + 1);
        cpu.times {
            executorService.submit(new Runnable() {

                @Override
                void run() {
                    while (true) {
                        def task = tasks.poll(30, TimeUnit.SECONDS)
                        int score = task.get("total") as int
                        classifier.train(task, Math.floor(score / 5))
                    }
                }
            })
        }
        executorService.shutdown()
        executorService.awaitTermination(2, TimeUnit.HOURS)

        println "time ellapsed:" + (System.currentTimeMillis() - t1) / 1000

        def tests = mongoDBUtil.findAllCursor((["\$or": [["ae": ["\$exists": true] as BasicDBObject] as BasicDBObject, ["be": ["\$exists": true] as BasicDBObject] as BasicDBObject] as BasicDBList] as BasicDBObject).append("url", ['\$gt': '201210300CLE'] as BasicDBObject), null, "log")
        def count = tests.count()
        def hit = 0
        tests.each {
            def result = classifier.classify(it) as int
            def answer = it.get("total") as int

            if (answer > result * 5 && answer < result * 5 + 5) {
                hit++
            }
        }
        println "hit rate: " + hit / count * 100 + "%"
        println "done"

        def server = Vertx.newVertx().createHttpServer()
        def routeMatcher = new RouteMatcher()
        routeMatcher.get("/cal2") { req ->
            req.response.end("jello")
        }

        routeMatcher.post("/cal") { req ->
            def body = new Buffer()
            req.dataHandler { buf ->
                body << buf
            }
            req.endHandler {
                JsonObject queryObject = new JsonObject(body.toString())
                def response = req.response
                response.chunked = true
                response.headers['Content-Type'] = "text/json; charset=utf-8"

                def result = classifier.classify(queryObject.getObject("info").toMap() as BasicDBObject)
                println(result as int) * 5 + "-" + (result as int) * 5 + 5
                response.write("{\"results\":")
                response.end(result + "}")
            }
        }
        println "deploying server"
        server.requestHandler(routeMatcher.asClosure()).listen(9090, args[2])
    }
}
