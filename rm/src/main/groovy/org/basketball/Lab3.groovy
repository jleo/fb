package org.basketball

import com.mongodb.BasicDBObject
import com.mongodb.Mongo
import groovy.json.JsonSlurper

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-3-13
 * Time: 下午5:02
 * Let's RocknRoll
 */

Mongo mongo = new Mongo("rm3", 27017)
def stat = mongo.getDB("eccrawler300").getCollection("comment")

def file = new File("/Users/jleo/aaa")
def j = new JsonSlurper().parseText(file.text)
j.statuses.each{

    if(!it.user)
        return

    try {
        stat.save([cid:it.id.toString(),Content:it.text,Score:5,Title:"",UserId:it.user.name,brand:'帮宝适',crawlTime:new Date(),creationTime:new Date()-new Random().nextInt(300),handlingStatus:'UNHANDLED',mid:'513ecc02196f60696f5272cb',pid:'可口可乐',reviewer:it.user.name,rawid:it.user.id,shop:'Sina_Weibo'] as BasicDBObject)
    } catch (e) {
        println it.id
        e.printStackTrace()
    }
}

