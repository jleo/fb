package org.basketball

import com.mongodb.Mongo

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-5-3
 * Time: 上午10:26
 * Let's RocknRoll
 */

Mongo mongo = new Mongo("rm4", 15000)
def stat = mongo.getDB("bb").getCollection("attendee")

