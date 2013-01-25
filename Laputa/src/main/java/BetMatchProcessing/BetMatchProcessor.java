package BetMatchProcessing;

import Util.MongoDBUtil;
import com.mongodb.DBObject;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-1-23
 * Time: 下午3:19
 * Let's RocknRoll
 */
public abstract class BetMatchProcessor {
    protected List<DBObject> matchList;
    public ExecutorService executorService;
    MongoDBUtil dbUtil;

    public BetMatchProcessor(ExecutorService executorService, List<DBObject> allBettingMatch, MongoDBUtil dbUtil) {
        this.matchList = allBettingMatch;
        this.executorService = executorService;
        this.dbUtil = dbUtil;
    }

    List<DBObject> getMatchList(){
        return matchList;
    }
}
