package HandicapProcessing;

import BasicDataProcessing.BasicData;
import BasicDataProcessing.BasicDataProcessing;
import BasicDataProcessing.iBasicDataProcessing;
import Util.MongoDBUtil;
import Util.Props;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: snowhyzhang
 * Date: 13-1-9
 * Time: 下午9:10
 * To change this template use File | Settings | File Templates.
 */
public class HandicapProcessing implements iHandicapProcessing {

    private iBasicDataProcessing bdp;
    private BasicData basicData;
    private MatchInformation matchInformation;

    private double winExpectation;
    private double loseExpectation;

    private double winProbability;
    private double winHalfProbability;
    private double loseProbability;
    private double loseHalfProbability;
    private double drawProbability;

    private double winFactor;
    private double pushFactor;
    private double loseFactor;
    private final double supportIncrease;
    private final int supportDegree;
    private final int supportIncreaseCount;

    public HandicapProcessing() {
        supportIncrease = Double.parseDouble(Props.getProperty("supportIncrease"));
        supportDegree = Integer.parseInt(Props.getProperty("supportDegree"));
        supportIncreaseCount = Integer.parseInt(Props.getProperty("supportIncreaseCount"));
    }

    public BasicData getBasicData() {
        return basicData;
    }

    public void setBasicData(BasicData basicData) {
        this.basicData = basicData;
    }

    public MatchInformation getMatchInformation() {
        return matchInformation;
    }

    public void setMatchInformation(MatchInformation matchInformation) {
        this.matchInformation = matchInformation;
    }

    public double getWinExpectation() {
        return winExpectation;
    }

    public void setWinExpectation(double winExpectation) {
        this.winExpectation = winExpectation;
    }

    public double getLoseExpectation() {
        return loseExpectation;
    }

    public void setLoseExpectation(double loseExpectation) {
        this.loseExpectation = loseExpectation;
    }

    public double getWinProbability() {
        return winProbability;
    }

    public void setWinProbability(double winProbability) {
        this.winProbability = winProbability;
    }

    public double getWinHalfProbability() {
        return winHalfProbability;
    }

    public void setWinHalfProbability(double winHalfProbability) {
        this.winHalfProbability = winHalfProbability;
    }

    public double getLoseProbability() {
        return loseProbability;
    }

    public void setLoseProbability(double loseProbability) {
        this.loseProbability = loseProbability;
    }

    public double getLoseHalfProbability() {
        return loseHalfProbability;
    }

    public void setLoseHalfProbability(double loseHalfProbability) {
        this.loseHalfProbability = loseHalfProbability;
    }

    public double getDrawProbability() {
        return drawProbability;
    }

    public void setDrawProbability(double drawProbability) {
        this.drawProbability = drawProbability;
    }

    public double getWinFactor() {
        return winFactor;
    }

    public void setWinFactor(double winFactor) {
        this.winFactor = winFactor;
    }

    public double getPushFactor() {
        return pushFactor;
    }

    public void setPushFactor(double pushFactor) {
        this.pushFactor = pushFactor;
    }

    public double getLoseFactor() {
        return loseFactor;
    }

    public void setLoseFactor(double loseFactor) {
        this.loseFactor = loseFactor;
    }

    public void setMatch(double win, double push, double lose, double handicap, double winRate, double loseRate,
                         String matchId, String clientId, String cid, Date date, String teamA, String teamB, int ch) {

        matchInformation = new MatchInformation(win, push, lose, handicap, winRate, loseRate, matchId, clientId, cid, date, teamA, teamB, ch);
    }

    public void setMatch(double win, double push, double lose, double handicap, double winRate, double loseRate,
                         String matchId, String clientId, String cid, String teamA, String teamB, int ch) {
        matchInformation = new MatchInformation(win, push, lose, handicap, winRate, loseRate, matchId, clientId, cid, teamA, teamB, ch);
    }

