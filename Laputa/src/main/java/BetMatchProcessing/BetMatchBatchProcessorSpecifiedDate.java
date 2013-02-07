package BetMatchProcessing;

import HandicapProcessing.HandicapProcessing;
import Util.MongoDBUtil;
import Util.Props;
import com.mongodb.*;

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
public class BetMatchBatchProcessorSpecifiedDate extends BetMatchProcessor {
    public BetMatchBatchProcessorSpecifiedDate(ExecutorService executorService, List<DBObject> allBettingMatch, MongoDBUtil dbUtil) {
        super(executorService, allBettingMatch, dbUtil);
    }

    public static void main(String args[]) {
        // 包括fromDate，不包括toDate
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

        List<DBObject> allBettingMatches = BetMatchBatchProcessorSpecifiedDate.getAllBettingMatch(fromDate, toDate, dbUtil, new int[]{}, new String[]{},true);
        ExecutorService executorService = Executors.newFixedThreadPool(Integer.parseInt(Props.getProperty("thread")));
        BetMatchBatchProcessorSpecifiedDate betMatchBatchProcessor = new BetMatchBatchProcessorSpecifiedDate(executorService, allBettingMatches, dbUtil);

        double minExpectation = Double.parseDouble(Props.getProperty("minExpectation"));//0.03;
        double minProbability = Double.parseDouble(Props.getProperty("minProbability"));//0.58;

        ProbabilityAndExpectation probabilityAndExpectation = new FixedProbabilityAndExpectation(minProbability, minExpectation);
        betMatchBatchProcessor.betBatchMatchHandicapGuarantee(probabilityAndExpectation, allBettingMatches);
    }

    public void betBatchMatchHandicapGuarantee(final ProbabilityAndExpectation probabilityAndExpectation, List<DBObject> matchList) {
        long t1 = System.currentTimeMillis();

        final int[] BetOnMatch = {0};
        List<Future> futures = new ArrayList<Future>();
        for (final DBObject match : matchList) {
            Future future = executorService.submit(new Runnable() {

                public void run() {
                    iBetMatchProcessing bmp = new BetHandicapMatchGuarantee(BetMatchBatchProcessorSpecifiedDate.this, false);
                    HandicapProcessing hp = new HandicapProcessing();

                    bmp.setCollection(Props.getProperty("MatchBatchBetSpecifiedDate"));
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

                    isBet = bmp.betMatch(probabilityAndExpectation, 10, hp);
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
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        long t2 = System.currentTimeMillis();

        System.out.println("\n****\nTotal Match: " + matchList.size() + "\nBet on match: " + BetOnMatch[0] + "\ntotal time:" + (t2 - t1));
    }

    public static List<DBObject> getAllBettingMatch(String dateFrom, String dateTo, MongoDBUtil dbUtil, int[] chs, String[] mtypes, boolean lastCh) {
        String cid = Props.getProperty("betCId");

        DBObject query = new BasicDBObject();
        if (chs.length == 0) {
            query.put("ch", new BasicDBObject("$ne", null));
        } else {
            BasicDBList chList = new BasicDBList();
            for (int i = 0; i < chs.length; i++) {
                chList.add(chs[i]);
            }
            query.put("ch", new BasicDBObject("$in", chList));
        }

        if (mtypes.length == 0) {
            query.put("mtype", new BasicDBObject("$ne", null));
        } else {
            BasicDBList mtypeist = new BasicDBList();
            for (int i = 0; i < mtypes.length; i++) {
                mtypeist.add(mtypes[i]);
            }
            query.put("mtype", new BasicDBObject("$in", mtypeist));
        }

        BasicDBList list = new BasicDBList();
        list.add(new BasicDBObject("abFlag", new BasicDBObject("$ne", 0)));
        list.add(new BasicDBObject("abFlag", new BasicDBObject("$ne", null)));
        query.put("$and", list);
        query.put("cid", cid);
        query.put("status", 4);
        try {
            query.put("time", new BasicDBObject("$gte", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateFrom + " 00:00:00")).append("$lte", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTo + " 00:00:00")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println(query.toString());
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

        DBCollection handicap = dbUtil.getMongoDB().getCollection("handicap");
        DBCursor c = dbUtil.findAllCursor(query, field, Props.getProperty("MatchHistoryResult"));

        List<DBObject> results = new ArrayList<DBObject>();
        while (c.hasNext()) {
            DBObject dbObject = c.next();
            String matchId = (String) dbObject.get("matchId");
            Integer abFlag = (Integer) dbObject.get("abFlag");
            Integer ch = (Integer) dbObject.get("ch");

            if (abFlag == null || ch == null)
                continue;

            if (lastCh) {
                DBCursor limit = handicap.find(new BasicDBObject("matchId", matchId)).sort(new BasicDBObject("time", 1)).limit(1);
                if (limit.count() != 0) {
                    DBObject handicapObject = limit.next();

                    dbObject.put("ch", handicapObject.get("ch"));
                    dbObject.put("h1", handicapObject.get("h1"));
                    dbObject.put("h2", handicapObject.get("h2"));
                }
                limit.close();
            }
            results.add(dbObject);
        }
        c.close();
        System.out.println(results.size());
        return results;
    }
}
