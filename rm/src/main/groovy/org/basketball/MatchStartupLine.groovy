package org.basketball

import com.mongodb.Mongo

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-5-1
 * Time: 下午6:25
 * To change this template use File | Settings | File Templates.
 */

Mongo m = new Mongo("localhost")

m.getDB("bb").getCollection("stat")