    public int getResult(boolean display) {
        clearInfo();

        bdp = new BasicDataProcessing();
        double winFactorInc = matchInformation.getWin() * supportIncrease;
        double pushFactorInc = matchInformation.getPush() * supportIncrease;
        double loseFactorInc = matchInformation.getLose() * supportIncrease;

        DBObject query = new BasicDBObject("matchId", matchInformation.getMatchId());
        MongoDBUtil.getInstance(null, null, null).remove(query, "bet");
        bdp.processBasicData(matchInformation.getWin(), matchInformation.getPush(), matchInformation.getLose(), winFactor,
                pushFactor, loseFactor);
        basicData = bdp.getBasicData();
        int increaseCount = 0;
        while (basicData.getMatchCount() < supportDegree && increaseCount < supportIncreaseCount) {
            winFactor = winFactor + winFactorInc;
            pushFactor = pushFactor + pushFactorInc;
            loseFactor = loseFactor + loseFactorInc;

            bdp.processBasicData(matchInformation.getWin(), matchInformation.getPush(), matchInformation.getLose(), winFactor,
                    pushFactor, loseFactor);
            basicData = bdp.getBasicData();
            ++increaseCount;
        }

        if (basicData.getMatchCount() < supportDegree) {
            System.out.println("The count of match is less than support degree. aborting betting on this match");
            return -1;
        }

        if (matchInformation.getHandicap() == 3) {

            winExpectation = basicData.getWin4Game() / basicData.getMatchCount() * matchInformation.getWinRate() - (basicData.getLoseMatch()
                    + basicData.getPushMatch() + basicData.getWin1Game() + basicData.getWin2Game()) / basicData.getMatchCount();
            winProbability = basicData.getWin4Game() / basicData.getMatchCount();

            loseExpectation = (basicData.getLoseMatch() + basicData.getPushMatch() + basicData.getWin1Game() + basicData.getWin2Game())
                    / basicData.getMatchCount() * matchInformation.getLoseRate() - (basicData.getWin4Game()) / basicData.getMatchCount();
            loseProbability = basicData.getLoseMatch() + basicData.getPushMatch() + basicData.getWin1Game() + basicData.getWin2Game();

            drawProbability = basicData.getWin3Game() / basicData.getMatchCount();

        } else if (matchInformation.getHandicap() == 2.75) {

            winExpectation = (basicData.getWin4Game() + basicData.getWin3Game() / 2) / basicData.getMatchCount() * matchInformation.getWinRate()
                    - (basicData.getLoseMatch() + basicData.getPushMatch() + basicData.getWin1Game() + basicData.getWin2Game()) / basicData.getMatchCount();
            winProbability = basicData.getWin4Game() / basicData.getMatchCount();
            winHalfProbability = basicData.getWin3Game() / basicData.getMatchCount();

            loseExpectation = (basicData.getLoseMatch() + basicData.getPushMatch() + basicData.getWin1Game() + basicData.getWin2Game())
                    / basicData.getMatchCount() * matchInformation.getLoseRate() - (basicData.getWin4Game() + basicData.getWin3Game() / 2) / basicData.getMatchCount();
            loseProbability = (basicData.getLoseMatch() + basicData.getPushMatch() + basicData.getWin1Game() + basicData.getWin2Game()) / basicData.getMatchCount();

        } else if (matchInformation.getHandicap() == 2.5) {

            winExpectation = (basicData.getWin3Game() + basicData.getWin4Game()) / basicData.getMatchCount() * matchInformation.getWinRate()
                    - (basicData.getLoseMatch() + basicData.getPushMatch() + basicData.getWin1Game() + basicData.getWin2Game()) / basicData.getMatchCount();
            winProbability = (basicData.getWin3Game() + basicData.getWin4Game()) / basicData.getMatchCount();

            loseExpectation = (basicData.getLoseMatch() + basicData.getPushMatch() + basicData.getWin1Game() + basicData.getWin2Game())
                    / basicData.getMatchCount() * matchInformation.getLoseRate() - (basicData.getWin4Game() + basicData.getWin3Game()) / basicData.getMatchCount();
            loseProbability = (basicData.getLoseMatch() + basicData.getPushMatch() + basicData.getWin1Game() + basicData.getWin2Game()) / basicData.getMatchCount();

        } else if (matchInformation.getHandicap() == 2.25) {

            winExpectation = (basicData.getWin3Game() + basicData.getWin4Game()) / basicData.getMatchCount() * matchInformation.getWinRate()
                    - (basicData.getLoseMatch() + basicData.getPushMatch() + basicData.getWin1Game() + basicData.getWin2Game() / 2) / basicData.getMatchCount();
            winProbability = (basicData.getWin3Game() + basicData.getWin4Game()) / basicData.getMatchCount();

            loseExpectation = (basicData.getLoseMatch() + basicData.getPushMatch() + basicData.getWin1Game() + basicData.getWin2Game() / 2)
                    / basicData.getMatchCount() * matchInformation.getLoseRate() - (basicData.getWin3Game() + basicData.getWin4Game()) / basicData.getMatchCount();
            loseProbability = (basicData.getLoseMatch() + basicData.getPushMatch() + basicData.getWin1Game()) / basicData.getMatchCount();
            loseHalfProbability = basicData.getWin2Game() / basicData.getMatchCount();

        } else if (matchInformation.getHandicap() == 2) {

            winExpectation = (basicData.getWin3Game() + basicData.getWin4Game()) / basicData.getMatchCount() * matchInformation.getWinRate()
                    - (basicData.getLoseMatch() + basicData.getPushMatch() + basicData.getWin1Game()) / basicData.getMatchCount();
            winProbability = (basicData.getWin3Game() + basicData.getWin4Game()) / basicData.getMatchCount();

            loseExpectation = (basicData.getLoseMatch() + basicData.getPushMatch() + basicData.getWin1Game()) / basicData.getMatchCount()
                    * matchInformation.getLoseRate() - (basicData.getWin3Game() + basicData.getWin4Game()) / basicData.getMatchCount();
            loseProbability = (basicData.getLoseMatch() + basicData.getPushMatch() + basicData.getWin1Game()) / basicData.getMatchCount();

            drawProbability = basicData.getWin2Game() / basicData.getMatchCount();

        } else if (matchInformation.getHandicap() == 1.75) {

            winExpectation = (basicData.getWin2Game() / 2 + basicData.getWin3Game() + basicData.getWin4Game()) / basicData.getMatchCount() * matchInformation.getWinRate()
                    - (basicData.getLoseMatch() + basicData.getPushMatch() + basicData.getWin1Game()) / basicData.getMatchCount();
            winProbability = (basicData.getWin3Game() + basicData.getWin4Game()) / basicData.getMatchCount();
            winHalfProbability = basicData.getWin2Game() / basicData.getMatchCount();

            loseExpectation = (basicData.getLoseMatch() + basicData.getPushMatch() + basicData.getWin1Game()) / basicData.getMatchCount()
                    * matchInformation.getLoseRate() - (basicData.getWin2Game() / 2 + basicData.getWin3Game() + basicData.getWin4Game()) / basicData.getMatchCount();
            loseProbability = (basicData.getLoseMatch() + basicData.getPushMatch() + basicData.getWin1Game()) / basicData.getMatchCount();


        } else if (matchInformation.getHandicap() == 1.5) {

            winExpectation = (basicData.getWin2Game() + basicData.getWin3Game() + basicData.getWin4Game()) / basicData.getMatchCount() * matchInformation.getWinRate()
                    - (basicData.getLoseMatch() + basicData.getPushMatch() + basicData.getWin1Game()) / basicData.getMatchCount();
            winProbability = (basicData.getWin2Game() + basicData.getWin3Game() + basicData.getWin4Game()) / basicData.getMatchCount();

            loseExpectation = (basicData.getLoseMatch() + basicData.getPushMatch() + basicData.getWin1Game()) / basicData.getMatchCount()
                    * matchInformation.getLoseRate() - (basicData.getWin2Game() + basicData.getWin3Game() + basicData.getWin4Game()) / basicData.getMatchCount();
            loseProbability = (basicData.getLoseMatch() + basicData.getPushMatch() + basicData.getWin1Game()) / basicData.getMatchCount();

        } else if (matchInformation.getHandicap() == 1.25) {

            winExpectation = (basicData.getWin2Game() + basicData.getWin3Game() + basicData.getWin4Game()) / basicData.getMatchCount() * matchInformation.getWinRate()
                    - (basicData.getLoseMatch() + basicData.getPushMatch() + basicData.getWin1Game() / 2) / basicData.getMatchCount();
            winProbability = (basicData.getWin2Game() + basicData.getWin3Game() + basicData.getWin4Game()) / basicData.getMatchCount();

            loseExpectation = (basicData.getLoseMatch() + basicData.getPushMatch() + basicData.getWin1Game() / 2) / basicData.getMatchCount()
                    * matchInformation.getLoseRate() - (basicData.getWin2Game() + basicData.getWin3Game() + basicData.getWin4Game()) / basicData.getMatchCount();
            loseProbability = (basicData.getLoseMatch() + basicData.getPushMatch()) / basicData.getMatchCount();
            loseHalfProbability = basicData.getWin1Game() / basicData.getMatchCount();

        } else if (matchInformation.getHandicap() == 1) {

            winExpectation = (basicData.getWin2Game() + basicData.getWin3Game() + basicData.getWin4Game()) / basicData.getMatchCount() * matchInformation.getWinRate()
                    - (basicData.getLoseMatch() + basicData.getPushMatch()) / basicData.getMatchCount();
            winProbability = (basicData.getWin2Game() + basicData.getWin3Game() + basicData.getWin4Game()) / basicData.getMatchCount();

            loseExpectation = (basicData.getLoseMatch() + basicData.getPushMatch()) / basicData.getMatchCount() * matchInformation.getLoseRate()
                    - (basicData.getWin2Game() + basicData.getWin3Game() + basicData.getWin4Game()) / basicData.getMatchCount();
            loseProbability = (basicData.getLoseMatch() + basicData.getPushMatch()) / basicData.getMatchCount();

            drawProbability = basicData.getWin1Game() / basicData.getMatchCount();

        } else if (matchInformation.getHandicap() == 0.75) {

            winExpectation = (basicData.getWin1Game() / 2 + basicData.getWin2Game() + basicData.getWin3Game() + basicData.getWin4Game()) / basicData.getMatchCount()
                    * matchInformation.getWinRate() - (basicData.getLoseMatch() + basicData.getPushMatch()) / basicData.getMatchCount();
            winProbability = (basicData.getWin2Game() + basicData.getWin3Game() + basicData.getWin4Game()) / basicData.getMatchCount();
            winHalfProbability = basicData.getWin1Game() / basicData.getMatchCount();

            loseExpectation = (basicData.getLoseMatch() + basicData.getPushMatch()) / basicData.getMatchCount() * matchInformation.getLoseRate()
                    - (basicData.getWin1Game() / 2 + basicData.getWin2Game() + basicData.getWin3Game() + basicData.getWin4Game()) / basicData.getMatchCount();
            loseProbability = (basicData.getLoseMatch() + basicData.getPushMatch()) / basicData.getMatchCount();

        } else if (matchInformation.getHandicap() == 0.5) {

            winExpectation = basicData.getWinMatch() / basicData.getMatchCount() * matchInformation.getWinRate()
                    - (basicData.getLoseMatch() + basicData.getPushMatch()) / basicData.getMatchCount();
            winProbability = basicData.getWinMatch() / basicData.getMatchCount();

            loseExpectation = (basicData.getLoseMatch() + basicData.getPushMatch()) / basicData.getMatchCount() * matchInformation.getLoseRate()
                    - (basicData.getWin1Game() + basicData.getWin2Game() + basicData.getWin3Game() + basicData.getWin4Game()) / basicData.getMatchCount();
            loseProbability = (basicData.getLoseMatch() + basicData.getPushMatch()) / basicData.getMatchCount();

        } else if (matchInformation.getHandicap() == 0.25) {

            winExpectation = basicData.getWinMatch() / basicData.getMatchCount() * matchInformation.getWinRate()
                    - (basicData.getLoseMatch() + basicData.getPushMatch() / 2) / basicData.getMatchCount();
            winProbability = basicData.getWinMatch() / basicData.getMatchCount();

            loseExpectation = (basicData.getLoseMatch() + basicData.getPushMatch() / 2) / basicData.getMatchCount() * matchInformation.getLoseRate()
                    - (basicData.getWinMatch()) / basicData.getMatchCount();
            loseProbability = basicData.getLoseMatch() / basicData.getMatchCount();
            loseHalfProbability = basicData.getPushMatch() / basicData.getMatchCount();

        } else if (matchInformation.getHandicap() == 0) {

            winExpectation = basicData.getWinMatch() / basicData.getMatchCount() * matchInformation.getWinRate() - basicData.getLoseMatch()
                    / basicData.getMatchCount();
            winProbability = basicData.getWinMatch() / basicData.getMatchCount();

            loseExpectation = basicData.getLoseMatch() / basicData.getMatchCount() * matchInformation.getLoseRate() - basicData.getWinMatch()
                    / basicData.getMatchCount();
            loseProbability = basicData.getLoseMatch() / basicData.getMatchCount();

            drawProbability = basicData.getPushGame() / basicData.getMatchCount();

        } else if (matchInformation.getHandicap() == -0.25) {

            winExpectation = (basicData.getWinMatch() + basicData.getPushMatch() / 2) / basicData.getMatchCount() * matchInformation.getWinRate()
                    - basicData.getLoseMatch() / basicData.getMatchCount();
            winProbability = basicData.getWinMatch() / basicData.getMatchCount();
            winHalfProbability = basicData.getPushMatch() / basicData.getMatchCount();

            loseExpectation = basicData.getLoseMatch() / basicData.getMatchCount() * matchInformation.getLoseRate()
                    - (basicData.getWinMatch() + basicData.getPushMatch() / 2) / basicData.getMatchCount();
            loseProbability = basicData.getLoseMatch() / basicData.getMatchCount();

        } else if (matchInformation.getHandicap() == -0.5) {

            winExpectation = (basicData.getWinMatch() + basicData.getPushMatch()) / basicData.getMatchCount() * matchInformation.getWinRate()
                    - basicData.getLoseMatch() / basicData.getMatchCount();
            winProbability = (basicData.getWinMatch() + basicData.getPushMatch()) / basicData.getMatchCount();

            loseExpectation = basicData.getLoseMatch() / basicData.getMatchCount() * matchInformation.getLoseRate()
                    - (basicData.getWinMatch() + basicData.getPushMatch()) / basicData.getMatchCount();
            loseProbability = basicData.getLoseMatch() / basicData.getMatchCount();


        } else if (matchInformation.getHandicap() == -0.75) {

            winExpectation = (basicData.getWinMatch() + basicData.getPushMatch()) / basicData.getMatchCount() * matchInformation.getWinRate()
                    - (basicData.getLose1Game() / 2 + basicData.getLose2Game() + basicData.getLose3Game() + basicData.getLose4Game()) / basicData.getMatchCount();
            winProbability = (basicData.getWinMatch() + basicData.getPushMatch()) / basicData.getMatchCount();

            loseExpectation = (basicData.getLose1Game() / 2 + basicData.getLose2Game() + basicData.getLose3Game() + basicData.getLose4Game()) / basicData.getMatchCount()
                    * matchInformation.getLoseRate() - (basicData.getWinMatch() + basicData.getPushMatch()) / basicData.getMatchCount();
            loseProbability = (basicData.getLoseMatch() - basicData.getLose1Game()) / basicData.getMatchCount();
            loseHalfProbability = basicData.getLose1Game() / basicData.getMatchCount();

        } else if (matchInformation.getHandicap() == -1) {

            winExpectation = (basicData.getWinMatch() + basicData.getPushMatch()) / basicData.getMatchCount() * matchInformation.getWinRate()
                    - (basicData.getLose2Game() + basicData.getLose3Game() + basicData.getLose4Game()) / basicData.getMatchCount();
            winProbability = (basicData.getWinMatch() + basicData.getPushMatch()) / basicData.getMatchCount();

            loseExpectation = (basicData.getLose2Game() + basicData.getLose3Game() + basicData.getLose4Game()) / basicData.getMatchCount()
                    * matchInformation.getLoseRate() - (basicData.getWinMatch() + basicData.getPushMatch()) / basicData.getMatchCount();
            loseProbability = (basicData.getLose2Game() + basicData.getLose3Game() + basicData.getLose4Game()) / basicData.getMatchCount();

            drawProbability = basicData.getLose1Game() / basicData.getMatchCount();

        } else if (matchInformation.getHandicap() == -1.25) {

            winExpectation = (basicData.getWinMatch() + basicData.getPushMatch() + basicData.getLose1Game() / 2) / basicData.getMatchCount() * matchInformation.getWinRate()
                    - (basicData.getLose2Game() + basicData.getLose3Game() + basicData.getLose4Game()) / basicData.getMatchCount();
            winProbability = (basicData.getWinMatch() + basicData.getPushMatch()) / basicData.getMatchCount();
            winHalfProbability = basicData.getLose1Game() / basicData.getMatchCount();

            loseExpectation = (basicData.getLose2Game() + basicData.getLose3Game() + basicData.getLose4Game()) / basicData.getMatchCount() * matchInformation.getLoseRate()
                    - (basicData.getWinMatch() + basicData.getPushMatch() + basicData.getLose1Game() / 2) / basicData.getMatchCount();
            loseProbability = (basicData.getLose2Game() + basicData.getLose3Game() + basicData.getLose4Game()) / basicData.getMatchCount();

        } else if (matchInformation.getHandicap() == -1.5) {

            winExpectation = (basicData.getWinMatch() + basicData.getPushMatch() + basicData.getLose1Game()) / basicData.getMatchCount() * matchInformation.getWinRate()
                    - (basicData.getLose2Game() + basicData.getLose3Game() + basicData.getLose4Game()) / basicData.getMatchCount();
            winProbability = (basicData.getWinMatch() + basicData.getPushMatch() + basicData.getLose1Game()) / basicData.getMatchCount();

            loseExpectation = (basicData.getLose2Game() + basicData.getLose3Game() + basicData.getLose4Game()) / basicData.getMatchCount() * matchInformation.getLoseRate()
                    - (basicData.getWinMatch() + basicData.getPushMatch() + basicData.getLose1Game()) / basicData.getMatchCount();
            loseProbability = (basicData.getLose2Game() + basicData.getLose3Game() + basicData.getLose4Game()) / basicData.getMatchCount();

        } else if (matchInformation.getHandicap() == -1.75) {

            winExpectation = (basicData.getWinMatch() + basicData.getPushMatch() + basicData.getLose1Game()) / basicData.getMatchCount() * matchInformation.getWinRate()
                    - (basicData.getLose2Game() / 2 + basicData.getLose3Game() + basicData.getLose4Game()) / basicData.getMatchCount();
            winProbability = (basicData.getWinMatch() + basicData.getPushMatch() + basicData.getLose1Game());

            loseExpectation = (basicData.getLose2Game() / 2 + basicData.getLose3Game() + basicData.getLose4Game()) / basicData.getMatchCount() * matchInformation.getLoseRate()
                    - (basicData.getWinMatch() + basicData.getPushMatch() + basicData.getLose1Game()) / basicData.getMatchCount();
            loseProbability = (basicData.getLose3Game() + basicData.getLose4Game()) / basicData.getMatchCount();
            loseHalfProbability = basicData.getLose2Game() / basicData.getMatchCount();

        } else if (matchInformation.getHandicap() == -2) {

            winExpectation = (basicData.getWinMatch() + basicData.getPushMatch() + basicData.getLose1Game()) / basicData.getMatchCount() * matchInformation.getWinRate()
                    - (basicData.getLose3Game() + basicData.getLose4Game()) / basicData.getMatchCount();
            winProbability = (basicData.getWinMatch() + basicData.getPushMatch() + basicData.getLose1Game()) / basicData.getMatchCount();

            loseExpectation = (basicData.getLose3Game() + basicData.getLose4Game()) / basicData.getMatchCount() * matchInformation.getLoseRate()
                    - (basicData.getWinMatch() + basicData.getPushMatch() + basicData.getLose1Game()) / basicData.getMatchCount();
            loseProbability = (basicData.getLose3Game() + basicData.getLose4Game()) / basicData.getMatchCount();

            drawProbability = basicData.getLose2Game() / basicData.getMatchCount();

        } else if (matchInformation.getHandicap() == -2.25) {

            winExpectation = (basicData.getWinMatch() + basicData.getPushMatch() + basicData.getLose1Game() + basicData.getLose2Game() / 2) / basicData.getMatchCount()
                    * matchInformation.getWinRate() - (basicData.getLose3Game() + basicData.getLose4Game()) / basicData.getMatchCount();
            winProbability = (basicData.getWinMatch() + basicData.getPushMatch() + basicData.getLose1Game()) / basicData.getMatchCount();
            winHalfProbability = basicData.getLose2Game() / basicData.getMatchCount();

            loseExpectation = (basicData.getLose3Game() + basicData.getLose4Game()) / basicData.getMatchCount() * matchInformation.getLoseRate()
                    - (basicData.getWinMatch() + basicData.getPushMatch() + basicData.getLose1Game() + basicData.getLose2Game() / 2) / basicData.getMatchCount();
            loseProbability = (basicData.getLose3Game() + basicData.getLose4Game()) / basicData.getMatchCount();

        } else if (matchInformation.getHandicap() == -2.5) {

            winExpectation = (basicData.getWinMatch() + basicData.getPushMatch() + basicData.getLose1Game() + basicData.getLose2Game()) / basicData.getMatchCount()
                    * matchInformation.getWinRate() - (basicData.getLose3Game() + basicData.getLose4Game()) / basicData.getMatchCount();
            winProbability = (basicData.getWinMatch() + basicData.getPushMatch() + basicData.getLose1Game() + basicData.getLose2Game()) / basicData.getMatchCount();

            loseExpectation = (basicData.getLose3Game() + basicData.getLose4Game()) / basicData.getMatchCount() * matchInformation.getLoseRate()
                    - (basicData.getWinMatch() + basicData.getPushMatch() + basicData.getLose1Game() + basicData.getLose2Game()) / basicData.getMatchCount();
            loseProbability = (basicData.getLose3Game() + basicData.getLose4Game()) / basicData.getMatchCount();

        } else if (matchInformation.getHandicap() == -2.75) {

            winExpectation = (basicData.getWinMatch() + basicData.getPushMatch() + basicData.getLose1Game() + basicData.getLose2Game()) / basicData.getMatchCount()
                    * matchInformation.getWinRate() - (basicData.getLose3Game() / 2 + basicData.getLose4Game()) / basicData.getMatchCount();
            winProbability = (basicData.getWinMatch() + basicData.getPushMatch() + basicData.getLose1Game() + basicData.getLose2Game()) / basicData.getMatchCount();

            loseExpectation = (basicData.getLose3Game() / 2 + basicData.getLose4Game()) / basicData.getMatchCount() * matchInformation.getLoseRate()
                    - (basicData.getWinMatch() + basicData.getPushMatch() + basicData.getLose1Game() + basicData.getLose2Game()) / basicData.getMatchCount();
            loseProbability = basicData.getLose4Game() / basicData.getMatchCount();
            loseHalfProbability = basicData.getLose3Game() / basicData.getMatchCount();

        } else if (matchInformation.getHandicap() == -3) {

            winExpectation = (basicData.getWinMatch() + basicData.getPushMatch() + basicData.getLose1Game() + basicData.getLose2Game()) / basicData.getMatchCount()
                    * matchInformation.getWinRate() - +basicData.getLose4Game() / basicData.getMatchCount();
            winProbability = (basicData.getWinMatch() + basicData.getPushMatch() + basicData.getLose1Game() + basicData.getLose2Game()) / basicData.getMatchCount();

            loseExpectation = basicData.getLose4Game() / basicData.getMatchCount() * matchInformation.getLoseRate()
                    - (basicData.getWinMatch() + basicData.getPushMatch() + basicData.getLose1Game() + basicData.getLose2Game()) / basicData.getMatchCount();
            loseProbability = basicData.getLose4Game() / basicData.getMatchCount();
        }

        if (display) {
            displayResult();
        }

        return 0;
    }

