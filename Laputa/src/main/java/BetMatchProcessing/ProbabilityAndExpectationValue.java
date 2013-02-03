package BetMatchProcessing;

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-2
 * Time: 下午12:24
 * To change this template use File | Settings | File Templates.
 */
public class ProbabilityAndExpectationValue {
    double probability;
    double expectation;

    public ProbabilityAndExpectationValue(double probability, double expectation) {
        this.probability = probability;
        this.expectation = expectation;
    }

    public double getExpectation() {
        return expectation;
    }

    public void setExpectation(double expectation) {
        this.expectation = expectation;
    }

    public double getProbability() {

        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }
}
