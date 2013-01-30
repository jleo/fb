package BetMatchProcessing;

import HandicapProcessing.HandicapProcessing;
import Util.MongoDBUtil;
import Util.Props;
import com.mongodb.BasicDBList;
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
 * Date: 13-1-16
 * Time: 上午12:35
 * To change this template use File | Settings | File Templates.
 */
public class BetMatchBatchProcessor extends BetMatchProcessor {

    public BetMatchBatchProcessor(ExecutorService executorService, List<DBObject> allBettingMatch, MongoDBUtil dbUtil) {
        super(executorService, allBettingMatch, dbUtil);
    }

    public BetMatchBatchProcessor(ExecutorService executorService, List<DBObject> allBettingMatch, MongoDBUtil dbUtil, boolean printOnly) {
        super(executorService, allBettingMatch, dbUtil, printOnly);
    }

    public static void main(String args[]) {
        final List<DBObject> matchList = BetMatchBatchProcessor.getAllBettingMatch();
        double minExpectation = Double.parseDouble(args[0]);
        double minProbability = Double.parseDouble(args[1]);

        ExecutorService executorService = Executors.newFixedThreadPool(Integer.parseInt(Props.getProperty("thread")));

        MongoDBUtil dbUtil = MongoDBUtil.getInstance(Props.getProperty("MongoDBRemoteHost"),
                Props.getProperty("MongoDBRemotePort"),
                Props.getProperty("MongoDBRemoteName"));

        BetMatchBatchProcessor betMatchBatchProcessor = new BetMatchBatchProcessor(executorService, matchList, dbUtil, false);
        betMatchBatchProcessor.betBatchMatchHandicapGuarantee(minExpectation, minProbability, matchList);
    }

    public void betBatchMatchHandicapGuarantee(final double minExpectation, final double minProbability, List<DBObject> matchList) {
        long t1 = System.currentTimeMillis();
        int cpuNum = Runtime.getRuntime().availableProcessors();


        final int[] BetOnMatch = {0};
        final double minExp = minExpectation;
        final double minPro = minProbability;
        List<Future> futures = new ArrayList<Future>();
        for (final DBObject match : matchList) {
            Future future = executorService.submit(new Runnable() {

                public void run() {
                    iBetMatchProcessing bmp = new BetHandicapMatchGuarantee(BetMatchBatchProcessor.this, true);
                    HandicapProcessing hp = new HandicapProcessing();

                    bmp.setCollection(Props.getProperty("MatchBatchBet"));

                    double win = ((Number) match.get("w1")).doubleValue();
                    double push = ((Number) match.get("p1")).doubleValue();
                    double lose = ((Number) match.get("l1")).doubleValue();

                    int abFlag = ((Number) match.get("abFlag")).intValue();
                    double h1 = ((Number) match.get("h1")).doubleValue();
                    double h2 = ((Number) match.get("h2")).doubleValue();
                    double winRate = (abFlag == 1) ? h1 : h2;
                    double loseRate = (abFlag == 1) ? h2 : h1;

                    String teamA = match.get("tNameA").toString();
                    String teamB = match.get("tNameB").toString();

                    String matchId = (String) match.get("matchId");
                    int ch = ((Number) match.get("ch")).intValue();
                    String cid = (String) match.get("cid");


                    DBObject query = new BasicDBObject("matchId", matchId);
                    dbUtil.remove(query, "bet");


                    Date matchTime = ((Date) match.get("time"));

                    double handicap = ch / 4.0;

                    if (handicap >= 3 || handicap <= -3) {
                        System.out.println("The handicap is out of range: " + handicap);
                        return;
                    }
                    hp.setMatch(win, push, lose, handicap, winRate, loseRate, matchId, "snow", cid, matchTime, teamA,
                            teamB, ch);

                    int isBet = hp.getResult(false);
                    if (isBet != 0) {
                        return;
                    }
                    isBet = bmp.betMatch(minExp, minPro, 10, hp);
                    if (isBet == 0) {
                        ++BetOnMatch[0];
                    }

                }
            });
            futures.add(future);
        }
        int index = 0;
        for (Future f : futures) {
            try {
                f.get();
                System.out.println("done with" + index++);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (ExecutionException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        long t2 = System.currentTimeMillis();

        System.out.println("\n****\nTotal Match: " + matchList.size() + "\nBet on match: " + BetOnMatch[0] + "\ntotal time:" + (t2 - t1));
        executorService.shutdown();
    }

    private double getHandicap(double type, int abFlag) {
        return type / 4.0;
    }

    public static List<DBObject> getAllBettingMatch() {
        String cid = Props.getProperty("betCId");

        DBObject query = new BasicDBObject();
        query.put("ch", new BasicDBObject("$ne", null));

        BasicDBList list = new BasicDBList();
        list.add(new BasicDBObject("$ne", null));
        list.add(new BasicDBObject("$ne", 0));
        query.put("abFlag", list);

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
        field.put("time", 1);
        field.put("tNameA", 1);
        field.put("tNameB", 1);
        field.put("mtype", 1);

        MongoDBUtil dbUtil = MongoDBUtil.getInstance(Props.getProperty("MongoDBRemoteHost"),
                Props.getProperty("MongoDBRemotePort"),
                Props.getProperty("MongoDBRemoteName"));

        List<DBObject> matchList = dbUtil.findAll(query, field, Props.getProperty("BettingMatch"));

        return matchList;
    }

}
