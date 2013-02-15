package BetMatchProcessing;

import HandicapProcessing.HandicapProcessing;
import Util.MongoDBUtil;
import Util.Props;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import java.text.SimpleDateFormat;
import java.util.*;
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
        final List<DBObject> matchList = BetMatchBatchProcessor.getAllBettingMatch(false);

        ExecutorService executorService = Executors.newFixedThreadPool(Integer.parseInt(Props.getProperty("thread")));

        MongoDBUtil dbUtil = MongoDBUtil.getInstance(Props.getProperty("MongoDBRemoteHost"),
                Props.getProperty("MongoDBRemotePort"),
                Props.getProperty("MongoDBRemoteName"));

        ProbabilityAndExpectation probabilityAndExpectation = new ByChProbabilityAndExpectation(new FromPropProbabilityAndExpectationFinder());

        BetMatchBatchProcessor betMatchBatchProcessor = new BetMatchBatchProcessor(executorService, matchList, dbUtil, false);
        betMatchBatchProcessor.betBatchMatchHandicapGuarantee(probabilityAndExpectation, matchList);
    }

    public void betBatchMatchHandicapGuarantee(final ProbabilityAndExpectation probabilityAndExpectation, List<DBObject> matchList) {
        long t1 = System.currentTimeMillis();
        int cpuNum = Runtime.getRuntime().availableProcessors();


        final int[] BetOnMatch = {0};
        List<Future> futures = new ArrayList<Future>();
        for (final DBObject match : matchList) {
            Future future = executorService.submit(new Runnable() {

                public void run() {
                    iBetMatchProcessing bmp = new BetHandicapMatchGuarantee(BetMatchBatchProcessor.this, true);
                    bmp.setOnBet(new OnBetListener() {
                        public void onBet(Map map) {
                            try {
                                String matchId = (String) map.get("matchId");
                                DBObject query = new BasicDBObject();
                                query.put("matchId", matchId);

                                DBObject update = new BasicDBObject();
                                update.put("matchId", matchId);

                                int newBetOn = (Integer) map.get("betOn");
                                update.put("betOn", newBetOn);

                                DBObject result = dbUtil.findOne(query, "email");
                                if (result == null) {
                                    sendMail(map);
                                    dbUtil.insert(update, "email");
                                } else {
                                    int betOn = (Integer) result.get("betOn");
                                    if (map.get("betOn").equals(betOn)) {
                                        System.out.println(matchId + "already sent,skip");
                                    } else {
                                        sendMail(map);
                                        dbUtil.update(query, update, "email", true);
                                    }
                                }

                            } catch (EmailException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                        }

                        private void sendMail(Map map) throws EmailException {
                            System.out.println("sending email...");

                            SimpleEmail email = new SimpleEmail();
                            email.setCharset("utf-8");
                            email.setHostName("smtp.googlemail.com");
                            email.setSmtpPort(465);
                            email.setAuthenticator(new DefaultAuthenticator("ggyyleo", "jf3hf2l1"));
                            email.setSSLOnConnect(true);
                            email.setFrom("ggyyleo@gmail.com");
                            email.setSubject("Let's roll");

                            int betOn = (Integer) map.get("betOn");
                            int ch = (Integer) map.get("ch");
                            double h1 = (Double) map.get("h1");
                            double h2 = (Double) map.get("h2");
                            String teamA = (String) map.get("teamA");
                            String teamB = (String) map.get("teamB");
                            BasicDBList mType = (BasicDBList) map.get("mtype");
                            String mtypeStr = "";
                            for (int i = 0; i < mType.size(); i++) {
                                mtypeStr += mType.get(i);
                            }
                            String msg = mtypeStr + "\n" + map.get("matchId") + "\n" + ch + "\n" + teamA + " vs " + teamB + "\nbet on " + (betOn == 0 ? teamA : teamB) + "\n h1:" + h1 + ", h2:" + h2 + ", \nmatch starts at:" + new SimpleDateFormat("MM-dd HH:mm").format(map.get("matchTime"));
                            email.setMsg(msg);
                            email.addTo("ggyyleo@gmail.com");
                            email.addTo("snowhyzhang@gmail.com");
                            email.send();
                        }
                    });

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

    public static List<DBObject> getAllBettingMatch(boolean startSoon) {
        String cid = Props.getProperty("betCId");

        DBObject query = new BasicDBObject();
        query.put("ch", new BasicDBObject("$ne", null));

        BasicDBList list = new BasicDBList();
        list.add(new BasicDBObject("abFlag", new BasicDBObject("$ne", 0)));
        list.add(new BasicDBObject("abFlag", new BasicDBObject("$ne", null)));
        query.put("$and", list);

        query.put("cid", cid);

        if (startSoon) {
            Calendar c = new GregorianCalendar();
            c.add(Calendar.MINUTE, 30);
            query.put("time", new BasicDBObject("$lte", c.getTime()));
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
        field.put("mtype", 1);

        MongoDBUtil dbUtil = MongoDBUtil.getInstance(Props.getProperty("MongoDBRemoteHost"),
                Props.getProperty("MongoDBRemotePort"),
                Props.getProperty("MongoDBRemoteName"));

        List<DBObject> matchList = dbUtil.findAll(query, field, Props.getProperty("BettingMatch"));

        return matchList;
    }

}
