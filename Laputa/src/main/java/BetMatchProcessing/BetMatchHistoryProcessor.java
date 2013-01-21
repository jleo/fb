package BetMatchProcessing;

import HandicapProcessing.HandicapProcessing;
import Util.MongoDBUtil;
import Util.Props;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: snowhyzhang
 * Date: 13-1-17
 * Time: 下午5:12
 * To change this template use File | Settings | File Templates.
 */
public class BetMatchHistoryProcessor {
    ExecutorService executorService = Executors.newFixedThreadPool(Integer.parseInt(Props.getProperty("thread")));

    public static void main(String[] args) {
        BetMatchHistoryProcessor betMatchHistoryProcessor = new BetMatchHistoryProcessor();
        double seedExpectation = 0.1;
        double seedProbability = 0.53;

        List<String> matches = new ArrayList<String>();
        matches.add("592356");

        List<DBObject> matchList = betMatchHistoryProcessor.getAllBettingMatch(matches);
        System.out.println("trying seedExpectation:" + seedExpectation + ", " + "seedProbability:" + seedProbability);
        betMatchHistoryProcessor.betBatchMatchHandicapGuarantee(seedExpectation, seedProbability, matchList);
    }

    public void betBatchMatchHandicapGuarantee(final double minExpectation, final double minProbability, List<DBObject> matchList) {


        List<Future> futures = new ArrayList<Future>();
        final int[] ProcessingMatch = {0};
        final int[] BetOnMatch = {0};
        for (final DBObject match : matchList) {

            Runnable runnable = new Runnable() {
                public void run() {
                    iBetMatchProcessing bmp = new BetHandicapMatchGuarantee();
                    HandicapProcessing hp = new HandicapProcessing();
                    bmp.setCollection(Props.getProperty("MatchHistoryBet"));

                    System.out.println("\n*_*_*_*_*_*_*_*_*_*");
                    System.out.println("Processing match: " + ProcessingMatch[0]);
                    ++ProcessingMatch[0];
                    double win = ((Number) match.get("w1")).doubleValue();
                    double push = ((Number) match.get("p1")).doubleValue();
                    double lose = ((Number) match.get("l1")).doubleValue();
                    double winRate = ((Number) match.get("h1")).doubleValue();
                    double loseRate = ((Number) match.get("h2")).doubleValue();
                    String teamA = match.get("tNameA").toString();
                    String teamB = match.get("tNameB").toString();

                    String matchId = (String) match.get("matchId");
                    int ch = ((Number) match.get("ch")).intValue();
                    String cid = (String) match.get("cid");
                    int abFlag = ((Number) match.get("abFlag")).intValue();
                    Date matchTime = ((Date) match.get("time"));

                    double handicap = getHandicap(ch, abFlag);
                    if (handicap == -999) {
                        System.out.println("ERROR handicap!");
                        return;
                    }
                    if (handicap >= 3 || handicap <= -3) {
                        System.out.println("The handicap is out of range: " + handicap);
                        return;
                    }
                    hp.setMatch(win, push, lose, handicap, winRate, loseRate, matchId, "snow", cid, matchTime, teamA, teamB, ch);
                    int isBet = hp.getResult(false);
                    if (isBet != 0) {
                        return;
                    }
                    isBet = bmp.betMatch(minExpectation, minProbability, 10, hp);
                    if (isBet == 0) {
                        ++BetOnMatch[0];
                    }
                }
            };
            Future f = executorService.submit(runnable);
            futures.add(f);
        }
        int index = 0;
        for (Future f : futures) {
            try {
                if (f != null)
                    f.get();
                System.out.println("done with" + index++);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\n****\nTotal Match: " + matchList.size() + "\nBet on match: " + BetOnMatch[0]);
        executorService.shutdown();
    }

    private double getHandicap(double type, int abFlag) {
        double handicap = type / 4.0;
        if (abFlag == 0) {    //history 0 主
            return handicap;
        } else if (abFlag == 1) {
            return handicap * -1;           //history 1客
        } else {
            return -999;
        }
    }

    private List<DBObject> getAllBettingMatch(List<String> matchIds) {
        String cid = Props.getProperty("betCId");

        DBObject query = new BasicDBObject();
        query.put("ch", new BasicDBObject("$ne", null));
        query.put("abFlag", new BasicDBObject("$ne", null));
        query.put("cid", cid);

        try {
            query.put("matchId", new BasicDBObject("$in", matchIds));
        } catch (Exception ex) {
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
        field.put("tNameA", 1);
        field.put("tNameB", 1);

        MongoDBUtil dbUtil = MongoDBUtil.getInstance(Props.getProperty("MongoDBRemoteHost"),
                Props.getProperty("MongoDBRemotePort"),
                Props.getProperty("MongoDBRemoteName"));

        List<DBObject> matchList = dbUtil.findAll(query, field, Props.getProperty("MatchRemoteResult"));

        return matchList;
    }

}
