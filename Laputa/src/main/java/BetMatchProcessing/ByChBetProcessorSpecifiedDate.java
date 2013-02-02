package BetMatchProcessing;

import Util.MongoDBUtil;
import Util.Props;
import com.mongodb.DBObject;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-2
 * Time: 下午9:27
 * To change this template use File | Settings | File Templates.
 */
public class ByChBetProcessorSpecifiedDate {
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

        List<DBObject> allBettingMatches = BetMatchBatchProcessorSpecifiedDate.getAllBettingMatch(fromDate, toDate, dbUtil, new int[]{}, new String[]{});
        ExecutorService executorService = Executors.newFixedThreadPool(Integer.parseInt(Props.getProperty("thread")));
        BetMatchBatchProcessorSpecifiedDate betMatchBatchProcessor = new BetMatchBatchProcessorSpecifiedDate(executorService, allBettingMatches, dbUtil);

        ProbabilityAndExpectation probabilityAndExpectation = new ByChProbabilityAndExpectation(new FromPropProbabilityAndExpectationFinder());
        betMatchBatchProcessor.betBatchMatchHandicapGuarantee(probabilityAndExpectation, allBettingMatches);
    }
}
