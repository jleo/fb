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
        joone.initNeuralNet();

        FileInputStream stream = new FileInputStream("train");
        ObjectInputStream out = new ObjectInputStream(stream);
        def allTraining = out.readObject()
        out.close()

        stream = new FileInputStream("real");
        out = new ObjectInputStream(stream);
        def allReal = out.readObject()
        out.close()


        joone.train(allTraining, allReal);
        joone.saveNeuralNet("trained");
    }
}
