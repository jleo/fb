package BetMatchProcessing;

import Util.MongoDBUtil;
import Util.Props;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: snowhyzhang
 * Date: 13-1-17
 * Time: 下午2:45
 * To change this template use File | Settings | File Templates.
 */
public abstract class BetMatchBasic implements iBetMatchProcessing {

    protected String matchBetCollection;

    private final BetMatchProcessor betMatchBatchProcessor;
    private boolean upsert;
    private OnBetListener onBetListener;

    public BetMatchBasic(BetMatchProcessor betMatchBatchProcessor, boolean upsert) {
        this.betMatchBatchProcessor = betMatchBatchProcessor;
        this.upsert = upsert;
    }

    public void setCollection(String collection) {
        this.matchBetCollection = collection;
    }

    public void setOnBet(OnBetListener onBetListener) {
        this.onBetListener = onBetListener;
    }

    protected void betOnMatch(String matchId, String cid, String clientId, int betOn, double bet, String aid,
                              double expectation, double probability, Date matchTime, String teamA, String teamB, int ch, double h1, double h2) {
        DBObject betQuery = new BasicDBObject();
        betQuery.put("matchId", matchId);
        betQuery.put("cid", cid);
        betQuery.put("clientId", clientId);
        betQuery.put("betOn", betOn);
        betQuery.put("bet", bet);
        betQuery.put("betType", 0);
        betQuery.put("status", "new");
        betQuery.put("aid", aid);
        betQuery.put("expectation", expectation);
        betQuery.put("probability", probability);
        betQuery.put("matchTime", matchTime);
        betQuery.put("teamA", teamA);
        betQuery.put("teamB", teamB);
        betQuery.put("ch", ch);
        betQuery.put("h1", h1);
        betQuery.put("h2", h2);


        DBObject uniqueQuery = new BasicDBObject();
        uniqueQuery.put("matchId", matchId);
        uniqueQuery.put("cid", cid);
        uniqueQuery.put("clientId", clientId);
        uniqueQuery.put("aid", aid);
        uniqueQuery.put("ch", ch);
        uniqueQuery.put("h1", h1);
        uniqueQuery.put("h2", h2);

        List<DBObject> matchList = betMatchBatchProcessor.getMatchList();
        for (DBObject dbObject : matchList) {
            if (dbObject.get("matchId").equals(matchId)) {
                betQuery.put("mtype", dbObject.get("mtype"));
                betQuery.put("time", dbObject.get("time"));
                betQuery.put("abFlag", dbObject.get("abFlag"));
            }
        }

        MongoDBUtil dbUtil = MongoDBUtil.getInstance(Props.getProperty("MongoDBRemoteHost"),
                Props.getProperty("MongoDBRemotePort"), Props.getProperty("MongoDBRemoteName"));


        if (betMatchBatchProcessor.printOnly()) {
            System.out.println(betQuery.toString());
        } else {
            if (upsert) {
                dbUtil.upsert(uniqueQuery, betQuery, false, matchBetCollection);
            } else {
                dbUtil.insert(betQuery, matchBetCollection);
            }
        }

        if (onBetListener != null) {

            onBetListener.onBet(betQuery.toMap());
        }
    }
}
