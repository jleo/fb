package org.basketball

import Util.MongoDBUtil
import com.mongodb.BasicDBObject
import com.mongodb.DBCursor
import org.joone.engine.*
import org.joone.engine.learning.TeachingSynapse
import org.joone.io.MemoryInputSynapse
import org.joone.io.MemoryOutputSynapse
import org.joone.net.NeuralNet
import org.joone.util.DynamicAnnealing
import org.joone.util.NormalizerPlugIn

public class JooneScoreTrend implements NeuralNetListener, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 61750666816215273L;
    private NeuralNet nnet = null;
    private MemoryInputSynapse inputSynapse, desiredOutputSynapse;
    private MemoryOutputSynapse outputSynapse;

    int row = 200

    int layer = 30

    int cycle = 500

    BigDecimal learnRate = 0.8

    BigDecimal mon = 0.3
    boolean useRProp = false

    String hiddenLayerClass

    protected void initNeuralNet() {
        // First create the three layers
        LinearLayer input = new LinearLayer();
        LearnableLayer hidden = Class.forName(hiddenLayerClass).newInstance() as LearnableLayer;
        SigmoidLayer output = new SigmoidLayer();
        input.setLayerName("input");
        hidden.setLayerName("hidden");
        output.setLayerName("output");
        // set the dimensions of the layers

        input.setRows(inputSize);
        hidden.setRows(row);
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
        layer.times {
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
        monitor.setLearningRate(learnRate);
        monitor.setMomentum(mon);
//        monitor.setLearningRate(0.0001);
//        monitor.setMomentum(0.00000001);
        monitor.setTrainingPatterns(inputArray.length);
        monitor.setTotCicles(cycle);
        monitor.setLearning(true);

        DynamicAnnealing dynamicAnnealing = new DynamicAnnealing()
        dynamicAnnealing.setRate(5)
        dynamicAnnealing.setStep(15);
        dynamicAnnealing.setNeuralNet(nnet);

        nnet.addNeuralNetListener(dynamicAnnealing);
        if (useRProp) {

            monitor.getLearners().add(0, "org.joone.engine.RpropLearner");
            monitor.setLearningMode(1);
            monitor.setLearningRate(1.0)
        }

        nnet.addNeuralNetListener(this);
        nnet.start();
        nnet.getMonitor().Go();
        for (Object o : outputSynapse.getAllPatterns()) {
            Pattern p = (Pattern) o;
            System.out.println(10 * p.getArray()[0]);
        }
    }


    public void errorChanged(NeuralNetEvent e) {
        println "RMSE:" + ((Monitor) e.getSource()).getGlobalError();
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
        if (c % 20 == 0)
            System.out.println(c + " epochs remaining - RMSE = " + mon.getGlobalError());
    }

    public void netStoppedError(NeuralNetEvent e, String error) {
    }

    static final int numberOfFeature = 0
    static final int inputSize = 7
    static final int outputSize = 36

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



        def output = new File("/Users/jleo/list.txt")

        int count = output.readLines().findIndexOf {
            it == "/boxscores/pbp/200201120CHI.html"
        }

        def allTraining = new double[count][inputSize]
        def allReal = new double[count][outputSize]
        int idx = 0

        output.eachLine { line ->
            if (idx < count) {
                int startFrom = 0
                def last = ["ae": [:].withDefault { 0 }, "be": [:].withDefault { 0 }, "score": 0]
                line = line.replaceAll("/boxscores/pbp/", "").replaceAll(".html", "")
//                def cursor = mongoDBUtil.findAllCursor((new BasicDBObject([:]).append("sec", ['\$gte': 2160] as BasicDBObject)).append("url", line), null, "log").sort([sec: 1] as BasicDBObject).limit(1)
//                add(cursor, allReal, 0, allTraining, false, 1, idx, last)
//
//                startFrom += numberOfFeature * 2 + 1
//                cursor = mongoDBUtil.findAllCursor((new BasicDBObject([:]).append("sec", ['\$gte': 1440] as BasicDBObject)).append("url", line), null, "log").sort([sec: 1] as BasicDBObject).limit(1)
//                add(cursor, allReal, startFrom, allTraining, false, 1, idx, last)
//
//                startFrom += numberOfFeature * 2 + 1
                def cursor = mongoDBUtil.findAllCursor((new BasicDBObject([:]).append("sec", ['\$gte': 720] as BasicDBObject)).append("url", line), null, "log").sort([sec: 1] as BasicDBObject).limit(1)
                add(cursor, allReal, startFrom, allTraining, false, 1, idx, last)

                cursor = mongoDBUtil.findAllCursor((new BasicDBObject([:]).append("sec", ['\$gte': 0] as BasicDBObject)).append("url", line), null, "log").sort([sec: 1] as BasicDBObject).limit(1)
                add(cursor, allReal, 0, allTraining, true, 1, idx, last)

                idx++
            }
            if (idx % 100 == 0)
                println idx
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

        FileOutputStream stream = new FileOutputStream("train");
        ObjectOutputStream out = new ObjectOutputStream(stream); out.writeObject(allTraining);
        out.close()

        stream = new FileOutputStream("real");
        out = new ObjectOutputStream(stream); out.writeObject(allReal);
        out.close()
//
//        joone.train(allTraining, allReal);
//        joone.saveNeuralNet("trained")
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

    public static def add(DBCursor cursor, allReal, int startFrom, allTraining, addReal = false, discount, number, last) {
        int sum = 0
        int scoreA = 0
        int scoreB = 0
        cursor.each {
            def scoreAandB = (it.get("score") as String).split("-")
            scoreA = scoreAandB[0] as int
            scoreB = scoreAandB[1] as int

            sum = scoreA + scoreB

            int index = startFrom
            double[] stats = allTraining[number];
            if (!addReal) {

//                Sharding.keyEventAbbr.values().asList()[0..numberOfFeature - 1].each { abbr ->

//                def countA = it.get("ae").get("ms2s")
//                if (countA) {
//                    countA = countA as int
//                    stats[index] = countA - last["ae"][abbr]
//                    last["ae"][abbr] = countA as int
//                } else {
//                    stats[index] = 0
//                }
//                index += 1
//                def countB = it.get("be").get(abbr)
//                if (countB) {
//                    countB = countB as int
//                    stats[index] = countB - last["be"][abbr]
//                    last["be"][abbr] = countB as int
//                } else {
//                    stats[index] = 0
//                }
//                index += 1
//                }
                def feature = 0
                ['ast', 'dr', 'mft', 'mkft', 'to', 'of'].each { abr ->
                    def fa = it.get("ae").get(abr)
                    def assistA = fa == null ? 0 : fa as int

                    def fb = it.get("be").get(abr)
                    def assistB = fb == null ? 0 : fb as int

                    feature = assistA + assistB

                    stats[index] = feature
                    index++
                }

                stats[index] = sum// - last["score"]

//                stats[index] = (scoreA - scoreB)
//                index++
//                stats[index] = (scoreA + scoreB)
                last["score"] = sum
            }
            if (addReal) {
                if (sum - 100 < 0)
                    throw new RuntimeException()

                def lastQuarter = sum - last["score"]
                if (lastQuarter <= 30)
                    allReal[number][0] = 1
                else if (lastQuarter >= 65)
                    allReal[number][1] = 1
//                else if (lastQuarter > 30 && lastQuarter <= 35)
//                    allReal[number][2] = 1
                else
                    allReal[number][lastQuarter - 29] = 1
            }
        }
        cursor.close()
        return last
    }
}