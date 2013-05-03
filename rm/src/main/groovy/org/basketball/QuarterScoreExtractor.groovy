//package org.basketball
//
//import Util.MongoDBUtil
//import com.mongodb.BasicDBObject
//import org.bson.types.ObjectId
//
///**
// * Created with IntelliJ IDEA.
// * User: jleo
// * Date: 13-2-9
// * Time: 下午9:32
// * To change this template use File | Settings | File Templates.
// */
//class QuarterScoreExtractor {
//
//    static def keyEvent = ['misses 2-pt shot', 'Defensive rebound', 'misses 3-pt shot', 'Turnover', 'makes 2-pt shot', 'Offensive foul', 'Personal foul', 'enters the game for', 'makes 3-pt shot', 'full timeout', 'Offensive rebound', 'Shooting foul', 'makes free throw', 'Violation by', 'Loose ball foul', 'misses free throw', 'Violation by', 'Technical foul', 'makes no shot', '20 second timeout', 'def tech foul', 'Clear path foul', 'Official timeout', 'Non unsport tech foul', 'Double technical foul', 'Away from play foul', 'Flagrant foul type 1', 'ejected from game', 'Double personal foul', 'Hanging tech foul', 'Team ejected from game', 'Delay tech foul', 'misses no shot', 'Taunting technical foul', 'Elbow foul by', 'Inbound foul', 'Flagrant foul type 2', 'makes technical free throw', 'Defensive three seconds', 'misses technical free throw', 'makes flagrant free throw', 'no timeout', 'misses flagrant free throw', 'Punching foul by', 'Def 3 sec tech foul', 'misses clear path free throw', 'makes clear path free throw', 'Shooting block foul', 'Offensive charge foul', 'Personal take foul']
//    static def keyEventAbbr = ['misses 2-pt shot': 'ms2s', 'Defensive rebound': 'dr', 'misses 3-pt shot': 'm3s', 'Turnover': 'to', 'makes 2-pt shot': 'mk2s', 'Offensive foul': 'of', 'Personal foul': 'pf', 'enters the game for': 'egf', 'makes 3-pt shot': 'mk3s', 'full timeout': 'ft', 'Offensive rebound': 'or', 'Shooting foul': 'sf', 'makes free throw': 'mft', 'Violation by': 'vb', 'Loose ball foul': 'lbf', 'misses free throw': 'mft', 'Technical foul': 'tf', 'makes no shot': 'mns', '20 second timeout': '2st', 'def tech foul': 'dtf', 'Clear path foul': 'cpf', 'Official timeout': 'ot', 'Non unsport tech foul': 'nutf', 'Double technical foul': 'dtf', 'Away from play foul': 'afpf', 'Flagrant foul type 1': 'fft1', 'ejected from game': 'efg', 'Double personal foul': 'dpf', 'Hanging tech foul': 'htf', 'Team ejected from game': 'tefg', 'Delay tech foul': 'dlytf', 'misses no shot': 'msns', 'Taunting technical foul': 'ttf', 'Elbow foul by': 'efb', 'Inbound foul': 'if', 'Flagrant foul type 2': 'fft2', 'makes technical free throw': 'mktft', 'Defensive three seconds': 'dts', 'misses technical free throw': 'mstft', 'makes flagrant free throw': 'mkfft', 'no timeout': 'nt', 'misses flagrant free throw': 'msfft', 'Punching foul by': 'pfb', 'Def 3 sec tech foul': 'd3stf', 'misses clear path free throw': 'mscpft', 'makes clear path free throw': 'mkcpft', 'Shooting block foul': 'sbf', 'Offensive charge foul': 'ocf', 'Personal take foul': 'ptf']
//
//
//    public static void main(String[] args) {
//        MongoDBUtil mongoDBUtil = MongoDBUtil.getInstance("localhost", "27017", "bb")
//
//        def statA = [:]
//        def statB = [:]
//
//        def output = new File("/Users/jleo/list.txt")
//        output.eachLine { url ->
//            statA = [:].withDefault {
//                return 0
//            }
//            statB = [:].withDefault {
//                return 0
//            }
//
//            def urlShorten = url.replaceAll("/boxscores/pbp/", "").replaceAll(".html", "")
//            def events = []
//
//            def c = mongoDBUtil.findAllCursor(new BasicDBObject("url", urlShorten), null, "log").sort(new BasicDBObject("sec": -1))
//            int scoreA, scoreB
////            int lastScoreTotal = 0
//
//            c.each { it ->
//                String event = it.get("eventBoth")
//                String eventA = it.get("eventA")
//                String eventB = it.get("eventB")
//                int quarter = it.get("quarter")
//                String time = it.get("time")
//
//                ObjectId id = it.get("_id")
//
////                String score = it.get("score")
////                if (score) {
////                    def splitted = score.split("-")
////                    scoreA = splitted[0] as int
////                    scoreB = splitted[1] as int
////                    lastScoreTotal = scoreA + scoreB
////                }
//
//                def found = keyEvent.findResult { ke ->
//                    def found = null
//
//                    if (eventA && eventA?.indexOf(ke) != -1) {
//                        found = [ke: keyEventAbbr.get(ke), team: "A"]
//                    }
//                    if (eventB && eventB?.indexOf(ke) != -1) {
//                        found = [ke: keyEventAbbr.get(ke), team: "B"]
//                    }
//                    if (!found)
//                        return null
//                    else
//                        return found
//                }
//
//                if (time.indexOf("Overtime") != -1)
//                    return
//
////                def split = time.split(":")
//
////                int minute = split[0] as int
////                int second = (split[1] as double) as int
//
////                int sec = (4 - quarter) * 12 * 60 + minute * 60 + second
//                if (found) {
////                    Event f = new Event()
////                    f.quarter = quarter
////
////                    f.seconds = minute * 60 + second
////
////                    f.keyEvent = found.ke
////                    f.team = found.team
////                    events << f
//
//                    if (found.team == "A")
//                        statA.put(found.ke, statA.get(found.ke) + 1)
//                    else
//                        statB.put(found.ke, statB.get(found.ke) + 1)
//                }
//
//                if (event) {
//
//                } else {
//                    mongoDBUtil.update(new BasicDBObject("_id", id), new BasicDBObject("\$set", new BasicDBObject("ae": new BasicDBObject(statA)).append("be", new BasicDBObject(statB))), "log", true)
////                    mongoDBUtil.update(new BasicDBObject("_id", id), new BasicDBObject("\$set", new BasicDBObject("sec", sec)), "log", true)
//                }
//            }
//        }
//
//    }
//}
