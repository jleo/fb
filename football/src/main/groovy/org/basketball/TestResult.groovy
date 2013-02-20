package org.basketball

import org.joone.engine.Pattern
import org.joone.io.MemoryInputSynapse
import org.joone.io.MemoryOutputSynapse
import org.joone.net.NeuralNet
import org.joone.util.NormalizerPlugIn

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-19
 * Time: 下午10:21
 * To change this template use File | Settings | File Templates.
 */
class TestResult {
    public static void main(String[] args) {
        JooneScoreTrend joone = new JooneScoreTrend();

        int hit5 = 0;
        int hit10 = 0;
        int hit15 = 0;

        FileInputStream stream = new FileInputStream("test");
        ObjectInputStream out = new ObjectInputStream(stream);
        def allTraining = out.readObject()
        out.close()

        stream = new FileInputStream("testreal");
        out = new ObjectInputStream(stream);
        def allReal = out.readObject()
        out.close()

        NeuralNet nn = joone.restoreNeuralNet(args[0])
        nn.getInputLayer().removeAllInputs()

        def inputSynapse = new MemoryInputSynapse();
        inputSynapse.setInputArray(allTraining);
        inputSynapse.setAdvancedColumnSelector((1..JooneScoreTrend.inputSize).join(","));

        NormalizerPlugIn normalizerPlugIn = new NormalizerPlugIn();
        normalizerPlugIn.setAdvancedSerieSelector((1..JooneScoreTrend.inputSize).join(","))
        normalizerPlugIn.setMax(1);//setting the max value as 1
        normalizerPlugIn.setMin(0);//setting the min value as 0
        normalizerPlugIn.setName("InputPlugin");
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

            int expected = allReal[j].findIndexOf {
                it == 1
            }
            if (Math.abs(expected - maxIndex) <= 5)
                hit5++

            if (Math.abs(expected - maxIndex) <= 10)
                hit10++

            if (Math.abs(expected - maxIndex) <= 15)
                hit15++

            println "actual:" + (maxIndex+100)  + ", expected:" + (expected+100)
            j++
        }

        println hit5 / allTraining.length * 100 + "%"
        println hit10 / allTraining.length * 100 + "%"
        println hit15 / allTraining.length * 100 + "%"
    }
}
