package matchrecommend;

import Util.MongoDBUtil;
import Util.Props;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: snowhyzhang
 * Date: 13-1-29
 * Time: 下午4:51
 * To change this template use File | Settings | File Templates.
 */
public abstract class MatchRecommendProcessor {

    public abstract void recommendMatches(double minExpectation, double minProbability);

    protected static List<DBObject> getRecommendMatches(){
        String cid = Props.getProperty("betCId");

        DBObject query = new BasicDBObject();
        query.put("ch", new BasicDBObject("$ne", null));
        query.put("abFlag", new BasicDBObject("$ne", null));
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

        return dbUtil.findAll(query, field, Props.getProperty("BettingMatch"));
    }
}
