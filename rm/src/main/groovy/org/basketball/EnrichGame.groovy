package org.basketball

import com.mongodb.Mongo

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-5-3
 * Time: 上午10:43
 * Let's RocknRoll
 */

Mongo mongo = new Mongo("rm4", 15000)
def collection = mongo.getDB("bb").getCollection("games")
def games = collection.find().each {
    def dateAndHome = it.get("match") - "http://www.basketball-reference.com/boxscores/"-".html"
    def date = dateAndHome[0..7]
    def home = dateAndHome[9..11]

    it.put("date",date)
    it.put("home",home)
    it.put("matchId",dateAndHome)

    collection.save(it)
}