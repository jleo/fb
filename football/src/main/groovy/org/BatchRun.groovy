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

        def date = Date.parse("yyyy-MM-dd", "2013-01-01")
        17.times {
            BetMatchBatchProcessorSpecifiedDate betMatchBatchProcessor = new BetMatchBatchProcessorSpecifiedDate(executorService, date.format("yyyy-MM-dd"), (date + 1).format("yyyy-MM-dd"));

            double minExpectation = Double.parseDouble(Props.getProperty("minExpectation"));//0.03;
            double minProbability = Double.parseDouble(Props.getProperty("minProbability"));//0.58;
            betMatchBatchProcessor.betBatchMatchHandicapGuarantee(minExpectation, minProbability);

            Settle s = new Settle()
            println date.format("yyyy-MM-dd")
            s.settle()
            date = date + 1
        }
    }
}
