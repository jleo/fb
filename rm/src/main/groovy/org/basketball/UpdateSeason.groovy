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
class UpdateSeason {
    public static void main(String[] args) {
        Mongo m = new Mongo("rm4", 15000)
        def collection = m.getDB("bb").getCollection("stat")
        collection.find([] as BasicDBObject, [_id: 1, match: 1] as BasicDBObject).each { cc ->
            def yearTeam = cc.get("match").replaceAll("http://www.basketball-reference.com/boxscores/", "").replaceAll(".html", "")

            def c = new GregorianCalendar()
            c.setTime(Date.parse("yyyyMMdd", yearTeam[0..7]))
            def year = c.get(Calendar.YEAR)
            def month = c.get(Calendar.MONTH)

            if (month > 8)
                year++

            collection.update([_id: cc.get("_id")] as BasicDBObject, [$set: [year: year, month: month]] as BasicDBObject)
        }
    }
}
