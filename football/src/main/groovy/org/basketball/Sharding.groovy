package org.basketball

import Util.MongoDBUtil
import com.mongodb.BasicDBObject
import org.bson.types.ObjectId

import java.util.concurrent.*

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-9
 * Time: 下午3:45
 * To change this template use File | Settings | File Templates.
 */
class Sharding {
    private MongoDBUtil mongoDBUtil

    static def keyEvent = ["assist by", 'misses 2-pt shot', 'Defensive rebound', 'misses 3-pt shot', 'Turnover', 'makes 2-pt shot', 'Offensive foul', 'Personal foul', 'enters the game for', 'makes 3-pt shot', 'full timeout', 'makes free throw', 'Offensive rebound', 'misses free throw', 'Shooting foul', 'Violation by', 'Loose ball foul', 'Violation by', 'Technical foul', 'makes no shot', '20 second timeout', 'def tech foul', 'Clear path foul', 'Official timeout', 'Non unsport tech foul', 'Double technical foul', 'Away from play foul', 'Flagrant foul type 1', 'ejected from game', 'Double personal foul', 'Hanging tech foul', 'Team ejected from game', 'Delay tech foul', 'misses no shot', 'Taunting technical foul', 'Elbow foul by', 'Inbound foul', 'Flagrant foul type 2', 'makes technical free throw', 'Defensive three seconds', 'misses technical free throw', 'makes flagrant free throw', 'no timeout', 'misses flagrant free throw', 'Punching foul by', 'Def 3 sec tech foul', 'misses clear path free throw', 'makes clear path free throw', 'Shooting block foul', 'Offensive charge foul', 'Personal take foul']
    static Map keyEventAbbr = ["assist by": "ast", 'makes 2-pt shot': ['mk2s', 'mkcs'], 'misses 2-pt shot': ['mk2s', 'mkcs'], 'Defensive rebound': 'dr', 'misses 3-pt shot': ['m3s', 'mkls'], 'Turnover': 'to', 'Offensive foul': 'of', 'Personal foul': 'pf', 'enters the game for': 'egf', 'makes 3-pt shot': ['m3s', 'mkls'], 'full timeout': 'ft', 'Offensive rebound': 'or', 'makes free throw': ['mkft', 'mft'], 'misses free throw': ['mft', 'msft'], 'Violation by': 'vb', 'Shooting foul': 'sf', 'Loose ball foul': 'lbf', 'Technical foul': 'tf', 'makes no shot': 'mns', '20 second timeout': '2st', 'def tech foul': 'dtf', 'Clear path foul': 'cpf', 'Official timeout': 'ot', 'Non unsport tech foul': 'nutf', 'Double technical foul': 'dtf', 'Away from play foul': 'afpf', 'Flagrant foul type 1': 'fft1', 'ejected from game': 'efg', 'Double personal foul': 'dpf', 'Hanging tech foul': 'htf', 'Team ejected from game': 'tefg', 'Delay tech foul': 'dlytf', 'misses no shot': 'msns', 'Taunting technical foul': 'ttf', 'Elbow foul by': 'efb', 'Inbound foul': 'if', 'Flagrant foul type 2': 'fft2', 'makes technical free throw': 'mktft', 'Defensive three seconds': 'dts', 'misses technical free throw': 'mstft', 'makes flagrant free throw': 'mkfft', 'no timeout': 'nt', 'misses flagrant free throw': 'msfft', 'Punching foul by': 'pfb', 'Def 3 sec tech foul': 'd3stf', 'misses clear path free throw': 'mscpft', 'makes clear path free throw': 'mkcpft', 'Shooting block foul': 'sbf', 'Offensive charge foul': 'ocf', 'Personal take foul': 'ptf']
    static Map divide = ["ms2s": 28, dr: 26, m3s: 7, to: 11, mk2s: 24, of: 2, pf: 7, egf: 15, mk3s: 4, ft: 3, or: 16, mkft: 17, msft: 0]
//    static Map divide = ["ms2s":24,dr:22,m3s:3,to:19,mk2s:22,of:2,pf:3,egf:11,mk3s:4,ft:3,or:10,mkft:10,msft:0]
    final static BlockingQueue tasks = new ArrayBlockingQueue<>(30);

