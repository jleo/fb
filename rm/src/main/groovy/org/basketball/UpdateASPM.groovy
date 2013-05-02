package org.basketball

import com.mongodb.BasicDBObject
import com.mongodb.Mongo

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-9
 * Time: 下午9:32
 * To change this template use File | Settings | File Templates.
 */
class UpdateASPM {
    public static void main(String[] args) {
        Mongo m = new Mongo("rm4", 15000)
        def collection = m.getDB("bb").getCollection("stat")
        collection.find().each { c ->
            def s1 = 0.169837848009137 * c.get("TRB%") - 1.71032529258683
            def s2 = 1.26328757443387 * c.get("STL%") + 0.279823196230518 * c.get("BLK%") - 2.60288237636979
            def s3 = ((c.get("TS%") * 2 * (1 - c.get("TOV%") / 100) - 0.533421613740659 * c.get("TOV%") / 100 - 1.47832117364876 + 0.0116000447515056 * c.get("USG%") + 0.00794314773262778 * c.get("AST%") * (c.get("USG%"))) * 0.664434591654431)
            def s4 = 0.080331324158065 * (c.get("MP")/60)

            def rawASPM = s1 + s2 + s3 + s4

            collection.update([_id: c.get("_id")] as BasicDBObject, [$set: [rawASPM: rawASPM as double]] as BasicDBObject)
        }
    }
}
