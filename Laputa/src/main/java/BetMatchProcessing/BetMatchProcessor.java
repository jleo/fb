package BetMatchProcessing;

import HandicapProcessing.HandicapProcessing;
import Util.MongoDBUtil;
import Util.Props;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: snowhyzhang
 * Date: 13-1-16
 * Time: 上午12:35
 * To change this template use File | Settings | File Templates.
 */
public class BetMatchProcessor {

    public static void main(String args[]){
        BetMatchProcessor betMatchProcessor = new BetMatchProcessor();
        betMatchProcessor.betBatchMatchHandicapGuarantee();
    }

    public void betBatchMatchHandicapGuarantee(){
        iBetMatchProcessing bmp = new BetHandicapMatchGuarantee();
        HandicapProcessing hp = new HandicapProcessing();

        List<DBObject> matchList = getAllBettingMatch();
        int betOnMatch = 0;
        System.out.println("Getting " + matchList.size() + " matches....");
        for (DBObject match: matchList){
            System.out.println("\n*_*_*_*_*_*_*_*_*_*");
            System.out.println("Processing match: " + betOnMatch);
            ++betOnMatch;
            double win = ((Number)match.get("w1")).doubleValue();
            double push = ((Number)match.get("p1")).doubleValue();
            double lose = ((Number)match.get("l1")).doubleValue();
            double winRate = ((Number)match.get("h1")).doubleValue();
            double loseRate = ((Number)match.get("h2")).doubleValue();

            String matchId = (String)match.get("matchId");
            double ch = ((Number)match.get("ch")).doubleValue();
            String cid = (String)match.get("cid");
            int abFlag = ((Number)match.get("abFlag")).intValue();

            double handicap = getHandicap(ch, abFlag);
            if (handicap == -999){
                System.out.println("ERROR handicap!");
                continue;
            }
            if (handicap >=3 || handicap <= -3){
                System.out.println("The handicap is out of range: " + handicap);
                continue;
            }
            hp.setMatch(win, push, lose, handicap, winRate, loseRate, matchId, "snow", cid);
            int isBet = hp.getResult(10000, 10, false);
            if (isBet != 0){
                continue;
            }
            isBet = bmp.betMatch(0.03, 0.58, 10, hp);
            if (isBet !=0){
                continue;
            }
        }
        System.out.println("\n****\nBet on match: " + betOnMatch);
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

        MongoDBUtil dbUtil= new MongoDBUtil(Props.getProperty("MongoDBRemoteHost"),
                Props.getProperty("MongoDBRemotePort"),
                Props.getProperty("MongoDBRemoteName"));
        dbUtil.getConnection();

        List<DBObject> matchList = dbUtil.findAll(query, field, Props.getProperty("BettingMatch"));

        dbUtil.closeConnection();
        return matchList;
    }
}
