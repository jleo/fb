package BetMatchProcessing;

import HandicapProcessing.HandicapProcessing;
import Util.MongoDBUtil;
import Util.Props;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.text.SimpleDateFormat;
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
public class BetMatchBatchProcessorSpecifiedDate {

    ExecutorService executorService;
    private List<DBObject> allBettingMatch;
    private MongoDBUtil dbUtil;

    public BetMatchBatchProcessorSpecifiedDate(ExecutorService executorService, List<DBObject> allBettingMatch, MongoDBUtil dbUtil) {
        this.executorService = executorService;
        this.allBettingMatch = allBettingMatch;
        this.dbUtil = dbUtil;
    }

    public static List<DBObject> getAllBettingMatch(String dateFrom, String dateTo, MongoDBUtil dbUtil) {
        String cid = Props.getProperty("betCId");

        DBObject query = new BasicDBObject();
        query.put("ch", new BasicDBObject("$ne", null));
        query.put("abFlag", new BasicDBObject("$ne", null));
        query.put("cid", cid);
        try {
            query.put("time", new BasicDBObject("$gte", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateFrom + " 00:00:00")).append("$lte", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTo + " 00:00:00")));
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


        List<DBObject> matchList = dbUtil.findAll(query, field, Props.getProperty("MatchHistoryResult"));

        return matchList;
    }

    public static void main(String args[]) {
        String fromDate = null;
        String toDate = null;

        if (args.length == 0) {
            fromDate = Props.getProperty("SpecifiedDateFrom");
            toDate = Props.getProperty("SpecifiedDateTo");
        } else {
            fromDate = args[0];
            toDate = args[1];
        }

        MongoDBUtil dbUtil = MongoDBUtil.getInstance(Props.getProperty("MongoDBRemoteHost"),
                Props.getProperty("MongoDBRemotePort"),
                Props.getProperty("MongoDBRemoteName"));

        List<DBObject> allBettingMatches = BetMatchBatchProcessorSpecifiedDate.getAllBettingMatch(fromDate, toDate, dbUtil);
        ExecutorService executorService = Executors.newFixedThreadPool(Integer.parseInt(Props.getProperty("thread")));
        BetMatchBatchProcessorSpecifiedDate betMatchBatchProcessor = new BetMatchBatchProcessorSpecifiedDate(executorService, allBettingMatches, dbUtil);

        double minExpectation = Double.parseDouble(Props.getProperty("minExpectation"));//0.03;
        double minProbability = Double.parseDouble(Props.getProperty("minProbability"));//0.58;
        betMatchBatchProcessor.betBatchMatchHandicapGuarantee(minExpectation, minProbability);
    }

    public void betBatchMatchHandicapGuarantee(double minExpectation, double minProbability) {
        long t1 = System.currentTimeMillis();

        final int[] BetOnMatch = {0};
        final double minExp = minExpectation;
        final double minPro = minProbability;
        List<Future> futures = new ArrayList<Future>();
        for (final DBObject match : allBettingMatch) {
            Future future = executorService.submit(new Runnable() {

                public void run() {
                    iBetMatchProcessing bmp = new BetHandicapMatchGuarantee();
                    HandicapProcessing hp = new HandicapProcessing();

                    bmp.setCollection(Props.getProperty("MatchBatchBetSpecifiedDate"));
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

        System.out.println("\n****\nTotal Match: " + allBettingMatch.size() + "\nBet on match: " + BetOnMatch[0] + "\ntotal time:" + (t2 - t1));
    }

    private double getHandicap(double type, int abFlag) {
        return type / 4.0;
    }


}