    public static void main(String[] args) {
        Sharding gameLogParser = new Sharding()

        Thread.start {
            def output = new File("/Users/jleo/list.txt")
            output.eachLine {
                def url = "http://www.basketball-reference.com" + it
                tasks.put([url: url, date: Date.parse("yyyyMMdd", it.replaceAll("/boxscores/pbp/", "")[0..7])])
            }
        }

        ExecutorService executorService = Executors.newFixedThreadPool(11);
        10.times {
            executorService.submit(new Runnable() {

                @Override
                void run() {
                    while (true) {
                        def task = tasks.poll(30, TimeUnit.SECONDS)
                        def url = task.url
                        def date = task.date

                        gameLogParser.parse(url, date)
                    }
                }
            })
        }
        executorService.shutdown()
    }

    Sharding() {
        this.mongoDBUtil = MongoDBUtil.getInstance("rm4", "15000", "bb")
    }

    void parse(url, date) {
        def statA = [:].withDefault {
            return 0
        }
        def statB = [:].withDefault {
            return 0
        }

        def urlShorten = url.replaceAll("/boxscores/pbp/", "").replaceAll(".html", "").replaceAll("http://www.basketball-reference.com", "")
        def events = []

        def c = mongoDBUtil.findAllCursor(new BasicDBObject("url", urlShorten), null, "log").sort(new BasicDBObject("sec": -1))
        int scoreA, scoreB
//            int lastScoreTotal = 0

        c.each { it ->
            String event = it.get("eventBoth")
            String eventA = it.get("eventA")
            String eventB = it.get("eventB")
            int quarter = it.get("quarter")
            String time = it.get("time")

            ObjectId id = it.get("_id")

//                String score = it.get("score")
//                if (score) {
//                    def splitted = score.split("-")
//                    scoreA = splitted[0] as int
//                    scoreB = splitted[1] as int
//                    lastScoreTotal = scoreA + scoreB
//                }

            def found = keyEvent.findResult { ke ->
                def found = null

                if (eventA && eventA?.indexOf(ke) != -1) {
                    found = [ke: keyEventAbbr.get(ke), team: "A"]
                }
                if (eventB && eventB?.indexOf(ke) != -1) {
                    found = [ke: keyEventAbbr.get(ke), team: "B"]
                }
                if (!found)
                    return null
                else
                    return found
            }

            if (time.indexOf("Overtime") != -1)
                return

//                def split = time.split(":")

//                int minute = split[0] as int
//                int second = (split[1] as double) as int

//                int sec = (4 - quarter) * 12 * 60 + minute * 60 + second
            if (found) {
//                    Event f = new Event()
//                    f.quarter = quarter
//
//                    f.seconds = minute * 60 + second
//
//                    f.keyEvent = found.ke
//                    f.team = found.team
//                    events << f

                if (found.team == "A") {
                    if (found.ke instanceof String) {
                        statA.put(found.ke, statA.get(found.ke) + 1)
                    } else {
                        found.ke.each { ke ->
                            statA.put(ke, statA.get(ke) + 1)
                        }
                    }
                } else {
                    if (found.ke instanceof String) {
                        statB.put(found.ke, statB.get(found.ke) + 1)
                    } else {
                        found.ke.each { ke ->
                            statB.put(ke, statB.get(ke) + 1)
                        }
                    }
                }
            }

            if (event) {

            } else {
                mongoDBUtil.update(new BasicDBObject("_id", id), new BasicDBObject("\$set", new BasicDBObject("ae": new BasicDBObject(statA)).append("be", new BasicDBObject(statB))), "log", true)
//                    mongoDBUtil.update(new BasicDBObject("_id", id), new BasicDBObject("\$set", new BasicDBObject("sec", sec)), "log", true)
            }
        }
    }
}

