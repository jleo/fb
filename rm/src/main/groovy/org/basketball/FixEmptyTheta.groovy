package org.basketball

import com.mongodb.BasicDBObject
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
def a = mongo.getDB("bb").getCollection("attendee")

def sum = 0
def count = 0
t.find([key: java.util.regex.Pattern.compile("HomeStart")] as BasicDBObject).each {
    sum += it.get("value") as double
    count += 1
}

def HomeStartDefault = sum / count

sum = 0
count = 0
t.find([key: java.util.regex.Pattern.compile("HomeReserve")] as BasicDBObject).each {
    sum += it.get("value") as double
    count += 1
}

def HomeReserveDefault = sum / count

sum = 0
count = 0
t.find([key: java.util.regex.Pattern.compile("AwayStart")] as BasicDBObject).each {
    sum += it.get("value") as double
    count += 1
}

def AwayStartDefault = sum / count

sum = 0
count = 0
t.find([key: java.util.regex.Pattern.compile("AwayReserve")] as BasicDBObject).each {
    sum += it.get("value") as double
    count += 1
}

def AwayReserveDefault = sum / count

sum = 0
count = 0
t.find([key: java.util.regex.Pattern.compile("-Home\$")] as BasicDBObject).each {
    sum += it.get("value") as double
    count += 1
}

def HomeComboDefault = sum / count

sum = 0
count = 0
t.find([key: java.util.regex.Pattern.compile("-Away\$")] as BasicDBObject).each {
    sum += it.get("value") as double
    count += 1
}

def AwayComboDefault = sum / count


t.find([value: 0] as BasicDBObject).each {
    String key = it.get("key")

    ["HomeStart", "HomeReserve", "AwayStart", "AwayReserve"].each { kw ->
        if (key.contains(kw)) {
            key = key - "-" - kw
            if (kw == "HomeStart")
                it.put("value", HomeStartDefault*0.7)
            if (kw == "HomeReserve")
                it.put("value", HomeReserveDefault*0.7)
            if (kw == "AwayStart")
                it.put("value", AwayStartDefault*0.7)
            if (kw == "AwayReserve")
                it.put("value", AwayReserveDefault*0.7)
        }
    }

    if (key.endsWith("-Home")) {
        it.put("value", HomeComboDefault*0.7)
    }

    if (key.endsWith("-Away")) {
        it.put("value", AwayComboDefault*0.7)
    }

    t.save(it)
}