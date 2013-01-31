package matchrecommend;

import HandicapProcessing.HandicapProcessing;

/**
 * Created with IntelliJ IDEA.
 * User: snowhyzhang
 * Date: 13-1-29
 * Time: 下午4:30
 * To change this template use File | Settings | File Templates.
 */
public class MatchRecommendGuarantee extends MatchRecommendBasic{

    public int getRecommendMatch(double minProbability, double minExpectation, HandicapProcessing handicapProcessing) {
        if ((handicapProcessing.getWinProbability() + handicapProcessing.getWinHalfProbability()  +
                handicapProcessing.getDrawProbability() ) > minProbability){
            saveRecommendMatch(handicapProcessing.getMatchInformation().getMatchId(), 0,
                    handicapProcessing.getMatchInformation().getCid(),
                    handicapProcessing.getMinRate(0, minExpectation, "guarantee"));
            return 0;
        } else if ((handicapProcessing.getLoseProbability() + handicapProcessing.getLoseHalfProbability() +
                handicapProcessing.getDrawProbability()) > minProbability){
            saveRecommendMatch(handicapProcessing.getMatchInformation().getMatchId(), 1,
                    handicapProcessing.getMatchInformation().getCid(),
                    handicapProcessing.getMinRate(1, minExpectation, "guarantee"));
            return 0;
        }
        return  -1;
    }
}
