package org.basketball

import com.mongodb.Mongo

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-13
 * Time: 上午11:38
 * To change this template use File | Settings | File Templates.
 */

Mongo m = new Mongo("localhost")
def games = m.getDB("bb").getCollection("games")

Mongo m2 = new Mongo("rm4",15000)
def remote = m2.getDB("bb").getCollection("games")


games.find().each {
    remote.insert(it)
}