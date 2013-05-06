package org.basketball

import com.mongodb.BasicDBObject
import com.mongodb.Mongo

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-3-13
 * Time: 下午5:02
 * Let's RocknRoll
 */

Mongo mongo = new Mongo("rm4", 15000)
def t = mongo.getDB("bb").getCollection("theta")

def f = new File("/Users/jleo/Dropbox/nba/meta/theta.txt")

def lines = f.readLines()
def header = lines[0].split(" ")
def theta = lines[1].split(" ")

(1..header.size()).each {
    t.insert([key: header[it - 1], value: theta[it - 1] as double] as BasicDBObject)
}