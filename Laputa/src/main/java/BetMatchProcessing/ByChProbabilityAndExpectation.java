package BetMatchProcessing;

import HandicapProcessing.MatchInformation;

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-2
 * Time: 下午12:15
 * To change this template use File | Settings | File Templates.
 */
public class ByChProbabilityAndExpectation implements ProbabilityAndExpectation {
    private final ProbabilityAndExpectationFinder probabilityAndExpectationFinder;

    public ByChProbabilityAndExpectation(ProbabilityAndExpectationFinder probabilityAndExpectationFinder) {
        this.probabilityAndExpectationFinder = probabilityAndExpectationFinder;
    }

    public ProbabilityAndExpectationValue get(MatchInformation matchInformation) {
        int ch = matchInformation.getCh();
        return probabilityAndExpectationFinder.findByCh(ch);  //To change body of implemented methods use File | Settings | File Templates.
    }
}
