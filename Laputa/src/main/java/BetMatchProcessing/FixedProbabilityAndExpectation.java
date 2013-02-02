package BetMatchProcessing;

import HandicapProcessing.MatchInformation;

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-2
 * Time: 上午11:59
 * To change this template use File | Settings | File Templates.
 */
public class FixedProbabilityAndExpectation implements ProbabilityAndExpectation {
    double probability;
    double expectation;

    public FixedProbabilityAndExpectation(double probability, double expectation) {
        this.probability = probability;
        this.expectation = expectation;
    }


    public void setProbability(double probability) {
        this.probability = probability;
    }


    public void setExpectation(double expectation) {
        this.expectation = expectation;
    }

    public ProbabilityAndExpectationValue get(MatchInformation matchInformation) {
        return new ProbabilityAndExpectationValue(probability, expectation);
    }
}
