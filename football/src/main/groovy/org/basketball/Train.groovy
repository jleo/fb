package org.basketball
/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-19
 * Time: 下午10:09
 * To change this template use File | Settings | File Templates.
 */
class Train {
    public static void main(String[] args) {

        JooneScoreTrend joone = new JooneScoreTrend();


        joone.cycle = args[0] as int
        joone.layer = args[1] as int
        joone.row = args[2] as int
        joone.learnRate = args[3] as double
        joone.mon = args[4] as double
        joone.useRProp = args[5] as boolean
        String outputName = args[6]
        joone.hiddenLayerClass = args[7]
        if (new File(args[6]).exists()) {
            def nn = joone.restoreNeuralNet(args[6])
            joone.initNeuralNet(nn)
        } else {
            joone.initNeuralNet();
        }

        FileInputStream stream = new FileInputStream("train");
        ObjectInputStream out = new ObjectInputStream(stream);
        def allTraining = out.readObject()
        out.close()

        stream = new FileInputStream("real");
        out = new ObjectInputStream(stream);
        def allReal = out.readObject()
        out.close()



        joone.train(allTraining, allReal);
        joone.saveNeuralNet(outputName);
    }
}