    public double getMinRate(int betOn, double minExpectation) {
        //betOn 0:主 1:客
        //Probability * rate - (1 - Probability) = expectation
        if (betOn == 0){
            return ((minExpectation + 1) / winProbability - 1);
        } else if (betOn == 1){
            return ((minExpectation + 1) / loseProbability - 1);
        }
        return -1;
    }


    private void clearInfo() {
        winExpectation = 0;
        loseExpectation = 0;

        winProbability = 0;
        winHalfProbability = 0;
        loseProbability = 0;
        loseHalfProbability = 0;
        drawProbability = 0;

        winFactor = 0;
        pushFactor = 0;
        loseFactor = 0;

    }

    public void saveMatchResult(String matchName, String betWinOrLose, String resultWinOrLose, double betMoney, double resultGain) {
        System.out.println("Saving Match: " + matchName);

        if (isSaved(matchName)) {
            System.out.println("Match was saved. aborting...");
            return;
        }

        String mongoDBHost = Props.getProperty("MongoDBHost");
        String mongoDBPort = Props.getProperty("MongoDBPort");
        String mongoDBName = Props.getProperty("MongoDBName");
        String collectionName = Props.getProperty("MatchStatistics");

        DBObject insertionQuery = new BasicDBObject();
        insertionQuery.put("Win", matchInformation.getWin());
        insertionQuery.put("push", matchInformation.getPush());
        insertionQuery.put("lose", matchInformation.getLose());
        insertionQuery.put("handicap", matchInformation.getHandicap());
        insertionQuery.put("winRate", matchInformation.getWinRate());
        insertionQuery.put("loseRate", matchInformation.getLoseRate());
        insertionQuery.put("winFactor", winFactor);
        insertionQuery.put("pushFactor", pushFactor);
        insertionQuery.put("loseFactor", loseFactor);

        insertionQuery.put("matchCount", basicData.getMatchCount());
        insertionQuery.put("winExpectation", winExpectation);
        insertionQuery.put("winProbability", winProbability);
        insertionQuery.put("winHalfProbability", winHalfProbability);
        insertionQuery.put("loseExpectation", loseExpectation);
        insertionQuery.put("loseProbability", loseProbability);
        insertionQuery.put("loseHalfProbability", loseHalfProbability);
        insertionQuery.put("drawProbability", drawProbability);

        insertionQuery.put("matchName", matchName);
        insertionQuery.put("betWinOrLose", betWinOrLose);
        insertionQuery.put("resultWinOrLose", resultWinOrLose);
        insertionQuery.put("betMoney", betMoney);
        insertionQuery.put("resultGain", resultGain);

        MongoDBUtil dbUtil = MongoDBUtil.getInstance(mongoDBHost, mongoDBPort, mongoDBName);
        dbUtil.insert(insertionQuery, collectionName);

        System.out.println("Saved Match:" + matchName);
    }

