package BetMatchProcessing;

import Util.MongoDBUtil;
import Util.Props;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Created with IntelliJ IDEA.
 * User: snowhyzhang
 * Date: 13-1-17
 * Time: 下午2:45
 * To change this template use File | Settings | File Templates.
 */
public abstract class BetMatchBasic implements iBetMatchProcessing{

    protected void betOnMatch(String matchId, String cid, String clientId, String betOn, double bet, String aid,
                            double expectation, double probability){
        DBObject matchIdQuery = new BasicDBObject("matchId", matchId);

        DBObject betQuery = new BasicDBObject();
        betQuery.put("matchId", matchId);
        betQuery.put("cid", cid);
        betQuery.put("clientId", clientId);
        betQuery.put("betOn", betOn);
        betQuery.put("bet", bet);
        betQuery.put("status", "new");
        betQuery.put("aid", aid);
        betQuery.put("expectation", expectation);
        betQuery.put("probability", probability);

        MongoDBUtil dbUtil = new MongoDBUtil(Props.getProperty("MongoDBRemoteHost"),
                Props.getProperty("MongoDBRemotePort"), Props.getProperty("MongoDBRemoteName"));
        dbUtil.getConnection();

        dbUtil.upsert(matchIdQuery, betQuery, false, Props.getProperty("MatchBet"));

        dbUtil.closeConnection();
    }

}
