package org

import com.mongodb.BasicDBObject
import com.mongodb.Mongo
import com.netflix.curator.framework.CuratorFrameworkFactory
import com.netflix.curator.framework.recipes.locks.InterProcessMutex
import com.netflix.curator.retry.RetryNTimes
import org.vertx.groovy.core.buffer.Buffer
import org.vertx.java.core.json.JsonArray
import org.vertx.java.core.json.JsonObject
/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 12-12-11
 * Time: 上午10:53
 * To change this template use File | Settings | File Templates.
 */

logger = container.getLogger();
def config = container.config


def bus = vertx.eventBus

logger.info "deploying kafka-persistor"
container.deployModule('vertx.kafka-connector-v1.2', config.kafka, 1) {
    logger.info "deploying kafka-persistor done"

    db = new Mongo("rm4", 15000).getDB("fb");

    def missing =db.getCollection("missing")
    final CONNECTSTRING = config.zk.url

    def client = CuratorFrameworkFactory.newClient(CONNECTSTRING, new RetryNTimes(Integer.MAX_VALUE, 1000))
    client.start()
    String config_root = "/fbcurrent"
    String config_lock = "/fblock"

    logger.info "I am in"
    if (!client.checkExists().forPath(config_root)) {
        client.create().forPath(config_root, "0".getBytes())
    }

    def batch = config.batch


    def httpClient = vertx.createHttpClient(host: "odds2.zso8.com")
    httpClient.setConnectTimeout(30000)

    def handler = { req ->
        InterProcessMutex mutex = new InterProcessMutex(client, config_lock)
        mutex.acquire()

        int current = new String(client.getData().forPath(config_root)) as int
        def next = current + batch
        client.setData().forPath(config_root, next.toString().getBytes())
        mutex.release()
        def counter = batch;
        logger.info "batch size:"+counter

        def responsed = []
        def done = false
        int index = 0;
        (current..next - 1).each { id ->
            try {
                logger.info "id:"+id
                def limit = missing.find(new BasicDBObject("taskid", id)).limit(1)
                if(!limit.hasNext()){
                    logger.info("no more")
                    done=true
                    counter--
                    return
                }


                def urlInfo = limit.next()
                def host = urlInfo.get("host")
                def path = urlInfo.get("path")
                def token = urlInfo.get("token")

                logger.info "requesting"

                httpClient.setHost(host)
                httpClient.getNow(path) { resp ->

                    logger.info "Got a response: ${resp.statusCode}"

                    responsed.add(index)

                    index++

                    def body = new Buffer()

                    resp.dataHandler { buffer ->
                        body << buffer
                    }
                    resp.endHandler {

                        def document = new JsonObject()
                        document.putString("token", token)
                        def html = new String(body.bytes, "utf-8")
                        document.putString("html", html)

                        if (html.trim()) {
                            def kafkaRequest = new JsonObject()
                            def messages = new JsonArray()
                            def message = new JsonObject()
                            message.putString("topic", "fb")
                            message.putString("body", document.toString())
                            messages.add(message)

                            kafkaRequest.putArray("messages", messages)
                            bus.send("vertx.kafkaconnector.outbound", kafkaRequest) {
                                logger.info "sent $id"
                            }
                        }
                    }

                    logger.info "responsed:" + responsed.size() + ", counter:" + counter
                    if (responsed.size() == counter) {
                        if(done){
                            logger.info "all done!"
                            return
                        }else{
                            bus.send("crawl", "go")
                        }
                    }
                }
            } catch (e) {
                counter--
            }
        }
    }

    bus.registerHandler("crawl", handler) {
        bus.send("crawl", "go")
    }
}





