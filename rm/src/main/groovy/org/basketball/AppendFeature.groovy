package org.basketball

import com.mongodb.BasicDBObject
import com.mongodb.DB
import com.mongodb.Mongo

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-3-2
 * Time: 下午5:20
 * To change this template use File | Settings | File Templates.
 */
class AppendFeature {
    public static void main(String[] args) {
        Mongo mongo = new Mongo("rm4", 15000)
        DB db = mongo.getDB("bb")

        db.getCollection("end3").find().each {
            def mk2s = it.get("ae").get("mk2s") ?: 0
            def mk3s = it.get("ae").get("mk3s") ?: 0
            def mkcs = it.get("ae").get("mkcs") ?: 0
            def mkls = it.get("ae").get("mkls") ?: 0
            def mft = it.get("ae").get("mft") ?: 0
            def to = it.get("ae").get("to") ?: 0
//
            def att = mkcs + mkls
//
            def efg = (mk2s + 0.5 * mk3s) * 100 / att
//
            def mk2sb = it.get("be").get("mk2s") ?: 0
            def mk3sb = it.get("be").get("mk3s") ?: 0
            def mkcsb = it.get("be").get("mkcs") ?: 0
            def mklsb = it.get("be").get("mkls") ?: 0
            def mftb = it.get("be").get("mft") ?: 0
            def tob = it.get("be").get("to") ?: 0

            def attb = mkcsb + mklsb
//
            def efgb = (mk2sb + 0.5 * mk3sb) * 100 / attb
//
//            db.getCollection("end3").update([_id: it.get("_id")] as BasicDBObject, ["\$set": ["ae.efg": efg as double, "be.efg": efgb as double]] as BasicDBObject, true, false)
            def a = it.get("score").split("-")[0] as int
            def b = it.get("score").split("-")[1] as int

            def tsa = 50 * a / (att + 0.44 * mft)
            def tsb = 50 * b / (attb + 0.44 * mftb)

//            db.getCollection("end3").update([_id: it.get("_id")] as BasicDBObject, ["\$set": ["ae.ts": tsa as double, "be.ts": tsb as double]] as BasicDBObject, true, false)

            def possa = att + 0.44 * mft + to
            def possb = attb + 0.44 * mftb + tob

//            def list = new BasicDBList()
//            def docA = new BasicDBObject("ae", [poss: possa])
//            def docB = new BasicDBObject("be", [poss: possb])
//            list.add(docA)
//            list.add(docB)
//            def push = new BasicDBObject("\$push", list)

//            db.getCollection("end3").update(["_id": it.get("_id")] as BasicDBObject, ["\$set": ["ae.poss": possa as double, "be.poss": possb as double]] as BasicDBObject, true, false)

            def pppa = possa / a
            def pppb = possb / b

//            db.getCollection("end32").update(["_id": it.get("_id")] as BasicDBObject, ["\$set": ["ae.ppp": pppa as double, "be.ppp": pppb as double]] as BasicDBObject, true, false)

            def ORTGA = a / possa * 100
            def ORTGB = b / possb * 100

            def DRTGA = b / possa * 100
            def DRTGB = a / possa * 100

            db.getCollection("end3").update(["_id": it.get("_id")] as BasicDBObject, ["\$set": ["ae.efg": efg as double, "be.efg": efgb as double, "ae.ts": tsa as double, "be.ts": tsb as double, "ae.ppp": pppa as double, "be.ppp": pppb as double, "ae.ortg": ORTGA as double, "ae.drtg": DRTGA as double, "be.drtg": DRTGB as double, "be.ortg": ORTGB as double]] as BasicDBObject, true, false)
//            db.getCollection("end300").insert([ae: [poss: possa as double] as BasicDBObject, be: [poss: possb as double] as BasicDBObject] as BasicDBObject)
        }
    }
}
