package org

import BetMatchProcessing.BetMatchBatchProcessorSpecifiedDate
import Util.Props

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

        BigDecimal seedExpectation = new BigDecimal("0.0");
        BigDecimal seedProbability = new BigDecimal("0.5");
        int loopingExpectation = 40;
        int loppingProbability = 12;

        for (int i = 1; i < loopingExpectation; ++i) {
            for (int j = 0; j < loppingProbability; ++j) {
                seedProbability = seedProbability.add(new BigDecimal("0.02"));

                System.out.println("trying seedExpectation:" + seedExpectation + ", " + "seedProbability:" + seedProbability);


                def date = Date.parse("yyyy-MM-dd", "2013-01-01")
                17.times {
                    BetMatchBatchProcessorSpecifiedDate betMatchBatchProcessor = new BetMatchBatchProcessorSpecifiedDate(executorService, date.format("yyyy-MM-dd"), (date + 1).format("yyyy-MM-dd"));
                    betMatchBatchProcessor.betBatchMatchHandicapGuarantee(seedExpectation, seedProbability);

                    Settle s = new Settle()
                    println date.format("yyyy-MM-dd")
                    String guarantee = "Guarantee" + seedExpectation.toString() + "" + seedProbability.toString()
                    s.settle(guarantee)
                    date = date + 1
                }

            }
            seedExpectation = seedExpectation.add(new BigDecimal("0.005"));
            seedProbability = new BigDecimal("0.5");
        }
    }
}
