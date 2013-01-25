package org

import BetMatchProcessing.BetMatchBatchProcessorSpecifiedDate
import Util.MongoDBUtil
import Util.Props
import com.mongodb.DBObject
import org.gearman.client.GearmanJobResult
import org.gearman.client.GearmanJobResultImpl
import org.gearman.util.ByteUtils
import org.gearman.worker.AbstractGearmanFunction

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-1-22
 * Time: 下午12:13
 * Let's RocknRoll
 */
class GearmanFunction extends AbstractGearmanFunction {

        ExecutorService executorService = Executors.newFixedThreadPool(Integer.parseInt(Props.getProperty("thread")));
    static MongoDBUtil dbUtil = MongoDBUtil.getInstance(Props.getProperty("MongoDBRemoteHost"),
            Props.getProperty("MongoDBRemotePort"),
            Props.getProperty("MongoDBRemoteName"));


    static List<DBObject> allBettingMatches = BetMatchBatchProcessorSpecifiedDate.getAllBettingMatch(Props.getProperty("SpecifiedDateFrom"), Props.getProperty("SpecifiedDateTo"), dbUtil);
        BetMatchBatchProcessorSpecifiedDate betMatchBatchProcessor = new BetMatchBatchProcessorSpecifiedDate(executorService, allBettingMatches, dbUtil);
    Settle s = new Settle(dbUtil.getMongo())

    @Override
    public GearmanJobResult executeFunction() {

        StringBuffer sb = new StringBuffer(ByteUtils.fromUTF8Bytes((byte[]) this.data));

        def args = sb.toString().split(",")

        def seedExpectation = args[0] as double
        def seedProbability = args[1] as double

        StringBuilder text = new StringBuilder();
        BufferedReader reader = null;
        try {
            System.out.println("trying seedExpectation:" + seedExpectation + ", " + "seedProbability:" + seedProbability);

            betMatchBatchProcessor.betBatchMatchHandicapGuarantee(seedExpectation, seedProbability, allBettingMatches);

            String guarantee = "Guarantee" + seedExpectation.toString() + "" + seedProbability.toString()
            s.settle(guarantee, true, false)
        } catch (Exception e) {
            e.printStackTrace()
            GearmanJobResult gjr = new GearmanJobResultImpl(this.jobHandle,
                    false, "".getBytes(),
                    new byte[0], e.getMessage().getBytes(), 0, 0);
            return gjr;
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        println "done..."
        GearmanJobResult gjr = new GearmanJobResultImpl(this.jobHandle,
                true, text.toString().getBytes(),
                new byte[0], new byte[0], 0, 0);
        return gjr;
    }
}