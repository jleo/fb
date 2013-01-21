package org

import BetMatchProcessing.BetMatchBatchProcessorSpecifiedDate
import Util.MongoDBUtil
import Util.Props
import com.mongodb.DBObject

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-1-18
 * Time: 下午6:08
 * Let's RocknRoll
 */
class BatchRun {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(Integer.parseInt(Props.getProperty("thread")));

        MongoDBUtil dbUtil = MongoDBUtil.getInstance(Props.getProperty("MongoDBRemoteHost"),
                Props.getProperty("MongoDBRemotePort"),
                Props.getProperty("MongoDBRemoteName"));

        BigDecimal seedExpectation = new BigDecimal("0.0");
        BigDecimal seedProbability = new BigDecimal("0.5");
        int loopingExpectation = 40;
        int loppingProbability = 12;

        def date = Date.parse("yyyy-MM-dd", "2013-01-01")

        List<DBObject> allBettingMatches = BetMatchBatchProcessorSpecifiedDate.getAllBettingMatch(date.format("yyyy-MM-dd"), (date + 20).format("yyyy-MM-dd"), dbUtil);
        BetMatchBatchProcessorSpecifiedDate betMatchBatchProcessor = new BetMatchBatchProcessorSpecifiedDate(executorService, allBettingMatches, dbUtil);

        for (int i = 1; i < loopingExpectation; ++i) {
            for (int j = 0; j < loppingProbability; ++j) {
                System.out.println("trying seedExpectation:" + seedExpectation + ", " + "seedProbability:" + seedProbability);

                betMatchBatchProcessor.betBatchMatchHandicapGuarantee(seedExpectation, seedProbability);

                Settle s = new Settle()
                String guarantee = "Guarantee" + seedExpectation.toString() + "" + seedProbability.toString()
                s.settle(guarantee, true, false)

            }
            seedExpectation = seedExpectation.add(new BigDecimal("0.005"));
            seedProbability = new BigDecimal("0.5");
        }
    }
}
