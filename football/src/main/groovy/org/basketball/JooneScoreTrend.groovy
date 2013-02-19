package org.basketball

import Util.MongoDBUtil
import com.mongodb.BasicDBObject
import com.mongodb.DBCursor
import org.joone.engine.*
import org.joone.engine.learning.TeachingSynapse
import org.joone.io.MemoryInputSynapse
import org.joone.io.MemoryOutputSynapse
import org.joone.net.NeuralNet
import org.joone.util.NormalizerPlugIn

public class JooneScoreTrend implements NeuralNetListener, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 61750666816215273L;
    private NeuralNet nnet = null;
    private MemoryInputSynapse inputSynapse, desiredOutputSynapse;
    private MemoryOutputSynapse outputSynapse;

    protected void initNeuralNet() {
        // First create the three layers
        DelayLayer input = new DelayLayer();
        SigmoidLayer hidden = new SigmoidLayer();
        SigmoidLayer output = new SigmoidLayer();
        input.setLayerName("input");
        hidden.setLayerName("hidden");
        output.setLayerName("output");
        // set the dimensions of the layers

        input.setTaps(576)
        input.setRows(1);
        hidden.setRows(50);
        output.setRows(outputSize);

        // Now create the two Synapses
        FullSynapse synapse_IH = new FullSynapse(); /* input -> hidden conn. */
        FullSynapse synapse_HO = new FullSynapse(); /* hidden -> output conn. */
        // Connect the input layer whit the hidden layer
        input.addOutputSynapse(synapse_IH);
        hidden.addInputSynapse(synapse_IH);
        // Connect the hidden layer whit the output layer
        hidden.addOutputSynapse(synapse_HO);
        output.addInputSynapse(synapse_HO);
        // the input to the neural net
        inputSynapse = new MemoryInputSynapse();
        input.addInputSynapse(inputSynapse);

        // the output of the neural net
        outputSynapse = new MemoryOutputSynapse();
        output.addOutputSynapse(outputSynapse);
        // The Trainer and its desired output
        desiredOutputSynapse = new MemoryInputSynapse();

        nnet = new NeuralNet();
        nnet.addLayer(input, NeuralNet.INPUT_LAYER);
        30.times {
            nnet.addLayer(hidden, NeuralNet.HIDDEN_LAYER);
        }
        nnet.addLayer(output, NeuralNet.OUTPUT_LAYER);


        TeachingSynapse trainer = new TeachingSynapse();
        trainer.setDesired(desiredOutputSynapse);


        nnet.setTeacher(trainer);
        output.addOutputSynapse(trainer);
//      File
    }


    public void train(double[][] inputArray, double[][] desiredOutputArray) {
        // set the inputs
        inputSynapse.setInputArray(inputArray);
        inputSynapse.setAdvancedColumnSelector((1..inputSize).join(","));

        NormalizerPlugIn normalizerPlugIn = new NormalizerPlugIn();
        normalizerPlugIn.setAdvancedSerieSelector((1..inputSize).join(","))
        normalizerPlugIn.setMax(1);//setting the max value as 1
        normalizerPlugIn.setMin(0);//setting the min value as 0
        normalizerPlugIn.setName("InputPlugin");
////
//
//        MaxM averagePlugIn = new MovingAveragePlugIn();
//        averagePlugIn.setAdvancedMovAvgSpec("1");
//        averagePlugIn.setAdvancedSerieSelector((1..inputSize).join(","));
//        averagePlugIn.setName("Average Plugin");
//
//        normalizerPlugIn.addPlugIn(averagePlugIn);
        inputSynapse.addPlugIn(normalizerPlugIn);

        // set the desired outputs
        desiredOutputSynapse.setInputArray(desiredOutputArray);
        desiredOutputSynapse.setAdvancedColumnSelector((1..outputSize).join(","));
        // get the monitor object to train or feed forward
        Monitor monitor = nnet.getMonitor();
        // set the monitor parameters
        monitor.setLearningRate(0.8);
        monitor.setMomentum(0.3);
//        monitor.setLearningRate(0.0001);
//        monitor.setMomentum(0.00000001);
        monitor.setTrainingPatterns(inputArray.length);
        monitor.setTotCicles(100);
        monitor.setLearning(true);
        nnet.addNeuralNetListener(this);
        nnet.start();
        nnet.getMonitor().Go();
        for (Object o : outputSynapse.getAllPatterns()) {
            Pattern p = (Pattern) o;
            System.out.println(10 * p.getArray()[0]);
        }
    }


    public void errorChanged(NeuralNetEvent e) {
    }

    public void netStarted(NeuralNetEvent e) {
        // TODO Auto-generated method stub
    }

    public void netStopped(NeuralNetEvent e) {
        System.out.println("Training finished");
    }

    public void cicleTerminated(NeuralNetEvent e) {
        Monitor mon = (Monitor) e.getSource();
        long c = mon.getCurrentCicle();
        if (c % 100 == 0)
            System.out.println(c + " epochs remaining - RMSE = " + mon.getGlobalError());
    }

    public void netStoppedError(NeuralNetEvent e, String error) {
    }

    static final int numberOfFeature = 11
    static final int inputSize = 432
    static final int outputSize = 110

    public void saveNeuralNet(String fileName) {
        try {
            FileOutputStream stream = new FileOutputStream(fileName); ObjectOutputStream out = new ObjectOutputStream(stream); out.writeObject(nnet);
            out.close();
        } catch (Exception excp) {
            excp.printStackTrace();
        }
    }


    public NeuralNet restoreNeuralNet(String filename) {
        try {
            FileInputStream stream = new FileInputStream(filename); ObjectInputStream inp = new ObjectInputStream(stream); return (NeuralNet) inp.readObject();
        } catch (Exception excp) {
            excp.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        MongoDBUtil mongoDBUtil = MongoDBUtil.getInstance("rm4", "15000", "bb");

        JooneScoreTrend joone = new JooneScoreTrend();
        joone.initNeuralNet();

        def output = new File("/Users/jleo/list.txt")

        int count = output.readLines().findIndexOf {
            it == "/boxscores/pbp/200201120CHI.html"
        }

        def allTraining = new double[count][inputSize]
        def allReal = new double[count][outputSize]
        int idx = 0

        output.eachLine { line ->
            if (idx < count) {
                line = line.replaceAll("/boxscores/pbp/", "").replaceAll(".html", "")
                def cursor = mongoDBUtil.findAllCursor(([:] as BasicDBObject).append("url", line), null, "second").sort([second: 1] as BasicDBObject)
                add(cursor, allReal, 0, allTraining, true, 1, idx)
            }
            idx++
        }

//        inputSynapse.setInputArray(inputArray);
//        joone.inputSynapse.setAdvancedColumnSelector((1..inputSize).join(","));
//
//        NormalizerPlugIn normalizerPlugIn = new NormalizerPlugIn();
//        normalizerPlugIn.setAdvancedSerieSelector((1..inputSize).join(","))
//        normalizerPlugIn.setMax(1);//setting the max value as 1
//        normalizerPlugIn.setMin(0);//setting the min value as 0
//        normalizerPlugIn.setName("InputPlugin");
////
//
//        MovingAveragePlugIn averagePlugIn = new MovingAveragePlugIn();
//        averagePlugIn.setAdvancedMovAvgSpec("2");
//        averagePlugIn.setAdvancedSerieSelector((1..inputSize).join(","));
//        averagePlugIn.setName("Average Plugin");
//
//        normalizerPlugIn.addPlugIn(averagePlugIn);
//        joone.inputSynapse.addPlugIn(normalizerPlugIn);

//        Monitor monitor = joone.nnet.getMonitor();
//        // set the monitor parameters
//        monitor.setLearningRate(0.8);
//        monitor.setMomentum(0.3);
////        monitor.setLearningRate(0.0001);
////        monitor.setMomentum(0.00000001);
//        monitor.setTrainingPatterns(allTraining.length);
//        monitor.setLearning(true);

//        JooneTools.train(joone.nnet, allTraining, allReal, 200, 0.01d, 100, joone, false);
        joone.train(allTraining, allReal);
        joone.saveNeuralNet("trained")
//        JooneTools.create_standard()
//        def cursor = mongoDBUtil.findAllCursor(([:] as BasicDBObject).append("url", ['\$ge': '201003300CLE'] as BasicDBObject), null, "quarter").sort([quarter: 1] as BasicDBObject)
//        def count = cursor.count()
//        allTraining = new double[count][inputSize]
//        allReal = new double[count][outputSize]
//        number = 0
//
//        startFrom = 0
//        add(cursor, allReal, startFrom, number, allTraining, true, 1)
//
//        allTraining.eachWithIndex { it, idx ->
//            def toTest = new double[1][inputSize];
//            toTest[0] = it
//            def expected = allReal[idx].findIndexOf { it == 1 }
//            def result = joone.test(toTest)
//            println "actual:" + result + ", expected:" + expected;
//            if (Math.abs(expected - result) <= 2)
//                hit++
//        }
//
//        println hit / count * 100 + "%"
    }

    public static void add(DBCursor cursor, allReal, int startFrom, allTraining, addReal = false, discount, number) {
        cursor.each {
            double[] stats = allTraining[number];

            int second = (it.get("to") as int)

            second /= 5

            int sum = (it.get("scoreA") as int) + (it.get("scoreB") as int)
            if (second >= 144)
                stats[575 - second] = sum
            else if (second == 5)
                allReal[number][Math.floor((sum - 100) / 5) as int] = 1
        }
        cursor.close()
    }
}