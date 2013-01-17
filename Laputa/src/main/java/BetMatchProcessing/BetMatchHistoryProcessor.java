package BetMatchProcessing;

import HandicapProcessing.HandicapProcessing;
import Util.MongoDBUtil;
import Util.Props;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: snowhyzhang
 * Date: 13-1-17
 * Time: 下午5:12
 * To change this template use File | Settings | File Templates.
 */
public class BetMatchHistoryProcessor {

    public static void main(String[] args){
        BetMatchHistoryProcessor betMatchHistoryProcessor = new BetMatchHistoryProcessor();
        double seedExpectation = 0.0;
        double seedProbability = 0.5;
        int loopingExpectation = 20;
        int loppingProbability = 10;
        for (int i = 1; i < loopingExpectation; ++i){
            for (int j = 0; j < loppingProbability; ++i){
                betMatchHistoryProcessor.betBatchMatchHandicapGuarantee(seedExpectation, seedProbability);
                seedProbability = seedExpectation + j * 0.02;
            }
            seedExpectation = seedExpectation + i * 0.005;
        }
    }

    public void betBatchMatchHandicapGuarantee(double minExpectation, double minProbability){
        iBetMatchProcessing bmp = new BetHandicapMatchGuarantee();
        HandicapProcessing hp = new HandicapProcessing();

        bmp.setCollection(Props.getProperty("betHistory"));
        List<DBObject> matchList = getAllBettingMatch();
        System.out.println(matchList.size());
        int ProcessingMatch = 0;
        int BetOnMatch = 0;
        for (DBObject match: matchList){
            System.out.println("\n*_*_*_*_*_*_*_*_*_*");
            System.out.println("Processing match: " + ProcessingMatch);
            ++ProcessingMatch;
            double win = ((Number)match.get("w1")).doubleValue();
            double push = ((Number)match.get("p1")).doubleValue();
            double lose = ((Number)match.get("l1")).doubleValue();
            double winRate = ((Number)match.get("h1")).doubleValue();
            double loseRate = ((Number)match.get("h2")).doubleValue();

            String matchId = (String)match.get("matchId");
            double ch = ((Number)match.get("ch")).doubleValue();
            String cid = (String)match.get("cid");
            int abFlag = ((Number)match.get("abFlag")).intValue();
            Date matchTime = ((Date)match.get("time"));

            double handicap = getHandicap(ch, abFlag);
            if (handicap == -999){
                System.out.println("ERROR handicap!");
                continue;
            }
            if (handicap >=3 || handicap <= -3){
                System.out.println("The handicap is out of range: " + handicap);
                continue;
            }
            hp.setMatch(win, push, lose, handicap, winRate, loseRate, matchId, "snow", cid, matchTime);
            int isBet = hp.getResult(10000, 10, false);
            if (isBet != 0){
                continue;
            }
            isBet = bmp.betMatch(minExpectation, minProbability, 10, hp);
            if (isBet ==0){
                ++BetOnMatch;
            }
        }
        System.out.println("\n****\nTotal Match: " + matchList.size() + "\nBet on match: " + BetOnMatch);
    }

    private double getHandicap(double type, int abFlag) {
        double handicap = type / 4.0;
        if (abFlag == 1){
            return handicap;
        } else if (abFlag == 2){
            return handicap * -1;
        } else {
            return -999;
        }
    }

    private List<DBObject> getAllBettingMatch() {
        String cid = Props.getProperty("betCId");

        DBObject query = new BasicDBObject();
        query.put("ch", new BasicDBObject("$ne",null));
        query.put("cid", cid);

        try{
            query.put("time", new BasicDBObject("$gte", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2013-01-01 00:00:00")));
        } catch (Exception ex){
            ex.printStackTrace();
        }

        DBObject field = new BasicDBObject();
        field.put("h1", 1);
        field.put("h2", 1);
        field.put("abFlag", 1);
        field.put("ch", 1);
        field.put("matchId", 1);
        field.put("cid", 1);
        field.put("w1", 1);
        field.put("p1", 1);
        field.put("l1", 1);
        field.put("time", 1);

        MongoDBUtil dbUtil= new MongoDBUtil(Props.getProperty("MongoDBRemoteHost"),
                Props.getProperty("MongoDBRemotePort"),
                Props.getProperty("MongoDBRemoteName"));
        dbUtil.getConnection();

        List<DBObject> matchList = dbUtil.findAll(query, field, Props.getProperty("MatchRemoteResult"));

        dbUtil.closeConnection();
        return matchList;
    }

}
