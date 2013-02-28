package org
/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 12-12-29
 * Time: 下午2:10
 * To change this template use File | Settings | File Templates.
 */

import com.mongodb.BasicDBObject
import com.mongodb.Mongo

db = new Mongo("rm4", 15000).getDB("fb");

def c = db.getCollection("missing")

def start = Date.parse("yyyy-MM-dd","2013-01-08")
def end = Date.parse("yyyy-MM-dd","2013-01-10")

def index = 0
(start..end).each {
    def cal = new GregorianCalendar()
    cal.setTime(it)
    def year =  cal.get(Calendar.YEAR)
    def month =  cal.get(Calendar.MONTH)+1
    def day =  cal.get(Calendar.DAY_OF_MONTH)
    c.save(new BasicDBObject("taskid",index++).append("host","odds2.zso8.com").append("path","/api/odds/oddshistory/$year/b_$year-$month-${day}.html".toString()).append("token",it.format("yyyy-MM-dd")))
    c.save(new BasicDBObject("taskid",index++).append("host","odds2.zso8.com").append("path","/api/odds/oddshistory/$year/m_$year-$month-${day}.html".toString()).append("token",it.format("yyyy-MM-dd")))
}