    private void displayResult() {

        System.out.println("Match:\t" + basicData.getMatchCount());
        System.out.println("Handicap:\t" + matchInformation.getHandicap());

        DecimalFormat df = new DecimalFormat("#.0000");

        System.out.println("Win Expectation:\t" + df.format(winExpectation * 100) + "%\t" + winExpectation);
        System.out.println("Win Probability:\t" + df.format(winProbability * 100) + "%");
        System.out.println("Win Half Probability:\t" + df.format(winHalfProbability * 100) + "%");

        System.out.println("Lose Expectation:\t" + df.format(loseExpectation * 100) + "%\t" + loseExpectation);
        System.out.println("Lose Probability:\t" + df.format(loseProbability * 100) + "%");
        System.out.println("Lose Half Probability:\t" + df.format(loseHalfProbability * 100) + "%");

        System.out.println("Draw Probability:\t" + df.format(drawProbability * 100) + "%");
    }

    private boolean isSaved(String matchName) {
        String mongoDBHost = Props.getProperty("MongoDBHost");
        String mongoDBPort = Props.getProperty("MongoDBPort");
        String mongoDBName = Props.getProperty("MongoDBName");
        String collectionName = Props.getProperty("MatchStatistics");

        DBObject query = new BasicDBObject("matchName", matchName);

        MongoDBUtil dbUtil = MongoDBUtil.getInstance(mongoDBHost, mongoDBPort, mongoDBName);
        DBObject result = dbUtil.findOne(query, collectionName);

        if (result == null)
            return false;

        return true;

    }
}
