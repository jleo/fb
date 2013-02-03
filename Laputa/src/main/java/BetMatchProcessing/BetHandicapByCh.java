package BetMatchProcessing;

import HandicapProcessing.HandicapProcessing;

/**
 * Created with IntelliJ IDEA.
 * User: snowhyzhang
 * Date: 13-2-3
 * Time: 下午1:42
 * To change this template use File | Settings | File Templates.
 */
public class BetHandicapByCh extends BetMatchBasic{

    public BetHandicapByCh(BetMatchProcessor betMatchBatchProcessor, boolean upsert) {
        super(betMatchBatchProcessor, upsert);
    }

    public int betMatch(ProbabilityAndExpectation probabilityAndExpectation, double baseMoney, HandicapProcessing handicapProcessing) {
        return -1;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int betMatch(double minExpectation, double minProbability, double baseMoney, HandicapProcessing handicapProcessing) {
        int ch = Math.abs(handicapProcessing.getMatchInformation().getCh());
        String aid = "Bych" + String.valueOf(minExpectation) + String.valueOf(minProbability);
        if ((ch % 4) == 0){
            if (handicapProcessing.getWinExpectation() > minExpectation &&
                    (handicapProcessing.getWinProbability() + handicapProcessing.getDrawProbability() * 0.5) > minProbability){
                betOnMatch(handicapProcessing.getMatchInformation().getMatchId(),
                        handicapProcessing.getMatchInformation().getCid(),
                        handicapProcessing.getMatchInformation().getClientId(),
                        0, baseMoney, aid, handicapProcessing.getWinExpectation(),
                        (handicapProcessing.getWinProbability() + handicapProcessing.getDrawProbability() * 0.5),
                        handicapProcessing.getMatchInformation().getMatchTime(),
                        handicapProcessing.getMatchInformation().getTeamA(),
                        handicapProcessing.getMatchInformation().getTeamB(),
                        handicapProcessing.getMatchInformation().getCh(),
                        handicapProcessing.getMatchInformation().getWinRate(),
                        handicapProcessing.getMatchInformation().getLoseRate());
                return 0;
            } else if (handicapProcessing.getLoseExpectation() > minExpectation &&
                    (handicapProcessing.getLoseProbability() + handicapProcessing.getDrawProbability()  * 0.5) > minProbability){
                betOnMatch(handicapProcessing.getMatchInformation().getMatchId(),
                        handicapProcessing.getMatchInformation().getCid(),
                        handicapProcessing.getMatchInformation().getClientId(),
                        1, baseMoney, aid, handicapProcessing.getLoseExpectation(),
                        (handicapProcessing.getLoseProbability() + handicapProcessing.getDrawProbability() * 0.5),
                        handicapProcessing.getMatchInformation().getMatchTime(),
                        handicapProcessing.getMatchInformation().getTeamA(),
                        handicapProcessing.getMatchInformation().getTeamB(),
                        handicapProcessing.getMatchInformation().getCh(),
                        handicapProcessing.getMatchInformation().getWinRate(),
                        handicapProcessing.getMatchInformation().getLoseRate());
                return 0;
            }
        } else if ((ch % 4) == 1 || (ch % 4) == 3){
            if (handicapProcessing.getWinExpectation() > minExpectation &&
                    (handicapProcessing.getWinProbability() + handicapProcessing.getWinHalfProbability() * 0.75) > minProbability){
                betOnMatch(handicapProcessing.getMatchInformation().getMatchId(),
                        handicapProcessing.getMatchInformation().getCid(),
                        handicapProcessing.getMatchInformation().getClientId(),
                        0, baseMoney, aid, handicapProcessing.getWinExpectation(),
                        (handicapProcessing.getWinProbability() + handicapProcessing.getWinHalfProbability() * 0.75),
                        handicapProcessing.getMatchInformation().getMatchTime(),
                        handicapProcessing.getMatchInformation().getTeamA(),
                        handicapProcessing.getMatchInformation().getTeamB(),
                        handicapProcessing.getMatchInformation().getCh(),
                        handicapProcessing.getMatchInformation().getWinRate(),
                        handicapProcessing.getMatchInformation().getLoseRate());
                return 0;
            } else if (handicapProcessing.getLoseExpectation() > minExpectation &&
                    (handicapProcessing.getLoseProbability() + handicapProcessing.getLoseHalfProbability() * 0.75) > minProbability){
                betOnMatch(handicapProcessing.getMatchInformation().getMatchId(),
                        handicapProcessing.getMatchInformation().getCid(),
                        handicapProcessing.getMatchInformation().getClientId(),
                        1, baseMoney, aid, handicapProcessing.getLoseExpectation(),
                        (handicapProcessing.getLoseProbability() + handicapProcessing.getLoseHalfProbability() * 0.75),
                        handicapProcessing.getMatchInformation().getMatchTime(),
                        handicapProcessing.getMatchInformation().getTeamA(),
                        handicapProcessing.getMatchInformation().getTeamB(),
                        handicapProcessing.getMatchInformation().getCh(),
                        handicapProcessing.getMatchInformation().getWinRate(),
                        handicapProcessing.getMatchInformation().getLoseRate());
                return 0;
            }
        }  else if ((ch % 4) == 2){
            if (handicapProcessing.getWinExpectation() > minExpectation &&
                    handicapProcessing.getWinProbability() > minProbability){
                betOnMatch(handicapProcessing.getMatchInformation().getMatchId(),
                        handicapProcessing.getMatchInformation().getCid(),
                        handicapProcessing.getMatchInformation().getClientId(),
                        0, baseMoney, aid, handicapProcessing.getWinExpectation(),
                        handicapProcessing.getWinProbability(),
                        handicapProcessing.getMatchInformation().getMatchTime(),
                        handicapProcessing.getMatchInformation().getTeamA(),
                        handicapProcessing.getMatchInformation().getTeamB(),
                        handicapProcessing.getMatchInformation().getCh(),
                        handicapProcessing.getMatchInformation().getWinRate(),
                        handicapProcessing.getMatchInformation().getLoseRate());
                return 0;
            } else if (handicapProcessing.getLoseExpectation() > minExpectation &&
                    handicapProcessing.getLoseProbability() > minProbability){
                betOnMatch(handicapProcessing.getMatchInformation().getMatchId(),
                        handicapProcessing.getMatchInformation().getCid(),
                        handicapProcessing.getMatchInformation().getClientId(),
                        1, baseMoney, aid, handicapProcessing.getLoseExpectation(),
                        handicapProcessing.getLoseProbability(),
                        handicapProcessing.getMatchInformation().getMatchTime(),
                        handicapProcessing.getMatchInformation().getTeamA(),
                        handicapProcessing.getMatchInformation().getTeamB(),
                        handicapProcessing.getMatchInformation().getCh(),
                        handicapProcessing.getMatchInformation().getWinRate(),
                        handicapProcessing.getMatchInformation().getLoseRate());
                return 0;
            }
        }

        return -1;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
