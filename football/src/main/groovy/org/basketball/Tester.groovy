package org.basketball

import Util.MongoDBUtil
import com.mongodb.BasicDBObject
import org.vertx.groovy.core.Vertx
import org.vertx.groovy.core.buffer.Buffer
import org.vertx.java.core.json.JsonObject

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-13
 * Time: 上午11:38
 * To change this template use File | Settings | File Templates.
 */

def vertx = Vertx.newVertx()
def client = vertx.createHttpClient(host: "localhost", port: 9090)
def request = client.post("/cal") { resp ->
    println "Got a response: ${resp.body.toString()}"
}

def myBuffer = new Buffer()

def it = MongoDBUtil.getInstance("localhost", "27017", "bb").getCollection("log").find(new BasicDBObject("url", "200101030LAL")).skip(100).next()


def toSent = new JsonObject()
toSent.putObject("info", new JsonObject(it.toMap()))
myBuffer.appendString(toSent.toString())
println toSent.toString()

request.putHeader("Content-Type", "application/json")
request.chunked = true
request.write(myBuffer)
request.end()

sleep( 10000)