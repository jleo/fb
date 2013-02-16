package org.basketball
import Util.MongoDBUtil
import com.mongodb.BasicDBList
import com.mongodb.BasicDBObject
import com.mongodb.DBCursor
import org.joone.engine.*
import org.joone.engine.learning.TeachingSynapse
import org.joone.io.MemoryInputSynapse
import org.joone.io.MemoryOutputSynapse
import org.joone.net.NeuralNet

public class Joone implements NeuralNetListener, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 61750666816215273L;
    private NeuralNet nnet = null;
    private MemoryInputSynapse inputSynapse, desiredOutputSynapse;
    private MemoryOutputSynapse outputSynapse;

    protected void initNeuralNet() {
        // First create the three layers
        LinearLayer input = new LinearLayer();
        SigmoidLayer hidden = new SigmoidLayer();
        SigmoidLayer output = new SigmoidLayer();
        input.setLayerName("input");
        hidden.setLayerName("hidden");
        output.setLayerName("output");
        // set the dimensions of the layers

        input.setRows(inputSize);
        hidden.setRows(20);
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
        TeachingSynapse trainer = new TeachingSynapse();
        trainer.setDesired(desiredOutputSynapse);
        nnet = new NeuralNet();
        nnet.addLayer(input, NeuralNet.INPUT_LAYER);
        30.times {
            nnet.addLayer(hidden, NeuralNet.HIDDEN_LAYER);
        }
        nnet.addLayer(output, NeuralNet.OUTPUT_LAYER);
        nnet.setTeacher(trainer);
        output.addOutputSynapse(trainer);
//      File
    }

    public int test(double[][] d) {
        inputSynapse.setInputArray(d);
        inputSynapse.setAdvancedColumnSelector((1..inputSize).join(","));
        nnet.getMonitor().setTotCicles(10);
        nnet.start();
        nnet.getMonitor().Go();
        for (Object o : outputSynapse.getAllPatterns()) {
            Pattern p = (Pattern) o;

            double max = -1;
            int maxIndex = 0;
            for (int i = 0; i < p.getArray().length; i++) {
                double c = p.getArray()[i];
                if (c > max) {
                    if (i == 13)
                        continue;
                    max = c
                    maxIndex = i;
                }
            }
            return maxIndex;
        }
    }

    public void train(double[][] inputArray, double[][] desiredOutputArray) {
        // set the inputs
        inputSynapse.setInputArray(inputArray);
        inputSynapse.setInputFull(true)

        inputSynapse.setAdvancedColumnSelector((1..inputSize).join(","));

//        NormalizerPlugIn normalizerPlugIn = new NormalizerPlugIn();
//        normalizerPlugIn.setAdvancedSerieSelector((1..inputSize).join(","))
//        normalizerPlugIn.setMax(1);//setting the max value as 1
//        normalizerPlugIn.setMin(0);//setting the min value as 0
//        normalizerPlugIn.setName("InputPlugin");
//        inputSynapse.addPlugIn(normalizerPlugIn);

        // set the desired outputs
        desiredOutputSynapse.setInputArray(desiredOutputArray);
        desiredOutputSynapse.setAdvancedColumnSelector((1..outputSize).join(","));
        // get the monitor object to train or feed forward
        Monitor monitor = nnet.getMonitor();
        // set the monitor parameters
        monitor.setLearningRate(0.0001);
        monitor.setMomentum(0.00000001);
        monitor.setTrainingPatterns(inputArray.length);
        monitor.setTotCicles(50);
        monitor.setLearning(true);
        nnet.addNeuralNetListener(this);
        nnet.start();
        nnet.getMonitor().Go();
        for (Object o : outputSynapse.getAllPatterns()) {
            Pattern p = (Pattern) o;
            System.out.println(10 * p.getArray()[0]);
        }
    }

    public void cicleTerminated(NeuralNetEvent e) {

    }

    public void errorChanged(NeuralNetEvent e) {
    }

    public void netStarted(NeuralNetEvent e) {
        // TODO Auto-generated method stub
    }

    public void netStopped(NeuralNetEvent e) {
        // TODO Auto-generated method stub
    }

    public void netStoppedError(NeuralNetEvent e, String error) {
    }

    static final int numberOfFeature = 11
    static final int inputSize = numberOfFeature * 2 * 3
    static final int outputSize = 30

    public static void main(String[] args) {
        MongoDBUtil mongoDBUtil = MongoDBUtil.getInstance(args[0], args[1], "bb");

        Joone joone = new Joone();
        joone.initNeuralNet();


        def cursor = mongoDBUtil.findAllCursor((["\$or": [["ae": ["\$exists": true] as BasicDBObject] as BasicDBObject, ["be": ["\$exists": true] as BasicDBObject] as BasicDBObject] as BasicDBList] as BasicDBObject).append("url", ['\$lte': '200203300CLE'] as BasicDBObject).append("sec", 720).append("ot", 0), null, "log")
        def allTraining = new double[cursor.count()][inputSize]
        def allReal = new double[cursor.count()][outputSize]
        int number = 0

        int startFrom = 0
        add(cursor, allReal, startFrom, number, allTraining, true, 1)

        cursor = mongoDBUtil.findAllCursor((["\$or": [["ae": ["\$exists": true] as BasicDBObject] as BasicDBObject, ["be": ["\$exists": true] as BasicDBObject] as BasicDBObject] as BasicDBList] as BasicDBObject).append("url", ['\$lte': '200203300CLE'] as BasicDBObject).append("sec", 1440).append("ot", 0), null, "log")
        startFrom += numberOfFeature * 2
        add(cursor, allReal, startFrom, number, allTraining, false, 2 / 3)

        cursor = mongoDBUtil.findAllCursor((["\$or": [["ae": ["\$exists": true] as BasicDBObject] as BasicDBObject, ["be": ["\$exists": true] as BasicDBObject] as BasicDBObject] as BasicDBList] as BasicDBObject).append("url", ['\$lte': '200203300CLE'] as BasicDBObject).append("sec", 2160).append("ot", 0), null, "log")
        startFrom += numberOfFeature * 2
        add(cursor, allReal, startFrom, number, allTraining, false, 1 / 3)

        joone.train(allTraining, allReal);

//        def tests = mongoDBUtil.findAllCursor((["\$or": [["ae": ["\$exists": true] as BasicDBObject] as BasicDBObject, ["be": ["\$exists": true] as BasicDBObject] as BasicDBObject] as BasicDBList] as BasicDBObject).append("url", ['\$gt': '201210300CLE'] as BasicDBObject).append("sec", 720).append("ot", 0), null, "log")
//        def count = tests.count()
//        def hit = new AtomicInteger()
//        tests.each {
//            double[][] stats = new double[1][inputSize];
//            int index = 0
//            Sharding.keyEventAbbr.values().asList()[0..numberOfFeature - 1].each { abbr ->
//                def countA = it.get("ae").get(abbr)
////                println abbr
////                println countA
//
//                if (countA) {
//                    countA = countA as int
//
//                    if (countA >= Sharding.divide.get(abbr)) {
//                        stats[0][index] = 1
//                    }
//                } else {
//                    stats[0][index] = 0
//                }
//                index += 1
//                def countB = it.get("be").get(abbr)
//                if (countB) {
//                    countB = countB as int
//                    if (countB >= Sharding.divide.get(abbr)) {
//                        stats[0][index] = 1
//                    }
//                } else {
//                    stats[0][index] = 0
//                }
//                index += 1
//            }
////            stats[index] = (it.get("sec") as int) / 10
//
//            def scores = it.get("score").split("-")
//            int current = (scores[0] as int) + (scores[1] as int)
////            stats[index] = current
////
////            index++
//
//            int pos = joone.test(stats)
//
//            println it.get("url")
//            println it.get("sec")
//            int total = it.get("total")
//            println pos
//            println "result:" + ((pos * 3) + current) + ", actual:" + total
//
//            if (current + pos * 3 <= total && current + pos * 4 >= total) {
//                hit++
//            }
//        }
        int hit = 0;

        cursor = mongoDBUtil.findAllCursor((["\$or": [["ae": ["\$exists": true] as BasicDBObject] as BasicDBObject, ["be": ["\$exists": true] as BasicDBObject] as BasicDBObject] as BasicDBList] as BasicDBObject).append("url", ['\$gte': '201210300CLE'] as BasicDBObject).append("sec", 720).append("ot", 0), null, "log")
        def count = cursor.count()
        allTraining = new double[count][inputSize]
        allReal = new double[count][outputSize]
        number = 0

        startFrom = 0
        add(cursor, allReal, startFrom, number, allTraining, true, 1)

        cursor = mongoDBUtil.findAllCursor((["\$or": [["ae": ["\$exists": true] as BasicDBObject] as BasicDBObject, ["be": ["\$exists": true] as BasicDBObject] as BasicDBObject] as BasicDBList] as BasicDBObject).append("url", ['\$gte': '201210300CLE'] as BasicDBObject).append("sec", 1440).append("ot", 0), null, "log")
        startFrom += numberOfFeature * 2
        add(cursor, allReal, startFrom, number, allTraining, false, 2 / 3)

        cursor = mongoDBUtil.findAllCursor((["\$or": [["ae": ["\$exists": true] as BasicDBObject] as BasicDBObject, ["be": ["\$exists": true] as BasicDBObject] as BasicDBObject] as BasicDBList] as BasicDBObject).append("url", ['\$gte': '201210300CLE'] as BasicDBObject).append("sec", 2160).append("ot", 0), null, "log")
        startFrom += numberOfFeature * 2
        add(cursor, allReal, startFrom, number, allTraining, false, 1 / 3)

        allTraining.eachWithIndex { it, idx ->
            def toTest = new double[1][inputSize];
            toTest[0] = it
            def expected = allReal[idx].findIndexOf { it == 1 }
            def result = joone.test(toTest)
            println "actual:" + result + ", expected:" + expected;
            if (Math.abs(expected - result) <= 2)
                hit++
        }

        println hit / count * 100 + "%"
    }

    public static void add(DBCursor cursor, allReal, int startFrom, int number, allTraining, addReal = false, discount) {
        cursor.each {
            if (!it.get("total"))
                return

            if (number >= allTraining.size()) {
                println allTraining.size() + " over"
                return
            }
            double[] stats = allTraining[number];
            int index = startFrom
            Sharding.keyEventAbbr.values().asList()[0..numberOfFeature - 1].each { abbr ->
                def countA = it.get("ae").get(abbr)
//                println abbr
//                println countA

                if (countA) {
                    countA = countA as int

                    if (countA >= Sharding.divide.get(abbr) * discount) {
                        stats[index] = 1
                    }
                } else {
                    stats[index] = 0
                }
                index += 1
                def countB = it.get("be").get(abbr)
                if (countB) {
                    countB = countB as int
                    if (countB >= Sharding.divide.get(abbr) * discount) {
                        stats[index] = 1
                    }
                } else {
                    stats[index] = 0
                }
                index += 1
            }
//            stats[index] = (it.get("sec") as int) / 10

            def scores = it.get("score").split("-")
            int current = (scores[0] as int) + (scores[1] as int)
//            stats[index] = current

//            index++

            int result = (it.get("total") - current) / 3
//            println result
            if (addReal) {
                double[] real = new double[outputSize];
                real[result] = 1
                allReal[number] = real
            }

            allTraining[number] = stats
            number++

//            if (number % 1000 == 0)
//                println number
        }
        cursor.close()
    }
}