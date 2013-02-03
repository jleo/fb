package BetMatchProcessing;

import HandicapProcessing.HandicapProcessing;

/**
 * Created with IntelliJ IDEA.
 * User: snowhyzhang
 * Date: 13-1-16
 * Time: 上午12:06
 * To change this template use File | Settings | File Templates.
 */
public interface iBetMatchProcessing {

    public void setCollection(String collection);

    public int betMatch(ProbabilityAndExpectation probabilityAndExpectation, double baseMoney, HandicapProcessing handicapProcessing);

    public int betMatch(double minExpectation, double minProbability, double baseMoney, HandicapProcessing handicapProcessing);

}
