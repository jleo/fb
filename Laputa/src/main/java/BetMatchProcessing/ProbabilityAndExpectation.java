package BetMatchProcessing;

import HandicapProcessing.MatchInformation;

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-2
 * Time: 下午12:03
 * To change this template use File | Settings | File Templates.
 */
public interface ProbabilityAndExpectation {
    ProbabilityAndExpectationValue get(MatchInformation matchInformation);
}
