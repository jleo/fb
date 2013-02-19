package org.basketball

import Util.MongoDBUtil
import com.mongodb.BasicDBObject
import org.joone.engine.Pattern
import org.joone.io.MemoryInputSynapse
import org.joone.io.MemoryOutputSynapse
import org.joone.net.NeuralNet
import org.joone.util.NormalizerPlugIn

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-18
 * Time: 下午3:22
 * Let's RocknRoll
 */
class Test {
    public static void main(String[] args) {
        MongoDBUtil mongoDBUtil = MongoDBUtil.getInstance("rm4", "15000", "bb");

        JooneScoreTrend joone = new JooneScoreTrend();

        NeuralNet nn = joone.restoreNeuralNet("trained")

        int hit = 0;

        def output = new File("/Users/jleo/list.txt")
        int total = output.readLines().size()
        def count = output.readLines().findIndexOf {
            it == "/boxscores/pbp/201203240LAC.html"
        }
        def idx = count
        def allTraining = new double[total - count][JooneScoreTrend.inputSize]
        def allReal = new double[total - count][JooneScoreTrend.outputSize]
        int scanned = 0
        output.eachLine { line ->
            if (scanned >= count) {
                line = line.replaceAll("/boxscores/pbp/", "").replaceAll(".html", "")
                def cursor = mongoDBUtil.findAllCursor(([:] as BasicDBObject).append("url", line), null, "quarter").sort([quarter: 1] as BasicDBObject)
                joone.add(cursor, allReal, 0, allTraining, true, 1, scanned - count)
            }
            scanned++
        }

        nn.getInputLayer().removeAllInputs()

        def inputSynapse = new MemoryInputSynapse();
        inputSynapse.setInputArray(allTraining);
        inputSynapse.setAdvancedColumnSelector((1..JooneScoreTrend.inputSize).join(","));

        NormalizerPlugIn normalizerPlugIn = new NormalizerPlugIn();
        normalizerPlugIn.setAdvancedSerieSelector((1..JooneScoreTrend.inputSize).join(","))
        normalizerPlugIn.setMax(1);//setting the max value as 1
        normalizerPlugIn.setMin(0);//setting the min value as 0
        normalizerPlugIn.setName("InputPlugin");
////////
//////
//        MovingAveragePlugIn averagePlugIn = new MovingAveragePlugIn();
//        averagePlugIn.setAdvancedMovAvgSpec("2");
//        averagePlugIn.setAdvancedSerieSelector((1..JooneScoreTrend.inputSize).join(","));
//        averagePlugIn.setName("Average Plugin");
//
//        normalizerPlugIn.addPlugIn(averagePlugIn);
        inputSynapse.addPlugIn(normalizerPlugIn);

        nn.getInputLayer().addInputSynapse(inputSynapse)

        nn.getOutputLayer().removeAllOutputs()
        def outputSynapse = new MemoryOutputSynapse();
        nn.getOutputLayer().addOutputSynapse(outputSynapse)

        nn.getMonitor().setTrainingPatterns(allTraining.length)
        nn.getMonitor().setTotCicles(1);
        nn.getMonitor().setLearning(false);

        nn.start();
        nn.getMonitor().Go();

        double[] result = new double[allTraining.length];

        int j = 0;
        for (Object o : outputSynapse.getAllPatterns()) {

            Pattern p = (Pattern) o;

            double max = -1;
            Integer maxIndex = 0;
            for (int i = 0; i < p.getArray().length; i++) {
                double c = p.getArray()[i];
                if (c > max) {
//                    if (i == 24)
//                        continue

                    max = c
                    maxIndex = i;
                }
            }
            result[j] = maxIndex + 20

            int expected = allReal[j].findIndexOf {
                it == 1
            }
            if (Math.abs(expected - maxIndex) < 3)
                hit++

            println allTraining[j]
            println "actual:" + (maxIndex + 20) + ", expected:" + (expected + 20)
            j++
        }

        println hit / allTraining.length * 100 + "%"
    }
}
