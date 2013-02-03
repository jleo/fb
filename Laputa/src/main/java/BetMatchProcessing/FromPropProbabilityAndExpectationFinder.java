package BetMatchProcessing;

import Util.Props;

/**
 * Created with IntelliJ IDEA.
 * User: jleo
 * Date: 13-2-2
 * Time: 下午12:28
 * To change this template use File | Settings | File Templates.
 */
public class FromPropProbabilityAndExpectationFinder implements ProbabilityAndExpectationFinder {

    public ProbabilityAndExpectationValue findByCh(int ch) {
        String tuple = Props.getProperty("ch" + ch);
        if (tuple == null)
            return null;


        String[] tuples = tuple.split(",");

        return new ProbabilityAndExpectationValue(Double.parseDouble(tuples[1]), Double.parseDouble(tuples[0]));
    }
}
