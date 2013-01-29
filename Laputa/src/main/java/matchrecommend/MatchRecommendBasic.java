package matchrecommend;

import Util.MongoDBUtil;
import Util.Props;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Created with IntelliJ IDEA.
 * User: snowhyzhang
 * Date: 13-1-29
 * Time: 下午4:06
 * To change this template use File | Settings | File Templates.
 */
public abstract class MatchRecommendBasic implements iMatchRecommend{

    protected void saveRecommendMatch(String matchId, int betOn, String cid, double minRate){

        MongoDBUtil dbUtil = MongoDBUtil.getInstance(Props.getProperty("MongoDBRemoteHost"),
                Props.getProperty("MongoDBRemotePort"), Props.getProperty("MongoDBRemoteName"));
        String recommendMatchCollection = Props.getProperty("RecommendMatch");


        DBObject uniqQuey = new BasicDBObject();
        uniqQuey.put("matchId", matchId);
        uniqQuey.put("cid", cid);

        DBObject insertQuery = new BasicDBObject();
        insertQuery.put("matchId", matchId);
        insertQuery.put("betOn", betOn);
        insertQuery.put("cid", cid);
        insertQuery.put("rate", minRate);

        dbUtil.upsert(uniqQuey, insertQuery, false, recommendMatchCollection);
    }
}
