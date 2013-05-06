package org.basketball

import com.mongodb.Mongo
/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-5-5
 * Time: 下午9:56
 * To change this template use File | Settings | File Templates.
 */
Mongo mongo = new Mongo("rm4", 15000)
def t = mongo.getDB("bb").getCollection("theta")


def map = [:]
t.find().each {
    map.put(it.get("key"), it.get("value") as double)
}

def f = new File("/Users/jleo/Dropbox/nba/meta/theta.txt")
def h = new File("/Users/jleo/Dropbox/nba/meta/header.txt")
def nf = new File("/Users/jleo/Dropbox/nba/meta/newTheta.txt")

def lines = f.readLines()
def headers = lines[0].split(" ")
def theta = []
headers.each { header ->
    theta << map.get(header)
}

//nf.write(h.text + "\n")
nf.write(theta.toString()[1..-2].replaceAll(",", ""))
