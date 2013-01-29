package matchrecommend;

import HandicapProcessing.HandicapProcessing;

/**
 * Created with IntelliJ IDEA.
 * User: snowhyzhang
 * Date: 13-1-29
 * Time: 下午4:03
 * To change this template use File | Settings | File Templates.
 */
public interface iMatchRecommend {

    public int getRecommendMatch(double minProbability, double minExpectation, HandicapProcessing handicapProcessing);

}
