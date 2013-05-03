package org.basketball

import com.mongodb.Mongo

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-5-2
 * Time: 下午10:43
 * To change this template use File | Settings | File Templates.
 */
Mongo m = new Mongo("rm4", 15000)

def games = m.getDB("bb").getCollection("games")
def attendee = m.getDB("bb").getCollection("attendee")

games.find()
